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

public final class ProjogTestGeneratorConfig {
   private File javaRootDirectory;
   private String packageName;
   private File prologTestsDirectory;
   private boolean requireJavadoc;
   private boolean requireTest;

   public File getJavaRootDirectory() {
      return javaRootDirectory;
   }

   public void setJavaRootDirectory(File javaRootDirectory) {
      this.javaRootDirectory = javaRootDirectory;
   }

   public String getPackageName() {
      return packageName;
   }

   public void setPackageName(String packageName) {
      this.packageName = packageName;
   }

   public File getPrologTestsDirectory() {
      return prologTestsDirectory;
   }

   public void setPrologTestsDirectory(File prologTestsDirectory) {
      this.prologTestsDirectory = prologTestsDirectory;
   }

   public boolean isRequireJavadoc() {
      return requireJavadoc;
   }

   public void setRequireJavadoc(boolean requireJavadoc) {
      this.requireJavadoc = requireJavadoc;
   }

   public boolean isRequireTest() {
      return requireTest;
   }

   public void setRequireTest(boolean requireTest) {
      this.requireTest = requireTest;
   }
}
