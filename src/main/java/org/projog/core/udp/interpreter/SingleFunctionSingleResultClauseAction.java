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

import java.util.Map;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;

/**
 * A fact whose body consists of a single non-retryable predicate.
 * <p>
 * e.g. {@code p(X) :- X<2.}
 */
public final class SingleFunctionSingleResultClauseAction extends AbstractSingleAnswerClauseAction {
   private final Term originalAntecedant;
   private final PredicateFactory ef;

   SingleFunctionSingleResultClauseAction(KnowledgeBase kb, ClauseModel ci) {
      super(kb, ci.getConsequent().getArgs());
      originalAntecedant = ci.getAntecedant();
      ef = kb.getPredicateFactory(originalAntecedant);
   }

   @Override
   protected boolean evaluateAntecedant(Map<Variable, Variable> sharedVariables) {
      Term antecedant = originalAntecedant.copy(sharedVariables);
      return ef.getPredicate(antecedant.getArgs()).evaluate();
   }
}
