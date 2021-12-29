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
package org.projog.core.predicate.builtin.compare;

import static org.projog.core.math.NumericTermComparator.NUMERIC_TERM_COMPARATOR;
import static org.projog.core.term.TermUtils.toLong;

import org.projog.core.math.ArithmeticOperators;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.Term;

/* TEST
%TRUE between(1, 5, 1)
%TRUE between(1, 5, 2)
%TRUE between(1, 5, 3)
%TRUE between(1, 5, 4)
%TRUE between(1, 5, 5)

%FAIL between(1, 5, 0)
%FAIL between(1, 5, -1)
%FAIL between(1, 5, -9223372036854775808)

%FAIL between(1, 5, 6)
%FAIL between(1, 5, 7)
%FAIL between(1, 5, 9223372036854775807)

%TRUE between(-9223372036854775808, 9223372036854775807, -9223372036854775808)
%TRUE between(-9223372036854775808, 9223372036854775807, -1)
%TRUE between(-9223372036854775808, 9223372036854775807, 0)
%TRUE between(-9223372036854775808, 9223372036854775807, 1)
%TRUE between(-9223372036854775808, 9223372036854775807, 9223372036854775807)

%?- between(1, 1, X)
% X=1

%?- between(1, 2, X)
% X=1
% X=2

%?- between(1, 5, X)
% X=1
% X=2
% X=3
% X=4
% X=5

%FAIL between(5, 1, X)

%TRUE between(5-2, 2+3, 2*2)
%FAIL between(5-2, 2+3, 8-6)
*/
/**
 * <code>between(X,Y,Z)</code> - checks if a number is within a specified range.
 * <p>
 * <code>between(X,Y,Z)</code> succeeds if the integer numeric value represented by <code>Z</code> is greater than or
 * equal to the integer numeric value represented by <code>X</code> and is less than or equal to the integer numeric
 * value represented by <code>Y</code>.
 * </p>
 * <p>
 * If <code>Z</code> is an uninstantiated variable then <code>Z</code> will be successively unified with all integer
 * values in the range from <code>X</code> to <code>Y</code>.
 * </p>
 */
public final class Between extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term low, Term high, Term middle) {
      ArithmeticOperators operators = getArithmeticOperators();
      if (middle.getType().isVariable()) {
         return new Retryable(middle, toLong(operators, low), toLong(operators, high));
      } else {
         boolean result = NUMERIC_TERM_COMPARATOR.compare(low, middle, operators) < 1 && NUMERIC_TERM_COMPARATOR.compare(middle, high, operators) < 1;
         return PredicateUtils.toPredicate(result);
      }
   }

   private static class Retryable implements Predicate {
      final Term middle;
      final long max;
      long ctr;

      Retryable(Term middle, long start, long max) {
         this.middle = middle;
         this.ctr = start;
         this.max = max;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            middle.backtrack();
            IntegerNumber n = IntegerNumberCache.valueOf(ctr++);
            return middle.unify(n);
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr <= max;
      }
   }
}
