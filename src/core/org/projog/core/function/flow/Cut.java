/*
 * Copyright 2013 S Webber
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
import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 :- member(X,[X|Xs]).
 member(X,[Y|Ys]) :- member(X,Ys).
 
 print_first_sentence(X) :- 
    atom_chars(X, Chars), member(Next, Chars), write(Next), Next=='.', !.
 
 % %QUERY% print_first_sentence('word1 word2 word3. word4 word5 word6.')
 % %OUTPUT% word1 word2 word3.
 % %ANSWER/%
 % %NO%
 
 a(x, Y) :- Y = 1, !.
 a(X, Y) :- Y = 2.

 % %QUERY% a(x, Y)
 % %ANSWER% Y = 1
 % %NO%

 % %QUERY% a(y, Y)
 % %ANSWER% Y = 2

 % %QUERY% a(z, Y)
 % %ANSWER% Y = 2
 */
/**
 * <code>!</code> - the "cut".
 * <p>
 * The "cut", represented as a <code>!</code>, is a special mechanism which affects how prolog backtracks.
 * </p>
 */
public final class Cut extends AbstractRetryablePredicate {
   private boolean retried = false;

   @Override
   public Cut getPredicate(Term... args) {
      return getPredicate();
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public Cut getPredicate() {
      return new Cut();
   }

   @Override
   public boolean evaluate(Term... args) {
      return evaluate();
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate() {
      if (retried) {
         throw CutException.CUT_EXCEPTION;
      }
      retried = true;
      return true;
   }
}