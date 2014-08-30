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