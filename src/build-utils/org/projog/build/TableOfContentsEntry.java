package org.projog.build;

/**
 * Represents an entry in {@code manual.txt}.
 */
class TableOfContentsEntry {
   /** The name to display in the link. */
   private final String title;
   /** Position in documentation hierarchy (e.g. 2.3) */
   private final String index;
   /** The actual HTML file. */
   private final String fileName;
   private TableOfContentsEntry previous;
   private TableOfContentsEntry next;

   TableOfContentsEntry(String title, String fileName, String index) {
      this.title = title;
      this.fileName = fileName;
      this.index = index;
   }

   TableOfContentsEntry getPrevious() {
      return previous;
   }

   void setPrevious(TableOfContentsEntry previous) {
      this.previous = previous;
   }

   TableOfContentsEntry getNext() {
      return next;
   }

   void setNext(TableOfContentsEntry next) {
      this.next = next;
   }

   String getTitle() {
      return title;
   }

   String getIndex() {
      return index;
   }

   String getFileName() {
      return fileName;
   }

   boolean isHeader() {
      return fileName == null;
   }

   boolean isSubSection() {
      return index.indexOf('.') != index.lastIndexOf('.');
   }
}