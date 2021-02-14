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
package org.projog.core.predicate.udp;

import org.projog.core.ProjogException;
import org.projog.core.event.SpyPoints;
import org.projog.core.predicate.CutException;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.Term;

final class SingleRetryableRulePredicateFactory implements PreprocessablePredicateFactory {
   private final ClauseAction clause;
   private final SpyPoints.SpyPoint spyPoint;

   public SingleRetryableRulePredicateFactory(ClauseAction clause, SpyPoints.SpyPoint spyPoint) {
      this.clause = clause;
      this.spyPoint = spyPoint;
   }

   @Override
   public RetryableRulePredicate getPredicate(Term[] args) {
      return new RetryableRulePredicate(clause, spyPoint, args);
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   public static class RetryableRulePredicate implements Predicate {
      private final Term[] args;
      private final ClauseAction clause;
      private final SpyPoints.SpyPoint spyPoint;
      private final boolean isSpyPointEnabled;
      private Predicate p;

      public RetryableRulePredicate(ClauseAction clause, SpyPoints.SpyPoint spyPoint, Term[] queryArgs) {
         this.clause = clause;
         this.spyPoint = spyPoint;
         this.args = queryArgs;
         this.isSpyPointEnabled = spyPoint.isEnabled();
      }

      @Override
      public boolean evaluate() {
         try {
            if (p == null) {
               if (isSpyPointEnabled) {
                  spyPoint.logCall(this, args);
               }
               p = clause.getPredicate(args);
            } else if (isSpyPointEnabled) {
               spyPoint.logRedo(this, args);
            }

            if (p.evaluate()) { // TODO p.couldReevaluationSucceed() &&
               if (isSpyPointEnabled) {
                  spyPoint.logExit(this, args, clause.getModel());
               }
               return true;
            } else {
               if (isSpyPointEnabled) {
                  spyPoint.logFail(this, args);
               }
               return false;
            }
         } catch (CutException e) {
            if (isSpyPointEnabled) {
               spyPoint.logFail(SingleNonRetryableRulePredicateFactory.class, args);
            }
            return false;
         } catch (ProjogException pe) {
            pe.addClause(clause.getModel());
            throw pe;
         } catch (Throwable t) {
            ProjogException pe = new ProjogException("Exception processing: " + spyPoint.getPredicateKey(), t);
            pe.addClause(clause.getModel());
            throw pe;
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return p == null || p.couldReevaluationSucceed();
      }
   }

   @Override
   public PredicateFactory preprocess(Term arg) {
      if (ClauseActionFactory.isMatch(clause, arg.getArgs())) {
         return this;
      } else {
         return new NeverSucceedsPredicateFactory(spyPoint);
      }
   }
}
