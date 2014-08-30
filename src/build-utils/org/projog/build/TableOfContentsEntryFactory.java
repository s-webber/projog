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
