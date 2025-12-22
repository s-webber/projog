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
import org.projog.core.term.Term;

final class SingleNonRetryableRulePredicateFactory implements PredicateFactory {
   private final ClauseAction clause;
   private final SpyPoints.SpyPoint spyPoint;

   SingleNonRetryableRulePredicateFactory(ClauseAction clause, SpyPoints.SpyPoint spyPoint) {
      this.clause = clause;
      this.spyPoint = spyPoint;
   }

   @Override
   public Predicate getPredicate(Term term) {
      return evaluateClause(clause, spyPoint, term);
   }

   static Predicate evaluateClause(ClauseAction clause, SpyPoints.SpyPoint spyPoint, Term query) {
      try {
         if (spyPoint.isEnabled()) {
            spyPoint.logCall(SingleNonRetryableRulePredicateFactory.class, query);

            final boolean result = clause.getPredicate(query).evaluate();

            if (result) {
               spyPoint.logExit(SingleNonRetryableRulePredicateFactory.class, query, clause.getModel());
            } else {
               spyPoint.logFail(SingleNonRetryableRulePredicateFactory.class, query);
            }

            return PredicateUtils.toPredicate(result);
         } else {
            return PredicateUtils.toPredicate(clause.getPredicate(query).evaluate());
         }
      } catch (CutException e) {
         if (spyPoint.isEnabled()) {
            spyPoint.logFail(SingleNonRetryableRulePredicateFactory.class, query);
         }
         return PredicateUtils.FALSE;
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

   @Override
   public PredicateFactory preprocess(Term term) {
      if (ClauseActionFactory.isMatch(clause, term)) {
         return this;
      } else {
         return new NeverSucceedsPredicateFactory(spyPoint);
      }
   }
}
