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
import static org.projog.core.KnowledgeBaseUtils.getTermFormatter;

import org.projog.core.FileHandles;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;

/* TEST
 %QUERY write( 1+1 )
 %OUTPUT 1 + 1
 %ANSWER/
 %QUERY write( '+'(1,1) )
 %OUTPUT 1 + 1
 %ANSWER/
 */
/**
 * <code>write(X)</code> - writes a term to the output stream.
 * <p>
 * Writes the term <code>X</code> to the current output stream. <code>write</code> takes account of current operator
 * declarations - thus an infix operator will be printed out between its arguments. <code>write</code> represents lists
 * as a comma separated sequence of elements enclosed in square brackets.
 * </p>
 * <p>
 * Succeeds only once.
 * </p>
 * 
 * @see #toString(Term)
 */
public final class Write extends AbstractSingletonPredicate {
   private TermFormatter termFormatter;
   private FileHandles fileHandles;

   @Override
   protected void init() {
      termFormatter = getTermFormatter(getKnowledgeBase());
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term arg) {
      print(toString(arg));
      return true;
   }

   private String toString(Term t) {
      return termFormatter.toString(t);
   }

   private void print(String s) {
      fileHandles.getCurrentOutputStream().print(s);
   }
}
