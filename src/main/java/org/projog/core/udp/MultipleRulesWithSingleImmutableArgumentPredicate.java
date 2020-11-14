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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.projog.core.Predicate;
import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.function.SucceedsFixedAmountPredicate;
import org.projog.core.term.Term;

/**
 * Provides an optimised implementation for evaluating a particular subset of user defined predicates that have an arity
 * of one and a number of clauses that all have a body of {@code true} (i.e. are "facts" rather than "rules") and are
 * immutable (i.e. have variables). Example: <pre>
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
   private final Term[] masterData;
   private final SpyPoints.SpyPoint spyPoint;
   private final Map<Term, SucceedsFixedAmountPredicate> index;

   MultipleRulesWithSingleImmutableArgumentPredicate(Term[] data, SpyPoints.SpyPoint spyPoint) {
      this.masterData = data;
      this.spyPoint = spyPoint;

      index = new HashMap<>(data.length);
      for (Term t : data) {
         SucceedsFixedAmountPredicate previous = index.put(t, AbstractSingletonPredicate.TRUE);
         if (previous != null) {
            index.put(t, previous.increment());
         }
      }
   }

   @Override
   public Predicate getPredicate(Term arg) {
      if (arg.isImmutable()) {
         return getPredicateUsingIndex(arg);
      } else {
         return new RetryablePredicate(masterData, arg);
      }
   }

   private Predicate getPredicateUsingIndex(Term arg) {
      SucceedsFixedAmountPredicate match = index.getOrDefault(arg, AbstractSingletonPredicate.FAIL);

      if (spyPoint != null && spyPoint.isEnabled()) {
         Term[] data = new Term[match.getCount()];
         Arrays.fill(data, arg);
         return new RetryablePredicate(data, arg);
      } else {
         return match.getFree();
      }
   }

   private class RetryablePredicate implements Predicate {
      private final Term[] data;
      private final int numClauses;
      private final Term arg;
      private final boolean isDebugEnabled;
      private int ctr;
      private boolean retrying;

      RetryablePredicate(Term[] data, Term arg) {
         this.data = data;
         this.numClauses = data.length;
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
