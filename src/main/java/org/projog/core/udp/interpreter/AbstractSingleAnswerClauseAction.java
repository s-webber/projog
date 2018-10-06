/*
 * Copyright 2013-2014 S. Webber
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
package org.projog.core.udp.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.KnowledgeBase;
import org.projog.core.term.Term;
import org.projog.core.term.Unifier;
import org.projog.core.term.Variable;

/**
 * A clause that will not succeed more than once.
 * <p>
 * e.g. {@code p(X) :- X<2.}
 */
abstract class AbstractSingleAnswerClauseAction implements ClauseAction {
   protected final KnowledgeBase kb;
   private final Term[] originalConsequentArgs;

   AbstractSingleAnswerClauseAction(KnowledgeBase kb, Term[] consequentArgs) {
      this.kb = kb;
      this.originalConsequentArgs = consequentArgs;
   }

   @Override
   public boolean evaluate(Term[] queryArgs) {
      final Map<Variable, Variable> sharedVariables = new HashMap<>();
      final Term[] newConsequentArgs = new Term[originalConsequentArgs.length];
      for (int i = 0; i < originalConsequentArgs.length; i++) {
         newConsequentArgs[i] = originalConsequentArgs[i].copy(sharedVariables);
      }

      if (Unifier.preMatch(queryArgs, newConsequentArgs) && evaluateAntecedant(sharedVariables)) {
         return true;
      } else {
         return false;
      }
   }

   protected abstract boolean evaluateAntecedant(Map<Variable, Variable> sharedVariables);

   @Override
   public AbstractSingleAnswerClauseAction getFree() {
      return this;
   }

   @Override
   public final boolean couldReevaluationSucceed() {
      return false;
   }
}
