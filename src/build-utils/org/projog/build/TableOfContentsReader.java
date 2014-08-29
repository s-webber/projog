package org.projog.build;

import static org.projog.build.BuildUtilsConstants.HTML_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.MANUAL_TEMPLATE;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.readAllLines;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Reads {@link BuildUtilsConstants#MANUAL_TEMPLATE}.
 */
class TableOfContentsReader {
   private final IndexCtr indexCtr;
   private final CommandsIterator commandsIterator;
   private final List<String> contents;
   private boolean isInCommandsSection;

   TableOfContentsReader(List<CodeExampleWebPage> indexOfGeneratedPages, Map<String, String> packageDescriptions) {
      this.indexCtr = new IndexCtr();
      this.commandsIterator = new CommandsIterator(indexOfGeneratedPages, packageDescriptions, indexCtr);
      this.contents = readTableOfContents();
   }

   static String getTitleForTarget(String targetFileNameMinusExtension) {
      String targetFileName = targetFileNameMinusExtension + HTML_FILE_EXTENSION;
      List<String> contents = readTableOfContents();
      for (String line : contents) {
         if (isSection(line) || isSubSection(line)) {
            if (targetFileName.equals(getHtmlFileNameFromLine(line))) {
               return getTitleFromLine(line);
            }
         }
      }
      throw new RuntimeException("Could not find title for link target: " + targetFileName);
   }

   private static List<String> readTableOfContents() {
      return readAllLines(MANUAL_TEMPLATE);
   }

   List<TableOfContentsEntry> getEntries() {
      List<TableOfContentsEntry> result = new ArrayList<>();
      TableOfContentsEntry previous = null;
      TableOfContentsEntry current;
      while ((current = getNext()) != null) {
         result.add(current);
         if (current.isLink()) {
            if (previous != null) {
               previous.setNext(current);
            }
            current.setPrevious(previous);
            previous = current;
         }
      }
      return result;
   }

   private TableOfContentsEntry getNext() {
      while (true) {
         if (isInCommandsSection) {
            if (commandsIterator.hasNext()) {
               return commandsIterator.next();
            } else {
               isInCommandsSection = false;
            }
         }

         if (contents.isEmpty()) {
            // end of file
            return null;
         }
         String next = contents.remove(0).trim();
         if (isSectionHeader(next)) {
            return getSectionHeader(next);
         } else if (isSection(next)) {
            return getSectionItem(next);
         } else if (isSubSection(next)) {
            return getSubSectionItem(next);
         } else if (isCommandsSection(next)) {
            isInCommandsSection = true;
         }
         // else move on to next line
      }
   }

   private static boolean isSectionHeader(String line) {
      return line.startsWith("+ ");
   }

   private static boolean isSection(String line) {
      return line.startsWith("* ");
   }

   private static boolean isSubSection(String line) {
      return line.startsWith("- ");
   }

   private static boolean isCommandsSection(String line) {
      return line.equals("[COMMANDS]");
   }

   private TableOfContentsEntry getSectionHeader(String line) {
      String title = line.substring(2);
      return indexCtr.createSectionHeader(title);
   }

   private TableOfContentsEntry getSectionItem(String line) {
      return indexCtr.createSectionItem(getTitleFromLine(line), getHtmlFileNameFromLine(line));
   }

   private TableOfContentsEntry getSubSectionItem(String line) {
      return indexCtr.createSubSectionItem(getTitleFromLine(line), getHtmlFileNameFromLine(line));
   }

   /**
    * Returns the html file an entry in the table of contents should link to.
    * <p>
    * The specified {@code line} parameter can be in one of two forms.
    * </p>
    * <p>
    * <b>Example 1</b> - {@code line} lists the prolog file the web page is constructed from:
    * </p>
    * 
    * <pre>
	 * - concepts/prolog-debugging.pl
	 * </pre>
    * <p>
    * or <b>Example 2</b> - {@code line} lists file name (minus extension) of the page followed by the displayed name of
    * the page:
    * </p>
    * 
    * <pre>
	 * * getting_started Getting Started
	 * </pre>
    */
   private static String getHtmlFileNameFromLine(String line) {
      int spacePos = line.indexOf(' ', 3);
      if (spacePos == -1) {
         int startPos = line.lastIndexOf('/');
         int endPos = line.indexOf('.');
         return line.substring(startPos + 1, endPos) + HTML_FILE_EXTENSION;
      }
      return line.substring(2, spacePos) + HTML_FILE_EXTENSION;
   }

   /**
    * Returns the name to display for an entry in the table of contents.
    * <p>
    * The specified {@code line} parameter can be in one of two forms.
    * </p>
    * <p>
    * <b>Example 1</b> - {@code line} lists the prolog file the web page is constructed from:
    * </p>
    * 
    * <pre>
	 * - concepts/prolog-debugging.pl
	 * </pre>
    * <p>
    * or <b>Example 2</b> - {@code line} lists file name (minus extension) of the page followed by the displayed name of
    * the page:
    * </p>
    * 
    * <pre>
	 * * getting_started Getting Started
	 * </pre>
    */
   private static String getTitleFromLine(String line) {
      int spacePos = line.indexOf(' ', 3);
      if (spacePos == -1) {
         String fileName = line.substring(1).trim();
         File scriptFile = new File(SCRIPTS_OUTPUT_DIR, fileName);
         CodeExampleWebPage page = CodeExampleWebPage.create(scriptFile);
         return page.getTitle();
      }
      return line.substring(spacePos + 1);
   }

   private static class IndexCtr {
      int sectionNumber;
      int subSectionNumber;

      TableOfContentsEntry createSectionHeader(String title) {
         incrementSectionNumber();
         return createEntry(title, null);
      }

      TableOfContentsEntry createSectionItem(String title, String htmlFileName) {
         incrementSectionNumber();
         return createEntry(title, htmlFileName);
      }

      TableOfContentsEntry createSubSectionItem(String title, String htmlFileName) {
         incrementSubSectionNumber();
         return createEntry(title, htmlFileName);
      }

      private void incrementSectionNumber() {
         sectionNumber++;
         subSectionNumber = 0;
      }

      private void incrementSubSectionNumber() {
         subSectionNumber++;
      }

      private TableOfContentsEntry createEntry(String title, String fileName) {
         return new TableOfContentsEntry(title, fileName, getIndex());
      }

      private String getIndex() {
         String index = sectionNumber + ".";
         if (subSectionNumber != 0) {
            index += subSectionNumber + ".";
         }
         return index;
      }
   }

   private static class CommandsIterator {
      private final List<CodeExampleWebPage> indexOfGeneratedPages;
      private final Map<String, String> packageDescriptions;
      private final IndexCtr indexCtr;
      private String previousPackage = null;

      CommandsIterator(List<CodeExampleWebPage> indexOfGeneratedPages, Map<String, String> packageDescriptions, IndexCtr indexCtr) {
         this.indexOfGeneratedPages = sort(indexOfGeneratedPages);
         this.packageDescriptions = packageDescriptions;
         this.indexCtr = indexCtr;
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
            return indexCtr.createSubSectionItem(p.getTitle(), p.getHtmlFileName());
         } else {
            previousPackage = currentPackage;
            return new TableOfContentsEntry(getPackageDescription(currentPackage), null, null);
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
}