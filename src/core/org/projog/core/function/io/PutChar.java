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
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>put_char(X)</code> - writes a character.
 * <p>
 * <code>put_char(X)</code> - writes character <code>X</code> to the output stream.
 * </p>
 * <p>
 * Writes to the current output stream. Succeeds only once and the operation is not undone on backtracking.
 * </p>
 */
public final class PutChar extends AbstractSingletonPredicate {
   private FileHandles fileHandles;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term argument) {
      String textToOutput = getAtomName(argument);
      fileHandles.getCurrentOutputStream().print(textToOutput);
      return true;
   }
}
