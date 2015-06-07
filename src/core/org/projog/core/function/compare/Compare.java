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
package org.projog.core.function.compare;

import static org.projog.core.term.TermComparator.TERM_COMPARATOR;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
 %QUERY compare(X, a, z)
 %ANSWER X=<

 %QUERY compare(X, a, a)
 %ANSWER X==

 %QUERY compare(X, z, a)
 %ANSWER X=>

 %FALSE compare(<, z, a)

 %TRUE compare(>, z, a)

 % All floating point numbers are less than all integers
 %QUERY compare(X, 1.0, 1)
 %ANSWER X=<

 %QUERY compare(X, a, Y)
 %ANSWER 
 % X=>
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER

 %FALSE compare(=, X, Y)
 
 %QUERY X=Y, compare(=, X, Y)
 %ANSWER 
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 */
/**
 * <code>compare(X,Y,Z)</code> - compares arguments.
 * <p>
 * Compares the second and third arguments.
 * <ul>
 * <li>If second is greater than third then attempts to unify first argument with <code>&gt;</code></li>
 * <li>If second is less than third then attempts to unify first argument with <code>&lt;</code></li>
 * <li>If second is equal to third then attempts to unify first argument with <code>=</code></li>
 * </ul>
 */
public final class Compare extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term result, Term t1, Term t2) {
      final int i = TERM_COMPARATOR.compare(t1, t2);
      final String symbol;
      if (i < 0) {
         symbol = "<";
      } else if (i > 0) {
         symbol = ">";
      } else {
         symbol = "=";
      }
      return result.unify(new Atom(symbol));
   }
}
