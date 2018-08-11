/*
 * Copyright 2013-2014 S. Webber
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
package org.projog.build;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Constants and utility methods used in the build process.
 */
final class BuildUtilsConstants {
   static final String FUNCTION_PACKAGE_NAME = "org.projog.core.function";
   static final File BUILD_DIR = new File("build");
   static final File WEB_SRC_DIR = new File("web");
   static final File DOCS_OUTPUT_DIR = new File(BUILD_DIR, "docs");
   static final File SCRIPTS_OUTPUT_DIR = new File(BUILD_DIR, "prolog");
   static final File MANUAL_TEMPLATE = new File(WEB_SRC_DIR, "manual.txt");
   static final File STATIC_PAGES_LIST = new File(WEB_SRC_DIR, "static_pages.properties");
   static final File COMMANDS_INDEX_FILE = new File(DOCS_OUTPUT_DIR, "prolog-predicates.html");
   static final String SOURCE_INPUT_DIR_NAME = "src/core/";
   static final File SOURCE_INPUT_DIR = new File(SOURCE_INPUT_DIR_NAME);
   static final File FUNCTION_PACKAGE_DIR = new File(SOURCE_INPUT_DIR, FUNCTION_PACKAGE_NAME.replace('.', File.separatorChar));
   static final String LINE_BREAK = "\n";
   static final String HTML_FILE_EXTENSION = ".html";
   static final String PROLOG_FILE_EXTENSION = ".pl";
   static final String TEXT_FILE_EXTENSION = ".txt";
   static final String MANUAL_HTML = "manual" + HTML_FILE_EXTENSION;
   static final String HEADER_HTML = "header" + HTML_FILE_EXTENSION;
   static final String FOOTER_HTML = "footer" + HTML_FILE_EXTENSION;

   /** Returns {@code true} if the the specified file has a prolog file extension. */
   static boolean isPrologScript(File f) {
      return f.getName().endsWith(PROLOG_FILE_EXTENSION);
   }

   /**
    * Returns the contents of the specified file as a byte array.
    *
    * @param f file to read
    * @return contents of file
    */
   static byte[] readAllBytes(File f) {
      try {
         return Files.readAllBytes(f.toPath());
      } catch (Exception e) {
         throw new RuntimeException("could not read file: " + f, e);
      }
   }

   /**
    * Returns the contents of the specified file as a {@code String}.
    * <p>
    * Note: Carriage returns will be represented by {@code #LINE_BREAK} rather than the underlying carriage return style
    * used in the actual file.
    *
    * @param f text file to read
    * @return list of lines contained in specified file
    */
   static String readText(File f) {
      return concatLines(readAllLines(f));
   }

   /**
    * Combines the specified list into a single {@code String}.
    * <p>
    * Each line will be followed by {@code #LINE_BREAK}.
    */
   static String concatLines(List<String> lines) {
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         sb.append(line);
         sb.append(LINE_BREAK);
      }
      return sb.toString();
   }

   /**
    * Returns list of lines contained in specified text file.
    *
    * @param f text file to read
    * @return list of lines contained in specified file
    */
   static List<String> readAllLines(File f) {
      try {
         return Files.readAllLines(f.toPath(), Charset.defaultCharset());
      } catch (Exception e) {
         throw new RuntimeException("could not read text file: " + f, e);
      }
   }

   /** Replaces all Windows-style {@code CR+LF} (i.e. {@code \r\n}) line endings with {@code LF} (i.e. {@code \n}). */
   static String toUnixLineEndings(String expected) {
      return expected.replace("\r\n", "\n");
   }

   static String htmlEncode(String input) {
      return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("  ", "&nbsp;&nbsp;").replace(LINE_BREAK, "<br>" + LINE_BREAK);
   }
}
