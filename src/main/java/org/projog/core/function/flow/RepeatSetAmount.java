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

import static org.projog.core.term.TermUtils.castToNumeric;

import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;

/* TEST
 %QUERY repeat(3), write('hello, world'), nl
 %OUTPUT
 % hello, world
 %
 %OUTPUT
 %ANSWER/
 %OUTPUT
 % hello, world
 %
 %OUTPUT
 %ANSWER/
 %OUTPUT
 % hello, world
 %
 %OUTPUT
 %ANSWER/

 %QUERY repeat(1)
 %ANSWER/
 %QUERY repeat(2)
 %ANSWER/
 %ANSWER/
 %QUERY repeat(3)
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %FALSE repeat(0)
 %FALSE repeat(-1)

 %QUERY repeat(X)
 %ERROR Expected Numeric but got: NAMED_VARIABLE with value: X
*/
/**
 * <code>repeat(N)</code> - succeeds <code>N</code> times.
 */
public final class RepeatSetAmount extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term arg) {
      long n = castToNumeric(arg).getLong();
      return new RepeatSetAmountPredicate(n);
   }

   private static final class RepeatSetAmountPredicate implements Predicate {
      private final long limit;
      private int ctr;

      /** @param limit the number of times to successfully evaluate */
      private RepeatSetAmountPredicate(long limit) {
         this.limit = limit;
      }

      @Override
      public boolean evaluate() {
         return ctr++ < limit;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < limit;
      }
   }
}
