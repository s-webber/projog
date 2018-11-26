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

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.SpyPoints;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/**
 * Provides an optimised implementation for evaluating a particular subset of user defined predicates that have an arity
 * greater than one and a number of clauses that all have a body of {@code true} and no shared variables. Example: <pre>
 * p(a,b,c).
 * p(x,y,z).
 * p(1,2,3).
 * </pre>
 *
 * @see SingleRuleWithSingleImmutableArgumentPredicate
 * @see SingleRuleWithMultipleImmutableArgumentsPredicate
 * @see MultipleRulesWithSingleImmutableArgumentPredicate
 */
public final class MultipleRulesWithMultipleImmutableArgumentsPredicate implements PredicateFactory {
   private final Term[][] data;
   private final SpyPoints.SpyPoint spyPoint;
   private final int numClauses;

   MultipleRulesWithMultipleImmutableArgumentsPredicate(Term[][] data, SpyPoints.SpyPoint spyPoint) {
      this.data = data;
      this.numClauses = data.length;
      this.spyPoint = spyPoint;
   }

   @Override
   public Predicate getPredicate(Term... args) {
      return new RetryablePredicate(args);
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   private class RetryablePredicate implements Predicate {
      private final Term[] args;
      private final boolean isDebugEnabled;
      private int ctr;
      private boolean retrying;

      RetryablePredicate(Term[] args) {
         this.args = args;
         this.isDebugEnabled = spyPoint != null && spyPoint.isEnabled();
      }

      @Override
      public boolean evaluate() {
         if (retrying) {
            logRedo();
            TermUtils.backtrack(args);
         } else {
            logCall();
            retrying = true;
         }

         while (ctr < numClauses) {
            if (TermUtils.unify(args, data[ctr++])) {
               logExit();
               return true;
            }
            TermUtils.backtrack(args);
         }

         logFail();
         return false;
      }

      private void logRedo() {
         if (isDebugEnabled) {
            spyPoint.logRedo(this, args);
         }
      }

      private void logCall() {
         if (isDebugEnabled) {
            spyPoint.logCall(this, args);
         }
      }

      private void logExit() {
         if (isDebugEnabled) {
            spyPoint.logExit(this, args, ctr);
         }
      }

      private void logFail() {
         if (isDebugEnabled) {
            spyPoint.logFail(this, args);
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < numClauses;
      }
   }
}
