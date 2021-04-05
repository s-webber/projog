/*
 * Copyright 2021 S. Webber
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
package org.projog.core.predicate.builtin.compound;

import static org.projog.core.term.TermUtils.castToNumeric;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.term.Term;

/* TEST
%QUERY limit(3, repeat)
%ANSWER/
%ANSWER/
%ANSWER/

%FALSE limit(-1, true)
%FALSE limit(0, true)
%TRUE limit(1, true)
%TRUE limit(2, true)

%FALSE limit(-1, fail)
%FALSE limit(0, fail)
%FALSE limit(1, fail)
%FALSE limit(2, fail)

%TRUE assert(p(a)), assert(p(b)), assert(p(c))

%FALSE limit(-1, p(X))

%FALSE limit(0, p(X))

%QUERY limit(1, p(X))
%ANSWER X=a

%QUERY limit(2, p(X))
%ANSWER X=a
%ANSWER X=b

%QUERY limit(3, p(X))
%ANSWER X=a
%ANSWER X=b
%ANSWER X=c

%QUERY limit(4, p(X))
%ANSWER X=a
%ANSWER X=b
%ANSWER X=c

%QUERY X=a, limit(3, p(X))
%ANSWER X=a

%QUERY X=b, limit(3, p(X))
%ANSWER X=b

%QUERY X=c, limit(3, p(X))
%ANSWER X=c

%FALSE X=d, limit(3, p(X))

%FALSE X=fail, limit(3, X)

%QUERY X=true, limit(3, X)
%ANSWER X=true

%QUERY X=repeat, limit(3, X)
%ANSWER X=repeat
%ANSWER X=repeat
%ANSWER X=repeat

%QUERY limit(a, true)
%ERROR Expected Numeric but got: ATOM with value: a
%QUERY limit(X, true)
%ERROR Expected Numeric but got: VARIABLE with value: X
%QUERY limit(3, X)
%ERROR Expected an atom or a predicate but got a VARIABLE with value: X
%QUERY limit(3, 999)
%ERROR Expected an atom or a predicate but got a INTEGER with value: 999
%FALSE limit(3, unknown_predicate)
 */
/**
 * <code>limit(N, X)</code> - calls the goal represented by a term a maximum number of times.
 * <p>
 * Evaluates the goal represented by <code>X</code> for a maximum of <code>N</code> attempts.
 */
public final class Limit extends AbstractPredicateFactory
{
   @Override
   public Predicate getPredicate(Term maxAttempts, Term goal) {
      PredicateFactory pf = getPredicates().getPredicateFactory(goal);
      Predicate p = pf.getPredicate(goal.getArgs());
      long n = castToNumeric(maxAttempts).getLong();
      return new LimitPredicate(p, n);
   }

   private static final class LimitPredicate implements Predicate {
      private final Predicate predicate;
      private final long limit;
      private long ctr;

      private LimitPredicate(Predicate predicate, long limit) {
         this.predicate = predicate;
         this.limit = limit;
      }

      @Override
      public boolean evaluate() {
         if (couldReevaluationSucceed()) {
            ctr++;
            return predicate.evaluate();
         } else {
            return false;
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < limit && (ctr == 0 || predicate.couldReevaluationSucceed());
      }
   }
}
