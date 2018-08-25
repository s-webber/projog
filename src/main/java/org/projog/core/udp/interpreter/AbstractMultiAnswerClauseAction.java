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
 * A clause that can succeed more than once.
 * <p>
 * e.g. {@code p(X) :- repeat(X).}
 */
abstract class AbstractMultiAnswerClauseAction implements ClauseAction {
   protected KnowledgeBase kb;
   private final Term[] originalConsequentArgs;
   private Map<Variable, Variable> sharedVariables;

   AbstractMultiAnswerClauseAction(KnowledgeBase kb, Term[] consequentArgs) {
      this.kb = kb;
      this.originalConsequentArgs = consequentArgs;
   }

   AbstractMultiAnswerClauseAction(AbstractMultiAnswerClauseAction action) {
      this(action.kb, action.originalConsequentArgs);
   }

   @Override
   public boolean evaluate(Term[] queryArgs) {
      if (sharedVariables == null) {
         sharedVariables = new HashMap<>();
         Term[] consequentArgs = new Term[originalConsequentArgs.length];
         for (int i = 0; i < consequentArgs.length; i++) {
            consequentArgs[i] = originalConsequentArgs[i].copy(sharedVariables);
         }
         boolean matched = Unifier.preMatch(queryArgs, consequentArgs);
         if (matched) {
            return evaluateAntecedant(sharedVariables);
         } else {
            return false;
         }
      } else {
         return reEvaluateAntecedant();
      }
   }

   protected abstract boolean evaluateAntecedant(Map<Variable, Variable> sharedVariables);

   protected abstract boolean reEvaluateAntecedant();
}
