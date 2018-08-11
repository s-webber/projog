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

import static org.projog.build.BuildUtilsConstants.FUNCTION_PACKAGE_NAME;
import static org.projog.build.BuildUtilsConstants.PROLOG_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.SOURCE_INPUT_DIR;
import static org.projog.build.BuildUtilsConstants.TEXT_FILE_EXTENSION;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projog.core.Calculatable;
import org.projog.core.PredicateFactory;

/**
 * Produces {@code .txt} and {@code .pl} files for implementations of {@code PredicateFactory}.
 * <p>
 * The contents of the files are extracted from the comments in the {@code .java} file of the {@code PredicateFactory}.
 * The {@code .txt} file contains the contents of the Javadoc comment of the class. The {@code .pl} file contains the
 * Prolog syntax contained in the "{@code TEST}" comment at the top of the class.
 */
public final class ProjogTestGenerator {
   private final String packageName;
   private final File outputDirectory;

   private ProjogTestGenerator(String packageName, File outputDirectory) {
      this.packageName = packageName;
      this.outputDirectory = outputDirectory;
   }

   private List<File> getDocumentableJavaSourceFiles(File dir) {
      List<File> result = new ArrayList<File>();
      for (File f : dir.listFiles()) {
         if (f.isDirectory()) {
            result.addAll(getDocumentableJavaSourceFiles(f));
         } else if (isJavaSourceFileOfDocumentedClass(f)) {
            result.add(f);
         }
      }
      return result;
   }

   private boolean isJavaSourceFileOfDocumentedClass(File file) {
      if (!isJavaSource(file)) {
         return false;
      }

      Class<?> c = getClass(file);
      return isDocumentable(c);
   }

   private Class<?> getClass(File file) {
      try {
         String className = getClassName(file);
         return Class.forName(className);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   private static boolean isDocumentable(Class<?> c) {
      return isConcrete(c) && isPublic(c) && (isPredicateFactory(c) || isCalculatable(c));
   }

   private static boolean isConcrete(Class<?> c) {
      return !Modifier.isAbstract(c.getModifiers());
   }

   private static boolean isPublic(Class<?> c) {
      return Modifier.isPublic(c.getModifiers());
   }

   private static boolean isPredicateFactory(Class<?> c) {
      return PredicateFactory.class.isAssignableFrom(c);
   }

   private static boolean isCalculatable(Class<?> c) {
      return Calculatable.class.isAssignableFrom(c);
   }

   private static boolean isJavaSource(File f) {
      return f.getName().endsWith(".java");
   }

   private void produceScriptFileFromJavaFile(File javaFile) {
      outputDirectory.mkdirs();
      try (FileReader fr = new FileReader(javaFile); BufferedReader br = new BufferedReader(fr)) {
         boolean sysTestRead = false;
         boolean javadocRead = false;
         String line;
         while ((!sysTestRead || !javadocRead) && (line = br.readLine()) != null) {
            line = line.trim();
            if ("/* TEST".equals(line)) {
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
      }
   }

   private void writeScriptFile(File javaFile, BufferedReader br) {
      File scriptFile = getOutputFile(javaFile, PROLOG_FILE_EXTENSION);

      try (FileWriter fw = new FileWriter(scriptFile); BufferedWriter bw = new BufferedWriter(fw)) {
         String line;
         while (!"*/".equals(line = br.readLine().trim())) {
            bw.write(line);
            bw.newLine();
         }
      } catch (IOException e) {
         throw new RuntimeException("Could not produce: " + scriptFile + " due to: " + e, e);
      }
   }

   /**
    * Writes comments contained in Javadoc for class to a text file.
    * <p>
    * Comments can then be reused to construct user manual documentation.
    */
   private void writeTextFile(File javaFile, BufferedReader br) {
      File textFile = getOutputFile(javaFile, TEXT_FILE_EXTENSION);

      try (FileWriter fw = new FileWriter(textFile); BufferedWriter bw = new BufferedWriter(fw)) {
         String line;
         while (!"*/".equals(line = br.readLine().trim())) {
            line = line.trim();
            if (line.startsWith("*")) {
               line = line.substring(1).trim();
            }
            // ignore any annotations present in input Javadoc
            if (!isAnnotation(line)) {
               bw.write(line);
               bw.newLine();
            }
         }
      } catch (IOException e) {
         throw new RuntimeException("Could not produce: " + textFile + " due to: " + e, e);
      }
   }

   private static boolean isAnnotation(String line) {
      return line.startsWith("@");
   }

   private File getOutputFile(File javaSourceFile, String extension) {
      return new File(outputDirectory, getClassName(javaSourceFile) + extension);
   }

   private String getClassName(File javaFile) {
      String path = javaFile.getPath().replace(File.separatorChar, '.');
      int startPos = path.lastIndexOf(packageName);
      return path.substring(startPos, path.lastIndexOf('.'));
   }

   public static void generate(File rootJavaDirectory, String packageName, File outputDirectory) {
      ProjogTestGenerator generator = new ProjogTestGenerator(packageName, outputDirectory);
      List<File> javaSourceFiles = generator.getDocumentableJavaSourceFiles(new File(rootJavaDirectory, packageName.replace('.', File.separatorChar)));
      Map<String, File> alreadyProcessed = new HashMap<String, File>();
      for (File f : javaSourceFiles) {
         File previousEntry = alreadyProcessed.put(f.getName(), f);
         if (previousEntry != null) {
            throw new IllegalArgumentException("Two instances of: " + f.getName() + " first: " + previousEntry + " second: " + f);
         }
         generator.produceScriptFileFromJavaFile(f);
      }
   }

   public static final void main(String[] args) throws Exception {
      generate(SOURCE_INPUT_DIR, FUNCTION_PACKAGE_NAME, new File(SCRIPTS_OUTPUT_DIR, "commands"));
   }
}
