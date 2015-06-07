/*
 * Copyright 2015 S. Webber
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

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/** Tests that the contents of every Java source file starts with the license header. */
public class SourceHeaderTest {
   @Test
   public void testSourceHeaders() throws IOException {
      List<Path> javaSourceFiles = getJavaSourceFiles();
      for (Path f : javaSourceFiles) {
         assertSourceHeader(f);
      }
   }

   /** @return all Java source files for the project */
   private List<Path> getJavaSourceFiles() throws IOException {
      JavaSourceFinder visitor = new JavaSourceFinder();
      Files.walkFileTree(new File("src").toPath(), visitor);
      return visitor.result;
   }

   /** Asserts that the specified Java source file starts with the license header. */
   private void assertSourceHeader(Path p) throws IOException {
      List<String> lines = Files.readAllLines(p, Charset.defaultCharset());
      String failureMessage = "No source header found for " + p.toFile();
      assertTrue(failureMessage, lines.size() > 15);
      assertEquals(failureMessage, "/*", lines.get(0));
      assertEquals(failureMessage, " * Licensed under the Apache License, Version 2.0 (the \"License\");", lines.get(3));
      assertEquals(failureMessage, " * you may not use this file except in compliance with the License.", lines.get(4));
      assertEquals(failureMessage, " * You may obtain a copy of the License at", lines.get(5));
      assertEquals(failureMessage, " *     http://www.apache.org/licenses/LICENSE-2.0", lines.get(7));
   }

   private class JavaSourceFinder extends SimpleFileVisitor<Path> {
      private final List<Path> result = new ArrayList<>();

      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
         if (isJavaSource(path)) {
            result.add(path);
         }
         return CONTINUE;
      }

      private boolean isJavaSource(Path p) {
         String name = p.toFile().getName();
         return name.endsWith(".java") && !name.equals("package-info.java");
      }
   }
}
