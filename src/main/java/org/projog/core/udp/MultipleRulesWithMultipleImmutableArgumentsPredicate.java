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
public final class MultipleRulesWithMultipleImmutableArgumentsPredicate implements Predicate, PredicateFactory { // TODO split into two classes
   private Term[] args;
   /** Public so can be used directly be code compiled at runtime. */
   public final Term[][] data;
   /** Public so can be used directly be code compiled at runtime. */
   public final SpyPoints.SpyPoint spyPoint;
   private final int numClauses;
   private final boolean isDebugEnabled;
   private int ctr;
   private boolean retrying;

   public MultipleRulesWithMultipleImmutableArgumentsPredicate(Term[] args, Term[][] data, SpyPoints.SpyPoint spyPoint) {
      this.args = args;
      this.data = data;
      this.numClauses = data.length;
      this.spyPoint = spyPoint;
      this.isDebugEnabled = spyPoint != null && spyPoint.isEnabled();
   }

   @Override
   public Predicate getPredicate(Term... args) {
      return new MultipleRulesWithMultipleImmutableArgumentsPredicate(args, data, spyPoint);
   }

   @Override
   public boolean evaluate() {
      if (retrying) {
         if (isDebugEnabled) {
            spyPoint.logRedo(this, args);
         }
         TermUtils.backtrack(args);
      } else {
         if (isDebugEnabled) {
            spyPoint.logCall(this, args);
         }
         retrying = true;
      }
      while (ctr < numClauses) {
         if (TermUtils.unify(args, data[ctr++])) {
            if (isDebugEnabled) {
               spyPoint.logExit(this, args, ctr);
            }
            return true;
         }
      }
      if (isDebugEnabled) {
         spyPoint.logFail(this, args);
      }
      return false;
   }

   @Override
   public boolean couldReevaluationSucceed() {
      return ctr < numClauses;
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }
}
