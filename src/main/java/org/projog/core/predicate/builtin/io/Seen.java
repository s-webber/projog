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

import static org.projog.core.io.FileHandles.USER_INPUT_HANDLE;

import org.projog.core.ProjogException;
import org.projog.core.io.FileHandles;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%LINK prolog-io
*/
/**
 * <code>seen</code> - closes the current input stream.
 * <p>
 * The new input stream becomes <code>user_input</code>.
 */
public final class Seen extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate() {
      FileHandles fileHandles = getFileHandles();
      Term handle = fileHandles.getCurrentInputHandle();
      close(fileHandles, handle);
      fileHandles.setInput(USER_INPUT_HANDLE);
      return true;
   }

   private void close(FileHandles fileHandles, Term handle) {
      try {
         fileHandles.close(handle);
      } catch (Exception e) {
         throw new ProjogException("Unable to close stream for: " + handle, e);
      }
   }
}
