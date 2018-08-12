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
package org.projog.test;

import java.io.File;
import java.io.FileFilter;

/**
 * Provides configuration for how Prolog tests should be extracted from the comments of Java source files.
 *
 * @see ProjogTestExtractor#extractTests(ProjogTestExtractorConfig)
 */
public final class ProjogTestExtractorConfig {
   private static final FileFilter DEFAULT_FILE_FILTER = new FileFilter() {
      @Override
      public boolean accept(File pathname) {
         return true;
      }
   };

   private File javaRootDirectory;
   private File prologTestsDirectory;
   private boolean requireJavadoc;
   private boolean requireTest;
   private FileFilter fileFilter;

   public ProjogTestExtractorConfig() {
      // default to values that seem sensible for use in a Maven project
      setJavaRootDirectory(new File("src/main/java"));
      setPrologTestsDirectory(new File("target/prolog-tests-extracted-from-java"));
      setFileFilter(DEFAULT_FILE_FILTER);
   }

   public File getJavaRootDirectory() {
      return javaRootDirectory;
   }

   /** The root directory of the Java source files from which Prolog tests will be extracted. */
   public void setJavaRootDirectory(File javaRootDirectory) {
      this.javaRootDirectory = javaRootDirectory;
   }

   public File getPrologTestsDirectory() {
      return prologTestsDirectory;
   }

   /** The output directory to which the extracted Prolog test file will be written. */
   public void setPrologTestsDirectory(File prologTestsDirectory) {
      this.prologTestsDirectory = prologTestsDirectory;
   }

   public FileFilter getFileFilter() {
      return fileFilter;
   }

   /** A filter used to configure which Java source files should have tests extracted from them. */
   public void setFileFilter(FileFilter fileFilter) {
      this.fileFilter = fileFilter;
   }

   public boolean isRequireJavadoc() {
      return requireJavadoc;
   }

   /** Indicates whether an exception being thrown when a Java source file does not contain Javadoc. */
   public void setRequireJavadoc(boolean requireJavadoc) {
      this.requireJavadoc = requireJavadoc;
   }

   public boolean isRequireTest() {
      return requireTest;
   }

   /** Indicates whether an exception being thrown when a Java source file does not contain Prolog tests. */
   public void setRequireTest(boolean requireTest) {
      this.requireTest = requireTest;
   }
}
