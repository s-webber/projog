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

import org.projog.core.event.SpyPoints.SpyPoint;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.SucceedsNeverPredicate;
import org.projog.core.predicate.SucceedsOncePredicate;
import org.projog.core.term.Term;

public final class PredicateUtils {
   public static final SucceedsOncePredicate TRUE = SucceedsOncePredicate.SINGLETON;

   public static final SucceedsNeverPredicate FALSE = SucceedsNeverPredicate.SINGLETON;

   private PredicateUtils() {
   }

   public static Predicate toPredicate(boolean result) {
      return result ? TRUE : FALSE;
   }

   static Predicate createSingleClausePredicate(ClauseAction clause, SpyPoint spyPoint, Term[] args) {
      if (clause.isRetryable()) {
         return new SingleRetryableRulePredicateFactory.RetryableRulePredicate(clause, spyPoint, args);
      } else {
         return SingleNonRetryableRulePredicateFactory.evaluateClause(clause, spyPoint, args);
      }
   }

   static Predicate createFailurePredicate(SpyPoint spyPoint, Term[] args) {
      if (spyPoint.isEnabled()) {
         spyPoint.logCall(PredicateUtils.class, args);
         spyPoint.logFail(PredicateUtils.class, args);
      }
      return PredicateUtils.FALSE;
   }
}
