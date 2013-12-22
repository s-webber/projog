/*
 * Copyright 2013 S Webber
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Constants and utility methods used in the build process.
 */
class BuildUtilsConstants {
   private static final File BUILD_DIR = new File("build");

   static final File WEB_SRC_DIR = new File("web");
   static final File DOCS_OUTPUT_DIR = new File(BUILD_DIR, "docs");
   static final File SCRIPTS_OUTPUT_DIR = new File(BUILD_DIR, "scripts");
   static final File MANUAL_TEMPLATE = new File(WEB_SRC_DIR, "manual.txt");
   static final File STATIC_PAGES_LIST = new File(WEB_SRC_DIR, "static_pages.properties");
   static final String LINE_BREAK = "\r\n";
   static final String HTML_FILE_EXTENSION = ".html";
   static final String PROLOG_FILE_EXTENSION = ".pl";
   static final String TEXT_FILE_EXTENSION = ".txt";
   static final String MANUAL_HTML = "manual" + HTML_FILE_EXTENSION;
   static final String HEADER_HTML = "header" + HTML_FILE_EXTENSION;
   static final String FOOTER_HTML = "footer" + HTML_FILE_EXTENSION;

   /**
    * Returns the contents of the specified file as a byte array.
    * 
    * @param f file to read
    * @return contents of file
    */
   static byte[] toByteArray(File f) {
      FileInputStream fis = null;
      ByteArrayOutputStream baos = null;
      try {
         fis = new FileInputStream(f);
         baos = new ByteArrayOutputStream();
         int next;
         while ((next = fis.read()) != -1) {
            baos.write(next);
         }
         return baos.toByteArray();
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         try {
            fis.close();
         } catch (Exception e) {
         }
         try {
            baos.close();
         } catch (Exception e) {
         }
      }
   }

   /**
    * Returns list of lines contained in specified text file.
    * 
    * @param f text file to read
    * @return list of lines contained in specified file
    */
   static List<String> readFile(File f) {
      List<String> result = new ArrayList<String>();
      FileReader fr = null;
      BufferedReader br = null;
      try {
         fr = new FileReader(f);
         br = new BufferedReader(fr);
         String next;
         while ((next = br.readLine()) != null) {
            result.add(next);
         }
         if (result.isEmpty()) {
            throw new Exception("File is empty");
         }
      } catch (Exception e) {
         throw new RuntimeException("could not read text file: " + f, e);
      } finally {
         try {
            br.close();
         } catch (Exception e) {
         }
         try {
            fr.close();
         } catch (Exception e) {
         }
      }
      return result;
   }

   /**
    * Writes the specified sequence of char values to a file.
    * 
    * @param contents the content to be written to the newly created file
    * @return the newly created file
    */
   static File writeToTempFile(CharSequence contents) throws IOException {
      FileWriter fw = null;
      BufferedWriter bw = null;
      try {
         File f = File.createTempFile("systest", ".tmp", BUILD_DIR);
         fw = new FileWriter(f);
         bw = new BufferedWriter(fw);
         bw.append(contents);
         return f;
      } finally {
         if (bw != null) {
            bw.close();
         }
         if (fw != null) {
            fw.close();
         }
      }
   }
}