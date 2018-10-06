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
import org.projog.core.Predicate;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;

/**
 * A functions whose body is a retryable predicate.
 * <p>
 * e.g. {@code p(X) :- repeat(X).}
 */
public final class SingleFunctionMultiResultClauseAction extends AbstractMultiAnswerClauseAction {
   private final Term originalAntecedant;
   private Term antecedant;
   private Predicate predicate;

   SingleFunctionMultiResultClauseAction(KnowledgeBase kb, ClauseModel ci) {
      super(kb, ci.getConsequent().getArgs());
      originalAntecedant = ci.getAntecedant();
   }

   private SingleFunctionMultiResultClauseAction(SingleFunctionMultiResultClauseAction original) {
      super(original);
      originalAntecedant = original.originalAntecedant;
   }

   @Override
   protected boolean evaluateAntecedant(Map<Variable, Variable> sharedVariables) {
      antecedant = originalAntecedant.copy(sharedVariables);
      predicate = kb.getPredicateFactory(antecedant).getPredicate(antecedant.getArgs());
      return predicate.evaluate();
   }

   @Override
   protected boolean reEvaluateAntecedant() {
      return predicate.couldReevaluationSucceed() && predicate.evaluate();
   }

   @Override
   public SingleFunctionMultiResultClauseAction getFree() {
      return new SingleFunctionMultiResultClauseAction(this);
   }

   @Override
   public boolean couldReevaluationSucceed() {
      return predicate == null || predicate.couldReevaluationSucceed();
   }
}
