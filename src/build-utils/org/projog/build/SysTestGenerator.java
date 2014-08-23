package org.projog.build;

import static org.projog.build.BuildUtilsConstants.PROLOG_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.SOURCE_INPUT_DIR;
import static org.projog.build.BuildUtilsConstants.SOURCE_INPUT_DIR_NAME;
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
 * Produces {@code .txt} and {@code .pl} files for every subclass of {@code PredicateFactory}.
 * <p>
 * The contents of the files are extracted from the comments in the {@code .java} file of the {@code PredicateFactory}.
 * The {@code .txt} file contains the contents of the javadoc comment of the class. The {@code .pl} file contains the
 * prolog syntax contained in the "{@code SYSTEM TEST}" comment at the top of the class.
 */
public class SysTestGenerator {
   private static final File COMMANDS_OUTPUT_DIR = new File(SCRIPTS_OUTPUT_DIR, "commands");

   private static List<File> getDocumentableJavaSourceFiles(File dir) {
      List<File> result = new ArrayList<File>();
      File[] directoryContents = dir.listFiles();
      for (File file : directoryContents) {
         if (file.isDirectory()) {
            // continue directory tree walk
            result.addAll(getDocumentableJavaSourceFiles(file));
         } else if (isJavaSourceFileOfDocumentedClass(file)) {
            result.add(file);
         }
      }
      return result;
   }

   private static boolean isJavaSourceFileOfDocumentedClass(File file) {
      try {
         if (!isJavaSource(file)) {
            return false;
         }

         String className = getClassName(file);
         Class<?> c = Class.forName(className);
         if (isDocumentable(c)) {
            // confirm the class is instantiatable
            Object o = c.newInstance();
            System.out.println("Will produce documentation for: " + o.getClass().getName());
            return true;
         } else {
            return false;
         }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
         System.out.println("Not producing documentation for " + file + " - due to: " + e.getClass());
         return false;
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

   private static String getClassName(File javaFile) {
      String filePath = javaFile.getPath();
      String filePathMinusExtension = removeFileExtension(filePath);
      String filePathMinusSourceDirectoryAndFileExtension = filePath.substring(SOURCE_INPUT_DIR_NAME.length(), filePathMinusExtension.length());
      return filePathMinusSourceDirectoryAndFileExtension.replace(File.separatorChar, '.');
   }

   private static void produceScriptFileFromJavaFile(File javaFile) {
      COMMANDS_OUTPUT_DIR.mkdirs();
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

   private static void writeScriptFile(File javaFile, BufferedReader br) {
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
   private static void writeTextFile(File javaFile, BufferedReader br) {
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

   private static File getOutputFile(File javaSourceFile, String extension) {
      return new File(COMMANDS_OUTPUT_DIR, toScriptName(javaSourceFile, extension));
   }

   private static String toScriptName(File javaFile, String extension) {
      String nameIncludingPackageStructure = javaFile.getPath().substring(SOURCE_INPUT_DIR_NAME.length()).replace(File.separatorChar, '.');
      return replaceFileExtension(nameIncludingPackageStructure, extension);
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
      List<File> javaSourceFiles = getDocumentableJavaSourceFiles(SOURCE_INPUT_DIR);
      Map<String, File> alreadyProcessed = new HashMap<String, File>();
      for (File f : javaSourceFiles) {
         File previousEntry = alreadyProcessed.put(f.getName(), f);
         if (previousEntry != null) {
            throw new IllegalArgumentException("Two instances of: " + f.getName() + " first: " + previousEntry + " second: " + f);
         }
         produceScriptFileFromJavaFile(f);
      }
   }
}