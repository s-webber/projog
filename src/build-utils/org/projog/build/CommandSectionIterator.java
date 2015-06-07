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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Returns {@code TableOfContentsEntry} instances that make up the "Prolog Commands" section of <a
 * href="http://projog.org/manual.html">manual.html<a>.
 */
class CommandSectionIterator {
   private final List<CodeExampleWebPage> indexOfGeneratedPages;
   private final Map<String, String> packageDescriptions;
   private final TableOfContentsEntryFactory entryFactory;
   private String previousPackage = null;

   CommandSectionIterator(List<CodeExampleWebPage> indexOfGeneratedPages, Map<String, String> packageDescriptions, TableOfContentsEntryFactory entryFactory) {
      this.indexOfGeneratedPages = sort(indexOfGeneratedPages);
      this.packageDescriptions = packageDescriptions;
      this.entryFactory = entryFactory;
   }

   private List<CodeExampleWebPage> sort(List<CodeExampleWebPage> unsortedIndexOfGeneratedPages) {
      List<CodeExampleWebPage> sortedVersion = new ArrayList<>(unsortedIndexOfGeneratedPages);
      // sort alphabetically so classes are grouped by their packages
      Collections.sort(sortedVersion, new Comparator<CodeExampleWebPage>() {
         @Override
         public int compare(CodeExampleWebPage o1, CodeExampleWebPage o2) {
            return compare(o1.getPrologSourceFile(), o2.getPrologSourceFile());
         }

         private int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
         }
      });
      return sortedVersion;
   }

   boolean hasNext() {
      return !indexOfGeneratedPages.isEmpty();
   }

   TableOfContentsEntry next() {
      CodeExampleWebPage p = indexOfGeneratedPages.get(0);
      String currentPackage = getPackageName(p);
      if (currentPackage.equals(previousPackage)) {
         indexOfGeneratedPages.remove(0);
         return entryFactory.createSubSectionItem(p.getTitle(), p.getHtmlFileName());
      } else {
         previousPackage = currentPackage;
         String packageDescription = getPackageDescription(currentPackage);
         return entryFactory.createDescription(packageDescription);
      }
   }

   private String getPackageName(CodeExampleWebPage p) {
      File f = p.getPrologSourceFile();
      String name = f.getName();
      // last . will be before extension, penultimate dot will be before class name
      int packageEndPos = name.lastIndexOf('.', name.lastIndexOf('.') - 1);
      return name.substring(0, packageEndPos);
   }

   private String getPackageDescription(String packageName) {
      String description = packageDescriptions.get(packageName);
      if (description == null) {
         throw new RuntimeException("Cannot find description for: " + packageName);
      }
      return description;
   }
}
