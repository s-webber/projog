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

import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/**
 * Provides an optimised implementation for evaluating a particular subset of user defined predicates that have an arity
 * of one and a single clause that has a body of {@code true} and no shared variables. Example: <pre>
 * p(a).
 * </pre>
 *
 * @see SingleRuleWithMultipleImmutableArgumentsPredicate
 * @see MultipleRulesWithSingleImmutableArgumentPredicate
 * @see MultipleRulesWithMultipleImmutableArgumentsPredicate
 */
public final class SingleRuleWithSingleImmutableArgumentPredicate extends AbstractSingletonPredicate {
   private final Term data;
   private final SpyPoints.SpyPoint spyPoint;

   public SingleRuleWithSingleImmutableArgumentPredicate(Term data, SpyPoints.SpyPoint spyPoint) {
      this.data = data;
      this.spyPoint = spyPoint;
   }

   @Override
   public boolean evaluate(Term arg) {
      if (isSpyPointEnabled()) {
         spyPoint.logCall(this, new Term[] {arg});
      }

      final boolean result = arg.unify(data);

      if (isSpyPointEnabled()) {
         if (result) {
            spyPoint.logExit(this, new Term[] {arg}, 1);
         } else {
            spyPoint.logFail(this, new Term[] {arg});
         }
      }

      return result;
   }

   private boolean isSpyPointEnabled() {
      return spyPoint != null && spyPoint.isEnabled();
   }
}
