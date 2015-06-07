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

/** @see TableOfContentsEntry */
class TableOfContentsEntryFactory {
   private int sectionNumber;
   private int subSectionNumber;

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

   TableOfContentsEntry createDescription(String description) {
      return new TableOfContentsEntry(description, null, null);
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
