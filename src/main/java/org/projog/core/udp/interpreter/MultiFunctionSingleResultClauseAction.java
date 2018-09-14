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

import static org.projog.core.KnowledgeBaseUtils.toArrayOfConjunctions;

import java.util.Map;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;

/**
 * A functions whose body is a conjunction consisting only of non-retryable predicates.
 * <p>
 * e.g. {@code p(A,B,C,D) :- A<B, B<C, C<D.}
 */
public final class MultiFunctionSingleResultClauseAction extends AbstractSingleAnswerClauseAction {
   private final PredicateFactory[] predicateFactories;
   private final Term[] originalTerms;

   MultiFunctionSingleResultClauseAction(KnowledgeBase kb, ClauseModel ci) {
      super(kb, ci.getConsequent().getArgs());
      originalTerms = toArrayOfConjunctions(ci.getAntecedant());
      predicateFactories = new PredicateFactory[originalTerms.length];
      for (int i = 0; i < originalTerms.length; i++) {
         predicateFactories[i] = kb.getPredicateFactory(originalTerms[i]);
      }
   }

   @Override
   protected boolean evaluateAntecedant(Map<Variable, Variable> sharedVariables) {
      for (int i = 0; i < originalTerms.length; i++) {
         Term t = originalTerms[i].copy(sharedVariables);
         if (!predicateFactories[i].getPredicate(t.getArgs()).evaluate()) {
            return false;
         }
      }
      return true;
   }
}
