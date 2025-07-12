/*
 * Copyright 2025 S. Webber
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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.term.TermUtils.assertType;

import org.projog.core.math.ArithmeticOperators;
import org.projog.core.math.Numeric;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- sum_list([], X)
% X=0

%?- sum_list([42], X)
% X=42

%?- sum_list([42.0], X)
% X=42.0

%?- sum_list([3, 8, 180], X)
% X=191

%?- sum_list([10, 2, 4.5], X)
% X=16.5

%?- sum_list([3, 6*7, 180], X)
% X=225

%?- sum_list([3, 6*7.0, 180], X)
% X=225.0

%TRUE sum_list([2, 3, 4], 9)
%FAIL sum_list([2, 3, 4], 8)
%FAIL sum_list([2, 3, 4], 10)

%?- sum_list(X, 1)
%ERROR Expected LIST but got: VARIABLE with value: X

%?- sum_list(a, X)
%ERROR Expected LIST but got: ATOM with value: a

%?- sum_list([1, a, 3], X)
%ERROR Cannot find arithmetic operator: a/0
*/
/**
 * <code>sum_list(X,Y)</code> - sums the elements of a list.
 * <p>
 * The <code>sum_list(X,Y)</code> goal succeeds if sum of elements in the list <code>X</code> matches <code>Y</code>.
 * </p>
 */
public final class SumList extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term input, Term output) {
      ArithmeticOperators operators = getArithmeticOperators();

      boolean isLong = true;
      long longTotal = 0;
      double doubleTotal = 0;
      Term tail = input;
      while (tail.getType() != TermType.EMPTY_LIST) {
         assertType(tail, TermType.LIST);

         Term head = tail.getArgument(0);
         Numeric n = operators.getNumeric(head);
         if (isLong && n.getType() != TermType.INTEGER) {
            isLong = false;
            doubleTotal = longTotal;
         }

         if (isLong) {
            longTotal += n.getLong();
         } else {
            doubleTotal += n.getDouble();
         }

         tail = tail.getArgument(1);
      }

      Term result = isLong ? IntegerNumberCache.valueOf(longTotal) : new DecimalFraction(doubleTotal);
      return output.unify(result);
   }
}