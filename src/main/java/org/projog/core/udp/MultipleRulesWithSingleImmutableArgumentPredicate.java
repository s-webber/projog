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
package org.projog.core.udp;

import org.projog.core.Predicate;
import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;

/**
 * Provides an optimised implementation for evaluating a particular subset of user defined predicates that have an arity
 * of one and a number of clauses that all have a body of {@code true} and no shared variables. Example: <pre>
 * p(a).
 * p(b).
 * p(c).
 * </pre>
 *
 * @see SingleRuleWithSingleImmutableArgumentPredicate
 * @see SingleRuleWithMultipleImmutableArgumentsPredicate
 * @see MultipleRulesWithMultipleImmutableArgumentsPredicate
 */
public final class MultipleRulesWithSingleImmutableArgumentPredicate extends AbstractPredicateFactory {
   private final Term[] data;
   private final SpyPoints.SpyPoint spyPoint;
   private final int numClauses;

   MultipleRulesWithSingleImmutableArgumentPredicate(Term[] data, SpyPoints.SpyPoint spyPoint) {
      this.data = data;
      this.numClauses = data.length;
      this.spyPoint = spyPoint;
   }

   @Override
   public Predicate getPredicate(Term arg) {
      return new RetryablePredicate(arg);
   }

   private class RetryablePredicate implements Predicate {
      private final Term arg;
      private final boolean isDebugEnabled;
      private int ctr;
      private boolean retrying;

      RetryablePredicate(Term arg) {
         this.arg = arg;
         this.isDebugEnabled = spyPoint != null && spyPoint.isEnabled();
      }

      @Override
      public boolean evaluate() {
         if (retrying) {
            logRedo();
            arg.backtrack();
         } else {
            logCall();
            retrying = true;
         }

         while (ctr < numClauses) {
            if (arg.unify(data[ctr++])) {
               logExit();
               return true;
            }
            arg.backtrack();
         }

         logFail();
         return false;
      }

      private void logRedo() {
         if (isDebugEnabled) {
            spyPoint.logRedo(this, new Term[] {arg});
         }
      }

      private void logCall() {
         if (isDebugEnabled) {
            spyPoint.logCall(this, new Term[] {arg});
         }
      }

      private void logExit() {
         if (isDebugEnabled) {
            spyPoint.logExit(this, new Term[] {arg}, ctr);
         }
      }

      private void logFail() {
         if (isDebugEnabled) {
            spyPoint.logFail(this, new Term[] {arg});
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < numClauses;
      }
   }
}
