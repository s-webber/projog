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
   /** Public so can be used directly be code compiled at runtime. */
   public final Term data;
   /** Public so can be used directly be code compiled at runtime. */
   public final SpyPoints.SpyPoint spyPoint;

   public SingleRuleWithSingleImmutableArgumentPredicate(Term data, SpyPoints.SpyPoint spyPoint) {
      this.data = data;
      this.spyPoint = spyPoint;
   }

   @Override
   public boolean evaluate(Term... args) {
      if (spyPoint != null) {
         spyPoint.logCall(this, args);
      }
      final boolean result = args[0].unify(data);
      if (result) {
         if (spyPoint != null) {
            spyPoint.logExit(this, args, 1);
         }
      } else {
         if (spyPoint != null) {
            spyPoint.logFail(this, args);
         }
      }
      return result;
   }
}
