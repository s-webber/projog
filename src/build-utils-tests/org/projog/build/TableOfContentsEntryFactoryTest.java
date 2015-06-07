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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TableOfContentsEntryFactoryTest {
   @Test
   public void testIndexIncrements() {
      TableOfContentsEntryFactory f = new TableOfContentsEntryFactory();

      assertIndex("1.", f.createSectionHeader(""));
      assertIndex("2.", f.createSectionHeader(""));
      assertIndex(null, f.createDescription(""));
      assertIndex("3.", f.createSectionItem("", ""));
      assertIndex("4.", f.createSectionItem("", ""));
      assertIndex("5.", f.createSectionHeader(""));
      assertIndex(null, f.createDescription(""));
      assertIndex("5.1.", f.createSubSectionItem("", ""));
      assertIndex("5.2.", f.createSubSectionItem("", ""));
      assertIndex(null, f.createDescription(""));
      assertIndex("5.3.", f.createSubSectionItem("", ""));
      assertIndex("6.", f.createSectionHeader(""));
      assertIndex("6.1.", f.createSubSectionItem("", ""));
      assertIndex("7.", f.createSectionItem("", ""));
   }

   @Test
   public void testCreateSectionHeader() {
      TableOfContentsEntryFactory f = new TableOfContentsEntryFactory();
      TableOfContentsEntry e = f.createSectionHeader("title");
      assertEntry(e, "title", "1.", null, true, false, false, false);
   }

   @Test
   public void testCreateSectionItem() {
      TableOfContentsEntryFactory f = new TableOfContentsEntryFactory();
      TableOfContentsEntry e = f.createSectionItem("name", "target");
      assertEntry(e, "name", "1.", "target", false, true, false, false);
   }

   @Test
   public void testCreateSubSectionItem() {
      TableOfContentsEntryFactory f = new TableOfContentsEntryFactory();
      TableOfContentsEntry e = f.createSubSectionItem("name", "target");
      assertEntry(e, "name", "0.1.", "target", false, true, true, false);
   }

   @Test
   public void testCreateDescription() {
      TableOfContentsEntryFactory f = new TableOfContentsEntryFactory();
      TableOfContentsEntry e = f.createDescription("text");
      assertEntry(e, "text", null, null, false, false, false, true);
   }

   private void assertEntry(TableOfContentsEntry entry, String title, String index, String fileName, boolean isHeader, boolean isLink, boolean isSubSection, boolean isDescription) {
      assertEquals(title, entry.getTitle());
      assertIndex(index, entry);
      assertEquals(fileName, entry.getFileName());
      assertEquals(isHeader, entry.isHeader());
      assertEquals(isLink, entry.isLink());
      assertEquals(isSubSection, entry.isSubSection());
      assertEquals(isDescription, entry.isDescription());
   }

   private void assertIndex(String expected, TableOfContentsEntry entry) {
      assertEquals(expected, entry.getIndex());
   }
}
