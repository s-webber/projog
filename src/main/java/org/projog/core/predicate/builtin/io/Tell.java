/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.builtin.io;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.ProjogException;
import org.projog.core.io.FileHandles;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
%LINK prolog-io
*/
/**
 * <code>tell(X)</code> - opens a file and sets it as the current output stream.
 * <p>
 * If <code>X</code> refers to a handle, rather than a filename, then the current output stream is set to the stream
 * represented by the handle.
 */
public final class Tell extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term source) {
      String fileName = getAtomName(source);
      try {
         FileHandles fileHandles = getFileHandles();
         if (!fileHandles.isHandle(fileName)) {
            Atom handle = fileHandles.openOutput(fileName);
            fileHandles.setOutput(handle);
         } else {
            fileHandles.setOutput(source);
         }
         return true;
      } catch (Exception e) {
         throw new ProjogException("Unable to open output for: " + source, e);
      }
   }
}
