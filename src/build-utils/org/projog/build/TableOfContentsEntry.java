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

/**
 * Represents a line of the {@code manual.html} page of the website.
 * <p>
 * Each {@code TableOfContentsEntry} represents a header, description or link contained in <a
 * href="http://projog.org/manual.html">manual.html<a>.
 * </p>
 */
class TableOfContentsEntry {
   /** The name to display in the link. */
   private final String title;
   /** Position in documentation hierarchy (e.g. 2.3) */
   private final String index;
   /** The file name the entry should link to. */
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
      return !isDescription() && fileName == null;
   }

   boolean isSubSection() {
      return !isDescription() && index.indexOf('.') != index.lastIndexOf('.');
   }

   boolean isDescription() {
      return index == null;
   }

   boolean isLink() {
      return !isHeader() && !isDescription();
   }
}
