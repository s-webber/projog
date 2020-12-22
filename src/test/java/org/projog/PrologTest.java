/*
 * Copyright 2018 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.COMPILATION_DISABLED_PROPERTIES;
import static org.projog.TestUtils.COMPILATION_ENABLED_PROPERTIES;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.projog.api.Projog;
import org.projog.core.ProjogProperties;
import org.projog.core.SpyPoints.SpyPointEvent;
import org.projog.core.SpyPoints.SpyPointExitEvent;
import org.projog.core.event.ProjogListener;
import org.projog.test.ProjogTestExtractor;
import org.projog.test.ProjogTestExtractorConfig;
import org.projog.test.ProjogTestRunner;
import org.projog.test.ProjogTestRunner.ProjogSupplier;
import org.projog.test.ProjogTestRunner.TestResults;

/** Uses {@code projog-test} to run Prolog code and compare the results against expectations. */
public class PrologTest {
   private static final File EXTRACTED_PROLOG_TESTS_DIR = new File("target/prolog-tests-extracted-from-java");
   private static final File SOURCE_PROLOG_TESTS_DIR = new File("src/test/prolog");

   @BeforeClass
   public static void extract() {
      ProjogTestExtractorConfig config = new ProjogTestExtractorConfig();
      config.setPrologTestsDirectory(EXTRACTED_PROLOG_TESTS_DIR);
      config.setRequireJavadoc(true);
      config.setRequireTest(true);
      config.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.getPath().replace(File.separatorChar, '.').contains("org.projog.core.function");
         }
      });
      ProjogTestExtractor.extractTests(config);
   }

   @Test
   public void prologTestsInterpretedMode() {
      assertSuccess(SOURCE_PROLOG_TESTS_DIR, compilationDisabledProjog());
   }

   @Ignore
   @Test
   public void prologTestsCompiledMode() {
      assertSuccess(SOURCE_PROLOG_TESTS_DIR, compilationEnabledProjog());
   }

   @Test
   public void extractedTestsInterpretedMode() {
      assertSuccess(EXTRACTED_PROLOG_TESTS_DIR, compilationDisabledProjog());
   }

   @Ignore
   @Test
   public void extractedTestsCompiledMode() {
      assertSuccess(EXTRACTED_PROLOG_TESTS_DIR, compilationEnabledProjog());
   }

   /** Test that if a user-defined predicate is too large to compile to Java then Projog reverts to interpreted mode. */
   @Test
   public void predicateTooLargeToCompileToJava() throws FileNotFoundException {
      // write to the file system a script containing a predicate that is too large to compile to Java
      File source = new File("target/predicateTooLargeToCompileToJava.pl");
      try (PrintWriter pw = new PrintWriter(source)) {
         for (int i = 1; i <= 2000; i++) {
            pw.println("test(X,Y):-Y is X+" + i + ".");
         }
         pw.println("%QUERY test(7,Y)");
         for (int i = 1; i <= 2000; i++) {
            pw.println("%ANSWER Y=" + (7 + i));
         }
      }

      // create Projog instance with an Observer so can check the events to confirm that the predicate cannot be compiled
      final List<String> events = new ArrayList<>();
      final ProjogListener listener = new ProjogListener() {
         @Override
         public void onInfo(String message) {
            add(message);
         }

         @Override
         public void onWarn(String message) {
            add(message);
         }

         @Override
         public void onRedo(SpyPointEvent event) {
            add(event);
         }

         @Override
         public void onFail(SpyPointEvent event) {
            add(event);
         }

         @Override
         public void onExit(SpyPointExitEvent event) {
            add(event);
         }

         @Override
         public void onCall(SpyPointEvent event) {
            add(event);
         }

         private void add(Object message) {
            events.add(message.toString());
         }
      };
      ProjogSupplier projogSupplier = new ProjogSupplier() {
         @Override
         public Projog get() {
            return new Projog(COMPILATION_ENABLED_PROPERTIES, listener);
         }
      };

      // assert tests pass
      assertSuccess(source, projogSupplier);

      // assert that notification was received that Projog reverted to interpreted mode
      assertEquals(events.toString(), 3, events.size());
      assertEquals("Reading prolog source in: projog-bootstrap.pl from classpath", events.get(0));
      assertEquals("Reading prolog source in: target" + File.separator + "predicateTooLargeToCompileToJava.pl from file system", events.get(1));
      assertEquals("Caught exception while compiling test/2 to Java so will revert to operating in interpreted mode for this predicate.", events.get(2));
   }

   private void assertSuccess(File scriptsDir, ProjogSupplier projogSupplier) {
      TestResults results = ProjogTestRunner.runTests(scriptsDir, projogSupplier);
      System.out.println(results.getSummary());
      results.assertSuccess();
   }

   private ProjogSupplier compilationDisabledProjog() {
      assertFalse(COMPILATION_DISABLED_PROPERTIES.isRuntimeCompilationEnabled());
      return projog(COMPILATION_DISABLED_PROPERTIES);
   }

   private ProjogSupplier compilationEnabledProjog() {
      assertTrue(COMPILATION_ENABLED_PROPERTIES.isRuntimeCompilationEnabled());
      return projog(COMPILATION_ENABLED_PROPERTIES);
   }

   private ProjogSupplier projog(final ProjogProperties properties) {
      return new ProjogSupplier() {
         @Override
         public Projog get() {
            return new Projog(properties);
         }
      };
   }
}
