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

import static org.projog.core.FileHandles.USER_OUTPUT_HANDLE;
import static org.projog.core.KnowledgeBaseUtils.getFileHandles;

import org.projog.core.FileHandles;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>told</code> - closes the current output stream.
 * <p>
 * The new input stream becomes <code>user_output</code>.
 */
public final class Told extends AbstractSingletonPredicate {
   private FileHandles fileHandles;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate() {
      Term handle = fileHandles.getCurrentOutputHandle();
      close(handle);
      fileHandles.setOutput(USER_OUTPUT_HANDLE);
      return true;
   }

   private void close(Term handle) {
      try {
         fileHandles.close(handle);
      } catch (Exception e) {
         throw new ProjogException("Unable to close stream for: " + handle, e);
      }
   }
}
