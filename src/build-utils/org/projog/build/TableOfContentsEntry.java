/*
 * Copyright 2013 S Webber
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