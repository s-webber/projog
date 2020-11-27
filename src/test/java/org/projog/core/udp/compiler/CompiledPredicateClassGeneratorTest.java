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
package org.projog.core.udp.compiler;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Ignore;
import org.junit.Test;
import org.projog.api.Projog;
import org.projog.core.ProjogDefaultProperties;
import org.projog.core.ProjogProperties;

@Ignore
public class CompiledPredicateClassGeneratorTest {
   private static final File PROLOG_SOURCE_DIR = new File("src/test/prolog/CompiledPredicateSourceGeneratorTest");
   private static final File TARGET_DIR = new File("target");

   @Test
   public void testSpyPointsEnabled() throws IOException {
      assertCompiledSourceCodeContents(createProperties(true));
   }

   @Test
   public void testSpyPointsDisabled() throws IOException {
      assertCompiledSourceCodeContents(createProperties(false));
   }

   private ProjogProperties createProperties(final boolean isSpyPointsEnabled) {
      final File compiledContentOutputDirectory = new File(TARGET_DIR, getClass().getName() + System.currentTimeMillis());
      compiledContentOutputDirectory.mkdir();

      return new ProjogDefaultProperties() {
         @Override
         public boolean isSpyPointsEnabled() {
            return isSpyPointsEnabled;
         }

         @Override
         public boolean isRuntimeCompilationEnabled() {
            return true;
         }

         @Override
         public File getCompiledContentOutputDirectory() {
            return compiledContentOutputDirectory;
         }
      };
   }

   private void assertCompiledSourceCodeContents(ProjogProperties properties) throws IOException {
      compilePredicates(properties);

      File javaSourceDir = new File(PROLOG_SOURCE_DIR, properties.isSpyPointsEnabled() ? "spy_points_enabled" : "spy_points_disabled");
      File[] expected = listTextFiles(javaSourceDir);
      File[] actual = listTextFiles(properties.getCompiledContentOutputDirectory());

      assertEquals(expected.length, actual.length);

      for (File f : expected) {
         assertFileContents(new File(javaSourceDir, f.getName()), new File(properties.getCompiledContentOutputDirectory(), f.getName()));
      }
   }

   private void compilePredicates(ProjogProperties properties) {
      new Projog(properties).consultFile(new File(PROLOG_SOURCE_DIR, "CompiledPredicateSourceGeneratorTest.pl"));
   }

   private File[] listTextFiles(File directory) {
      return directory.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith(".txt");
         }
      });
   }

   private void assertFileContents(File f1, File f2) throws IOException {
      assertEquals(readAsText(f1), readAsText(f2));
   }

   private String readAsText(File f) throws IOException {
      StringBuilder sb = new StringBuilder();
      for (String s : Files.readAllLines(f.toPath())) {
         sb.append(s).append(System.lineSeparator());
      }
      return sb.toString();
   }
}
