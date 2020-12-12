/*
 * Copyright 2020 S. Webber
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
package org.projog.core.function.list;

import static org.projog.core.KnowledgeBaseUtils.getArithmeticOperators;
import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;
import static org.projog.core.term.TermUtils.assertType;

import org.projog.core.ArithmeticOperators;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %FALSE min_list([], X)
 %FALSE max_list([], X)

 %TRUE min_list([1,2,3], 1)
 %FALSE min_list([1,2,3], 2)
 %FALSE min_list([1,2,3], 3)

 %FALSE max_list([1,2,3], 1)
 %FALSE max_list([1,2,3], 2)
 %TRUE max_list([1,2,3], 3)

 %TRUE min_list([7], 7)
 %TRUE max_list([7], 7)

 %TRUE min_list([2*7], 14)
 %FALSE min_list([2*7], 2*7)
 %FALSE min_list([14], 2*7)

 %TRUE max_list([2*7], 14)
 %FALSE min_list([2*7], 2*7)
 %FALSE max_list([14], 2*7)

 %QUERY min_list([11*2, 7*3, 92/4], X)
 %ANSWER X=21
 %QUERY max_list([11*2, 7*3, 92/4], X)
 %ANSWER X=23

 %QUERY min_list([Y], X)
 %ERROR Cannot get Numeric for term: Y of type: VARIABLE
 %QUERY max_list([[1]], X)
 %ERROR Cannot get Numeric for term: .(1, []) of type: LIST

 %QUERY min_list([a], X)
 %ERROR Cannot find arithmetic operator: a/0
 %QUERY max_list([q(1,2)], X)
 %ERROR Cannot find arithmetic operator: q/2

 %QUERY min_list(Y, X)
 %ERROR Expected LIST but got: VARIABLE with value: Y
 %QUERY max_list(a, X)
 %ERROR Expected LIST but got: ATOM with value: a
 */
/**
 * <code>min_list</code> / <code>max_list</code>
 * <p>
 * <ul>
 * <li><code>min_list(Min, List)</code> - True if Min is the smallest number in List. Fails if List is empty.</li>
 * <li><code>max_list(Max, List)</code> - True if Max is the largest number in List. Fails if List is empty.</li>
 * </ul>
 */
public final class ExtremumList extends AbstractSingletonPredicate {
   private final boolean findMinimum;

   private ArithmeticOperators operators;

   public static ExtremumList minList() {
      return new ExtremumList(true);
   }

   public static ExtremumList maxList() {
      return new ExtremumList(false);
   }

   private ExtremumList(boolean findMinimum) {
      this.findMinimum = findMinimum;
   }

   @Override
   public final void init() {
      operators = getArithmeticOperators(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term input, Term output) {
      if (input.getType() == TermType.EMPTY_LIST) {
         return false;
      }
      assertType(input, TermType.LIST);

      Numeric result = operators.getNumeric(input.getArgument(0));
      Term tail = input.getArgument(1);
      while (tail.getType() != TermType.EMPTY_LIST) {
         assertType(tail, TermType.LIST);

         Numeric next = operators.getNumeric(tail.getArgument(0));
         int diff = findMinimum ? NUMERIC_TERM_COMPARATOR.compare(next, result) : NUMERIC_TERM_COMPARATOR.compare(result, next);
         if (diff < 0) {
            result = next;
         }

         tail = tail.getArgument(1);
      }

      return output.unify(result);
   }
}
