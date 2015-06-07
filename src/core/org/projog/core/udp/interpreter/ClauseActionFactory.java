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

import static org.projog.core.KnowledgeBaseUtils.isConjunction;
import static org.projog.core.KnowledgeBaseUtils.isSingleAnswer;

import java.util.HashSet;
import java.util.Set;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.function.bool.True;
import org.projog.core.function.flow.Cut;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.udp.ClauseModel;

/**
 * Constructs new {@link ClauseAction} instances.
 */
public final class ClauseActionFactory {
   /**
    * Returns a new {@link ClauseAction} based on the specified {@link ClauseModel}.
    */
   public static ClauseAction getClauseAction(KnowledgeBase kb, ClauseModel clauseModel) {
      Term consequent = clauseModel.getConsequent();
      Term antecedant = clauseModel.getAntecedant();
      if (antecedant.getType().isVariable()) {
         return new SingleFunctionMultiResultClauseAction(kb, clauseModel);
      }
      PredicateFactory ef = kb.getPredicateFactory(antecedant);

      if (ef.getClass() == True.class) {
         return createClauseActionWithNoAntecedant(consequent);
      } else if (ef.getClass() == Cut.class) {
         return new CutClauseAction(kb, consequent.getArgs());
      } else if (isSingleAnswer(kb, antecedant)) {
         if (isConjunction(antecedant)) {
            return new MultiFunctionSingleResultClauseAction(kb, clauseModel);
         } else {
            return new SingleFunctionSingleResultClauseAction(kb, clauseModel);
         }
      } else {
         // NOTE: if it can give more than one result per call to evaluate, 
         // a conjunction is treated as a single function (not an array of many functions) 
         return new SingleFunctionMultiResultClauseAction(kb, clauseModel);
      }
   }

   private static ClauseAction createClauseActionWithNoAntecedant(Term consequent) {
      if (consequent.getNumberOfArguments() == 0) {
         return new AlwaysMatchedClauseAction(null);
      }

      // if all non-shared variables then always true
      // if all concrete terms (no variables) then reusable
      boolean hasVariables = false;
      boolean hasConcreteTerms = false;
      boolean hasSharedVariables = false;
      Set<Term> variables = new HashSet<>();
      for (Term t : consequent.getArgs()) {
         if (t.getType() == TermType.NAMED_VARIABLE) {
            hasVariables = true;
            if (!variables.add(t)) {
               hasSharedVariables = true;
            }
         } else {
            hasConcreteTerms = true;
            if (t.isImmutable() == false) {
               hasVariables = true;
            }
         }
      }

      if (!hasSharedVariables && !hasConcreteTerms) {
         return new AlwaysMatchedClauseAction(consequent.getArgs());
      } else if (hasConcreteTerms && !hasVariables) {
         return new ImmutableArgumentsClauseAction(consequent.getArgs());
      } else {
         return new MutableArgumentsClauseAction(consequent.getArgs());
      }
   }
}
