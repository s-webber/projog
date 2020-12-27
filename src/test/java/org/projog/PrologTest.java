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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.projog.api.Projog;
import org.projog.core.event.ProjogListener;
import org.projog.core.event.SpyPoints.SpyPointEvent;
import org.projog.core.event.SpyPoints.SpyPointExitEvent;
import org.projog.test.ProjogTestExtractor;
import org.projog.test.ProjogTestExtractorConfig;
import org.projog.test.ProjogTestRunner;
import org.projog.test.ProjogTestRunner.ProjogSupplier;
import org.projog.test.ProjogTestRunner.TestResults;

/** Uses {@code projog-test} to run Prolog code and compare the results against expectations. */
public class PrologTest {
   private static final File SOURCE_PROLOG_TESTS_DIR = new File("src/test/prolog");
   private static final String BUILTIN_PREDICATES_PACKAGE = "org.projog.core.predicate.builtin";
   private static final File EXTRACTED_PREDICATES_TESTS_DIR = new File("target/prolog-predicate-tests-extracted-from-java");
   private static final String BUILTIN_OPERATORS_PACKAGE = "org.projog.core.math.builtin";
   private static final File EXTRACTED_OPERATORS_TESTS_DIR = new File("target/prolog-operator-tests-extracted-from-java");

   @Test
   public void prologTests() {
      assertSuccess(SOURCE_PROLOG_TESTS_DIR);
   }

   @Test
   public void extractedPredicateTests() {
      extract(EXTRACTED_PREDICATES_TESTS_DIR, BUILTIN_PREDICATES_PACKAGE);
      assertSuccess(EXTRACTED_PREDICATES_TESTS_DIR);
   }

   @Test
   public void extractedOperatorTests() {
      extract(EXTRACTED_OPERATORS_TESTS_DIR, BUILTIN_OPERATORS_PACKAGE);
      assertSuccess(EXTRACTED_OPERATORS_TESTS_DIR);
   }

   /** Test that a user-defined predicate with many clauses can be interpreted. */
   @Test
   public void predicateWithManyClauses() throws FileNotFoundException {
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

      // assert tests pass
      assertSuccess(source, new ProjogSupplier() {
         @Override
         public Projog get() {
            return new Projog(listener);
         }
      });

      // assert that notifications
      assertEquals(events.toString(), 2, events.size());
      assertEquals("Reading prolog source in: projog-bootstrap.pl from classpath", events.get(0));
      assertEquals("Reading prolog source in: target" + File.separator + "predicateTooLargeToCompileToJava.pl from file system", events.get(1));
   }

   private void assertSuccess(File scriptsDir) {
      assertSuccess(scriptsDir, new ProjogSupplier() {
         @Override
         public Projog get() {
            return new Projog();
         }
      });
   }

   private void assertSuccess(File scriptsDir, ProjogSupplier projogSupplier) {
      TestResults results = ProjogTestRunner.runTests(scriptsDir, projogSupplier);
      System.out.println(results.getSummary());
      results.assertSuccess();
   }

   private static void extract(File outputDir, String packageName) {
      ProjogTestExtractorConfig config = new ProjogTestExtractorConfig();
      config.setPrologTestsDirectory(outputDir);
      config.setRequireJavadoc(true);
      config.setRequireTest(true);
      config.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            String name = f.getPath().replace(File.separatorChar, '.');
            return name.contains(packageName);
         }
      });
      ProjogTestExtractor.extractTests(config);
   }

}
