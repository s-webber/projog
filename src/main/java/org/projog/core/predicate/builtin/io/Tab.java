/*
 * Copyright 2018 S. Webber
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
%?- write(start), tab(1), write(finish)
%OUTPUT start finish
%YES

%?- write(start), tab(2), write(finish)
%OUTPUT start  finish
%YES

%?- write(start), tab(3), write(finish)
%OUTPUT start   finish
%YES

%?- write(start), tab(32), write(finish)
%OUTPUT start                                finish
%YES

%?- write(start), tab(3+4), write(finish)
%OUTPUT start       finish
%YES

%?- write(start), tab(0), write(finish)
%OUTPUT startfinish
%YES

%?- write(start), tab(-1), write(finish)
%OUTPUT startfinish
%YES

%?- write(start), tab(3.5), write(finish)
%OUTPUT start   finish
%YES
*/
/**
 * <code>tab(X)</code> - writes <code>X</code> number of spaces to the output stream.
 */
public final class Tab extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      long numberOfSpaces = getArithmeticOperators().getNumeric(arg).getLong();
      PrintStream os = getFileHandles().getCurrentOutputStream();
      for (int i = 0; i < numberOfSpaces; i++) {
         os.print(' ');
      }
      return true;
   }
}
