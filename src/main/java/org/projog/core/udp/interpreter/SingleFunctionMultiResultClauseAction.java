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
   private final Term originalAntecedent;
   private Term antecedent;
   private Predicate predicate;

   SingleFunctionMultiResultClauseAction(KnowledgeBase kb, ClauseModel ci) {
      super(kb, ci.getConsequent().getArgs());
      originalAntecedent = ci.getAntecedent();
   }

   private SingleFunctionMultiResultClauseAction(SingleFunctionMultiResultClauseAction original) {
      super(original);
      originalAntecedent = original.originalAntecedent;
   }

   @Override
   protected boolean evaluateAntecedent(Map<Variable, Variable> sharedVariables) {
      antecedent = originalAntecedent.copy(sharedVariables);
      predicate = kb.getPredicateFactory(antecedent).getPredicate(antecedent.getArgs());
      return predicate.evaluate();
   }

   @Override
   protected boolean reEvaluateAntecedent() {
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
