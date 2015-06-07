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
package org.projog.core.function.io;

import static org.projog.core.KnowledgeBaseUtils.getFileHandles;
import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.FileHandles;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>see(X)</code> - opens a file and sets it as the current input stream.
 * <p>
 * If <code>X</code> refers to a handle, rather than a filename, then the current input stream is set to the stream
 * represented by the handle.
 */
public final class See extends AbstractSingletonPredicate {
   private FileHandles fileHandles;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term source) {
      String fileName = getAtomName(source);
      try {
         if (!fileHandles.isHandle(fileName)) {
            Atom handle = fileHandles.openInput(fileName);
            fileHandles.setInput(handle);
         } else {
            fileHandles.setInput(source);
         }
         return true;
      } catch (Exception e) {
         throw new ProjogException("Unable to open input for: " + source, e);
      }
   }
}
