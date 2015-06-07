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
package org.projog.core.udp.compiler;

import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateKey;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.TailRecursivePredicateMetaData;

/**
 * Defines the characteristics of a user defined predicate.
 */
final class PredicateMetaData {
   private final PredicateKey key;
   private final ClauseMetaData[] clauses;
   private final boolean isSingleResultPredicate;
   private final boolean isTailRecursive;
   private final TailRecursivePredicateMetaData recursiveFunctionMetaData;

   private final int numberArguments;

   PredicateMetaData(KnowledgeBase kb, List<ClauseModel> implications) {
      this.recursiveFunctionMetaData = TailRecursivePredicateMetaData.create(kb, implications);
      this.isTailRecursive = recursiveFunctionMetaData != null;
      this.clauses = new ClauseMetaData[implications.size()];
      for (int i = 0; i < clauses.length; i++) {
         ClauseModel clauseModel = implications.get(i);
         clauses[i] = new ClauseMetaData(kb, i, clauseModel, isTailRecursive);
      }
      this.key = PredicateKey.createForTerm(clauses[0].getConsequent());
      this.numberArguments = clauses[0].getConsequent().getNumberOfArguments();
      this.isSingleResultPredicate = (clauses.length == 1 && clauses[0].isSingleResult());
   }

   PredicateKey getPredicateKey() {
      return key;
   }

   int getNumberArguments() {
      return numberArguments;
   }

   ClauseMetaData[] getClauses() {
      return clauses;
   }

   ClauseMetaData getClause(int idx) {
      return clauses[idx];
   }

   boolean isSingleResultPredicate() {
      return isSingleResultPredicate;
   }

   boolean isTailRecursive() {
      return isTailRecursive;
   }

   boolean isTailRecursiveArgument(int idx) {
      return isTailRecursive && recursiveFunctionMetaData.isTailRecursiveArgument(idx);
   }

   boolean isPossibleSingleResultRecursiveFunction() {
      return isTailRecursive && recursiveFunctionMetaData.isPotentialSingleResult();
   }

   boolean isSingleResultIfArgumentImmutable(int idx) {
      return isTailRecursive && recursiveFunctionMetaData.isSingleResultIfArgumentImmutable(idx);
   }

   boolean isCutVariableRequired() {
      for (ClauseMetaData c : clauses) {
         if (c.containsCut()) {
            return true;
         }
      }
      return false;
   }

   boolean isMultipleAnswersClause() {
      for (ClauseMetaData c : clauses) {
         if (c.isSingleResult() == false) {
            return true;
         }
      }
      return false;
   }
}
