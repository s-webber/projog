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

import static org.projog.build.BuildUtilsConstants.COMMANDS_INDEX_FILE;
import static org.projog.build.BuildUtilsConstants.HTML_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.MANUAL_TEMPLATE;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.readAllLines;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reads {@link BuildUtilsConstants#MANUAL_TEMPLATE} to determine the {@code TableOfContentsEntry} instances required to
 * construct <a href="http://projog.org/manual.html">manual.html<a>.
 */
class TableOfContentsReader {
   private final TableOfContentsEntryFactory entryFactory;
   private final CommandSectionIterator commandSectionIterator;
   private final List<String> contents;
   private boolean isInCommandsSection;

   TableOfContentsReader(List<CodeExampleWebPage> indexOfGeneratedPages, Map<String, String> packageDescriptions) {
      this.entryFactory = new TableOfContentsEntryFactory();
      this.commandSectionIterator = new CommandSectionIterator(indexOfGeneratedPages, packageDescriptions, entryFactory);
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
            if (commandSectionIterator.hasNext()) {
               return commandSectionIterator.next();
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
            return entryFactory.createSubSectionItem("Index of Built-in Predicates", COMMANDS_INDEX_FILE.getName());
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
      return entryFactory.createSectionHeader(title);
   }

   private TableOfContentsEntry getSectionItem(String line) {
      return entryFactory.createSectionItem(getTitleFromLine(line), getHtmlFileNameFromLine(line));
   }

   private TableOfContentsEntry getSubSectionItem(String line) {
      return entryFactory.createSubSectionItem(getTitleFromLine(line), getHtmlFileNameFromLine(line));
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
    * e.g.: <code>- concepts/prolog-debugging.pl</code>
    * <p>
    * or <b>Example 2</b> - {@code line} lists file name (minus extension) of the page followed by the displayed name of
    * the page:
    * </p>
    * 
    * e.g.: <code>getting_started Getting Started</code>
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
    * e.g.: <code>- concepts/prolog-debugging.pl</code>
    * <p>
    * or <b>Example 2</b> - {@code line} lists file name (minus extension) of the page followed by the displayed name of
    * the page:
    * </p>
    * 
    * e.g.: <code>getting_started Getting Started</code>
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
}
