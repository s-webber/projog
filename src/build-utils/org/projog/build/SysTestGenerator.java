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

import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.TEXT_FILE_EXTENSION;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.projog.core.PredicateFactory;

/**
 * Produces {@code .txt} and {@code .pl} files for every subclass of {@code PredicateFactory}.
 * <p>
 * The contents of the files are extracted from the comments in the {@code .java} file of the {@code PredicateFactory}.
 * The {@code .txt} file contains the contents of the javadoc comment of the class. The {@code .pl} file contains the
 * prolog syntax contained in the "{@code SYSTEM TEST}" comment at the top of the class.
 */
public class SysTestGenerator {
   private static final String SOURCE_INPUT_DIR = "src/core/";
   private static final File COMMANDS_OUTPUT_DIR = new File(SCRIPTS_OUTPUT_DIR, "commands");

   private static void findAllPredicates(File dir) {
      File[] directoryContents = dir.listFiles();
      for (File file : directoryContents) {
         if (file.isDirectory()) {
            findAllPredicates(file);
         } else if (isJavaSourceFileOfPredicateClass(file)) {
            // produce the script file
            produceScriptFileFromJavaFile(file);
         }
      }
   }

   private static boolean isJavaSourceFileOfPredicateClass(File file) {
      if (!isJavaSource(file)) {
         return false;
      }
      String className = getClassName(file);
      try {
         System.out.println("SysTest: Checking: " + className);
         Class<?> c = Class.forName(className);
         if (PredicateFactory.class.isAssignableFrom(c)) {
            PredicateFactory e = (PredicateFactory) c.newInstance();
            System.out.println("SysTest: Created: " + e);
            return true;
         }
      } catch (Exception e) {
         System.out.println("trying to create Predicate: " + className + " caused: " + e);
      }
      return false;
   }

   private static boolean isJavaSource(File f) {
      return f.getName().endsWith(".java");
   }

   private static String getClassName(File javaFile) {
      String filePath = javaFile.getPath();
      String filePathMinusExtension = removeFileExtension(filePath);
      String filePathMinusSourceDirectoryAndFileExtension = filePath.substring(SOURCE_INPUT_DIR.length(), filePathMinusExtension.length());
      return filePathMinusSourceDirectoryAndFileExtension.replace(File.separatorChar, '.');
   }

   private static void produceScriptFileFromJavaFile(File javaFile) {
      COMMANDS_OUTPUT_DIR.mkdirs();
      FileReader fr = null;
      BufferedReader br = null;
      try {
         fr = new FileReader(javaFile);
         br = new BufferedReader(fr);
         boolean sysTestRead = false;
         boolean javadocRead = false;
         String line;
         while ((!sysTestRead || !javadocRead) && (line = br.readLine()) != null) {
            line = line.trim();
            if ("/* SYSTEM TEST".equals(line)) {
               sysTestRead = true;
               writeScriptFile(javaFile, br);
            } else if (sysTestRead && !javadocRead && "/**".equals(line)) {
               javadocRead = true;
               writeTextFile(javaFile, br);
            }
         }
         if (!sysTestRead) {
            throw new Exception("No system tests read for: " + javaFile);
         }
         if (!javadocRead) {
            throw new Exception("No javadoc read for: " + javaFile);
         }
      } catch (Exception e) {
         throw new RuntimeException("cannot generate script from " + javaFile + " due to " + e, e);
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
   }

   private static void writeScriptFile(File javaFile, BufferedReader br) {
      String scriptName = replaceFileExtension(javaFile.getName(), ".pl");
      File scriptFile = new File(COMMANDS_OUTPUT_DIR, scriptName);

      FileWriter fw = null;
      BufferedWriter bw = null;
      try {
         fw = new FileWriter(scriptFile);
         bw = new BufferedWriter(fw);
         String line;
         while (!"*/".equals(line = br.readLine().trim())) {
            bw.write(line);
            bw.newLine();
         }
      } catch (IOException e) {
         throw new RuntimeException("Could not produce: " + scriptFile + " due to: " + e, e);
      } finally {
         try {
            bw.flush();
         } catch (Exception e) {
         }
         try {
            bw.close();
         } catch (Exception e) {
         }
         try {
            fw.close();
         } catch (Exception e) {
         }
      }
   }

   /**
    * Writes comments contained in Javadoc for class to a text file.
    * <p>
    * Comments can then be reused to construct user manual documentation.
    */
   private static void writeTextFile(File javaFile, BufferedReader br) {
      String textFileName = replaceFileExtension(javaFile.getName(), TEXT_FILE_EXTENSION);
      File textFile = new File(COMMANDS_OUTPUT_DIR, textFileName);

      FileWriter fw = null;
      BufferedWriter bw = null;
      try {
         fw = new FileWriter(textFile);
         bw = new BufferedWriter(fw);
         String line;
         while (!"*/".equals(line = br.readLine().trim())) {
            line = line.trim();
            if (line.startsWith("*")) {
               line = line.substring(1).trim();
            }
            // ignore ant @see annotations present in input Javadoc 
            if (!line.startsWith("@see")) {
               bw.write(line);
               bw.newLine();
            }
         }
      } catch (IOException e) {
         throw new RuntimeException("Could not produce: " + textFile + " due to: " + e, e);
      } finally {
         try {
            bw.flush();
         } catch (Exception e) {
         }
         try {
            bw.close();
         } catch (Exception e) {
         }
         try {
            fw.close();
         } catch (Exception e) {
         }
      }
   }

   private static String replaceFileExtension(String fileName, String newExtension) {
      return removeFileExtension(fileName) + newExtension;
   }

   private static String removeFileExtension(String fileName) {
      int extensionPos = fileName.lastIndexOf('.');
      if (extensionPos == -1) {
         return fileName;
      } else {
         return fileName.substring(0, extensionPos);
      }
   }

   public static final void main(String[] args) {
      findAllPredicates(new File(SOURCE_INPUT_DIR));
   }
}