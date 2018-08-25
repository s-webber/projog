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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.COMPILATION_DISABLED_PROPERTIES;
import static org.projog.TestUtils.COMPILATION_ENABLED_PROPERTIES;

import java.io.File;
import java.io.FileFilter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.projog.api.Projog;
import org.projog.core.ProjogProperties;
import org.projog.test.ProjogTestExtractor;
import org.projog.test.ProjogTestExtractorConfig;
import org.projog.test.ProjogTestRunner;
import org.projog.test.ProjogTestRunner.ProjogSupplier;
import org.projog.test.ProjogTestRunner.TestResults;

/**
 * TODO
 */
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
            String path = f.getPath();
            return !path.endsWith("package-info.java") && path.replace(File.separatorChar, '.').contains("org.projog.core.function");
         }
      });
      ProjogTestExtractor.extractTests(config);
   }

   @Test
   public void prologTestsInterpretedMode() {
      assertSuccess(SOURCE_PROLOG_TESTS_DIR, compilationDisabledProjog());
   }

   @Test
   public void prologTestsCompiledMode() {
      assertSuccess(SOURCE_PROLOG_TESTS_DIR, compilationEnabledProjog());
   }

   @Test
   public void extractedTestsInterpretedMode() {
      assertSuccess(EXTRACTED_PROLOG_TESTS_DIR, compilationDisabledProjog());
   }

   @Test
   public void extractedTestsCompiledMode() {
      assertSuccess(EXTRACTED_PROLOG_TESTS_DIR, compilationEnabledProjog());
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
