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
package org.projog.core.udp;

import org.projog.core.CutException;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.ProjogException;
import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.interpreter.ClauseAction;

final class SingleNonRetryableRulePredicate implements PredicateFactory {
   private final ClauseAction clause;
   private final SpyPoints.SpyPoint spyPoint;

   SingleNonRetryableRulePredicate(ClauseAction clause, SpyPoints.SpyPoint spyPoint) {
      this.clause = clause;
      this.spyPoint = spyPoint;
   }

   @Override
   public Predicate getPredicate(Term... args) {
      return evaluateClause(clause, spyPoint, args);
   }

   static Predicate evaluateClause(ClauseAction clause, SpyPoints.SpyPoint spyPoint, Term[] args) {
      try {
         if (spyPoint.isEnabled()) {
            spyPoint.logCall(SingleNonRetryableRulePredicate.class, args);

            final boolean result = clause.getPredicate(args).evaluate();

            if (result) {
               spyPoint.logExit(SingleNonRetryableRulePredicate.class, args, clause.getModel());
            } else {
               spyPoint.logFail(SingleNonRetryableRulePredicate.class, args);
            }

            return AbstractSingletonPredicate.toPredicate(result);
         } else {
            return AbstractSingletonPredicate.toPredicate(clause.getPredicate(args).evaluate());
         }
      } catch (CutException e) {
         if (spyPoint.isEnabled()) {
            spyPoint.logFail(SingleNonRetryableRulePredicate.class, args);
         }
         return AbstractSingletonPredicate.FAIL;
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
   public boolean isRetryable() {
      return false;
   }
}
