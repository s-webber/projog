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

import java.io.PrintStream;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%?- write( 1+1 )
%OUTPUT 1 + 1
%YES

%?- write( '+'(1,1) )
%OUTPUT 1 + 1
%YES

%?- write(hello), nl, write(world), nl
%OUTPUT
%hello
%world
%
%OUTPUT
%YES

%?- writeln(hello), writeln(world)
%OUTPUT
%hello
%world
%
%OUTPUT
%YES
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
 * <p>
 * <code>writeln(X)</code> writes the term <code>X</code> to the current output stream, followed by a new line
 * character. <code>writeln(X)</code> can be used as an alternative to <code>write(X), nl</code>.
 * </p>
 *
 * @see #toString(Term)
 */
public final class Write extends AbstractSingleResultPredicate {
   private final boolean addNewLine;

   public static Write write() {
      return new Write(false);
   }

   public static Write writeln() {
      return new Write(true);
   }

   private Write(boolean addNewLine) {
      this.addNewLine = addNewLine;
   }

   @Override
   protected boolean evaluate(Term arg) {
      writeString(toString(arg));
      return true;
   }

   private void writeString(String s) {
      PrintStream os = getFileHandles().getCurrentOutputStream();
      if (addNewLine) {
         os.println(s);
      } else {
         os.print(s);
      }
   }

   private String toString(Term t) {
      return getTermFormatter().formatTerm(t);
   }
}
