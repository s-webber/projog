/*
 * Copyright 2013 S Webber
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
package org.projog.core.function.kb;

import java.util.Iterator;
import java.util.Map;

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/**
 * Extended by {@code Predicate}s that unify arguments with clauses of user defined predicates.
 */
abstract class AbstractUserDefinedPredicateInspectionFunction extends AbstractRetryablePredicate {
   private Iterator<ClauseModel> implications;

   /**
    * @param clauseHead cannot be {@code null}
    * @param clauseBody can be {@code null}
    * @return {@code true} if there is a rule in the knowledge base whose consequent can be unified with
    * {@code clauseHead} and, if {@code clauseBody} is not {@code null}, whose antecedent can be unified with
    * {@code clauseBody}.
    */
   protected boolean internalEvaluate(Term clauseHead, Term clauseBody) {
      if (implications == null) {
         PredicateKey key = PredicateKey.createForTerm(clauseHead);
         Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = getKnowledgeBase().getUserDefinedPredicates();
         UserDefinedPredicateFactory userDefinedPredicate = userDefinedPredicates.get(key);
         if (userDefinedPredicate == null) {
            return false;
         }
         implications = userDefinedPredicate.getImplications();
      } else {
         clauseHead.backtrack();
         if (clauseBody != null) {
            clauseBody.backtrack();
         }
      }

      while (implications.hasNext()) {
         ClauseModel clauseModel = implications.next();
         if (unifiable(clauseHead, clauseBody, clauseModel)) {
            if (doRemoveMatches()) {
               implications.remove();
            }
            return true;
         }
      }
      return false;
   }

   private boolean unifiable(Term clauseHead, Term clauseBody, ClauseModel clauseModel) {
      Term consequent = clauseModel.getConsequent();
      Term antecedant = clauseModel.getAntecedant();
      if (clauseHead.unify(consequent)) {
         if (clauseBody == null || clauseBody.unify(antecedant)) {
            return true;
         } else {
            clauseHead.backtrack();
         }
      }
      return false;
   }

   /**
    * @return {@code true} if matching rules should be removed (retracted) from the knowledge base as part of calls to
    * {@link #internalEvaluate(Term, Term)} or {@code false} if the knowledge base should remain unaltered.
    */
   protected abstract boolean doRemoveMatches();

   @Override
   public boolean couldReEvaluationSucceed() {
      return implications == null || implications.hasNext();
   }
}