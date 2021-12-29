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
package org.projog.core.predicate.builtin.flow;

import static org.projog.core.term.TermUtils.castToNumeric;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.term.Term;

/* TEST
%?- repeat(3), write('hello, world'), nl
%OUTPUT
%hello, world
%
%OUTPUT
%YES
%OUTPUT
%hello, world
%
%OUTPUT
%YES
%OUTPUT
%hello, world
%
%OUTPUT
%YES

%?- repeat(1)
%YES
%?- repeat(2)
%YES
%YES
%?- repeat(3)
%YES
%YES
%YES
%FAIL repeat(0)
%FAIL repeat(-1)

%?- repeat(X)
%ERROR Expected Numeric but got: VARIABLE with value: X
*/
/**
 * <code>repeat(N)</code> - succeeds <code>N</code> times.
 */
public final class RepeatSetAmount extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term arg) {
      long n = castToNumeric(arg).getLong();
      return new RepeatSetAmountPredicate(n);
   }

   private static final class RepeatSetAmountPredicate implements Predicate {
      private final long limit;
      private long ctr;

      /** @param limit the number of times to successfully evaluate */
      private RepeatSetAmountPredicate(long limit) {
         this.limit = limit;
      }

      @Override
      public boolean evaluate() {
         if (ctr < limit) {
            ctr++;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < limit;
      }
   }
}
