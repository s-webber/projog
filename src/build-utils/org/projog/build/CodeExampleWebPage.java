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

import static org.projog.build.BuildUtilsConstants.DOCS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.HTML_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.LINE_BREAK;
import static org.projog.build.BuildUtilsConstants.TEXT_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.readAllLines;

import java.io.File;
import java.util.List;

/**
 * Represents a web page containing example Prolog queries and responses.
 */
class CodeExampleWebPage {
   static CodeExampleWebPage create(File prologSourceFile) {
      File htmlFile = getHtmlFile(prologSourceFile);
      File textFile = getTextFile(prologSourceFile);
      List<String> textFileContents = readAllLines(textFile);
      String title = textFileContents.remove(0);
      String description = toString(textFileContents);
      return new CodeExampleWebPage(title, description, prologSourceFile, htmlFile);
   }

   private static File getHtmlFile(File scriptFile) {
      String nameMinusExtension = getFileNameMinusExtension(scriptFile);
      String nameMinusPackage = nameMinusExtension.substring(nameMinusExtension.lastIndexOf('.') + 1);
      String htmlFileName = nameMinusPackage + HTML_FILE_EXTENSION;
      return new File(DOCS_OUTPUT_DIR, htmlFileName);
   }

   private static File getTextFile(File scriptFile) {
      String nameMinusExtension = getFileNameMinusExtension(scriptFile);
      String textFileName = nameMinusExtension + TEXT_FILE_EXTENSION;
      return new File(scriptFile.getParentFile(), textFileName);
   }

   private static String getFileNameMinusExtension(File file) {
      String fileName = file.getName();
      int dotPos = fileName.lastIndexOf('.');
      return fileName.substring(0, dotPos);
   }

   private static String toString(List<String> lines) {
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         sb.append(line);
         sb.append(LINE_BREAK);
      }
      return sb.toString();
   }

   private final String title;
   private final String description;
   private final File prologSourceFile;
   private final File htmlFile;

   private CodeExampleWebPage(String title, String description, File prologSourceFile, File htmlFile) {
      this.title = title;
      this.description = description;
      this.prologSourceFile = prologSourceFile;
      this.htmlFile = htmlFile;
   }

   String getTitle() {
      return title;
   }

   String getDescription() {
      return description;
   }

   File getPrologSourceFile() {
      return prologSourceFile;
   }

   File getHtmlFile() {
      return htmlFile;
   }

   String getHtmlFileName() {
      return htmlFile.getName();
   }
}
