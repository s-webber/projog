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
package org.projog.core.function.flow;

import org.projog.core.CutException;
import org.projog.core.function.AbstractPredicate;
import org.projog.core.function.AbstractPredicateFactory;

/* TEST
 %QUERY repeat, !
 %ANSWER/
 %NO

 print_first_sentence(X) :-
    atom_chars(X, Chars), member(Next, Chars), write(Next), Next=='.', !.

 %QUERY print_first_sentence('word1 word2 word3. word4 word5 word6.')
 %OUTPUT word1 word2 word3.
 %ANSWER/
 %NO

 a(x, Y) :- Y = 1, !.
 a(X, Y) :- Y = 2.

 %QUERY a(x, Y)
 %ANSWER Y = 1
 %NO

 %QUERY a(y, Y)
 %ANSWER Y = 2

 %QUERY a(z, Y)
 %ANSWER Y = 2
 */
/**
 * <code>!</code> - the "cut".
 * <p>
 * The "cut", represented as a <code>!</code>, is a special mechanism which affects how prolog backtracks.
 * </p>
 */
public final class Cut extends AbstractPredicateFactory {
   @Override
   public AbstractPredicate getPredicate() {
      return new CutPredicate();
   }

   private final static class CutPredicate extends AbstractPredicate {
      private boolean retried = false;

      @Override
      public boolean evaluate() {
         if (retried) {
            throw CutException.CUT_EXCEPTION;
         }
         retried = true;
         return true;
      }
   }
}
