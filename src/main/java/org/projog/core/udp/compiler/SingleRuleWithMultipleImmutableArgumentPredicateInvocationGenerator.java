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
package org.projog.core.udp.compiler;

import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.udp.SingleRuleWithMultipleImmutableArgumentsPredicate;

final class SingleRuleWithMultipleImmutableArgumentPredicateInvocationGenerator implements PredicateInvocationGenerator {
   @Override
   public void generate(CompiledPredicateWriter g) {
      Term function = g.currentClause().getCurrentFunction();
      PredicateFactory ef = g.currentClause().getCurrentPredicateFactory();

      String functionVariableName = null;
      if (g.isSpyPointsEnabled()) {
         functionVariableName = g.classVariables().getPredicateFactoryVariableName(function, g.knowledgeBase());
         g.logInlinedPredicatePredicate("Call", functionVariableName, function);
      }
      Term[] data = ((SingleRuleWithMultipleImmutableArgumentsPredicate) ef).data;
      Runnable r = g.createOnBreakCallback(functionVariableName, function, null);
      for (int i = 0; i < data.length; i++) {
         g.outputEqualsEvaluation(function.getArgument(i), data[i], r);
      }
      g.logExitInlinedPredicatePredicate(functionVariableName, function, "1");
   }
}
