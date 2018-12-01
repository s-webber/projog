/*
 * Copyright 2018 S. Webber
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
package org.projog.core.udp.compiler.model;

import java.util.Collection;
import java.util.Map;

import org.projog.core.PredicateFactory;
import org.projog.core.function.flow.Cut;
import org.projog.core.term.Term;
import org.projog.core.udp.compiler.CompiledTailRecursivePredicate;

/**
 * Contains meta-data for an element of an antecedent (i.e. body) of a clause.
 * <p>
 * If the antecedent of a clause is a conjunction then it each element of the conjunction will be represented as a
 * distinct element.
 */
public final class AntecedentElementMetaData implements ClauseElement {
   private final Term term;
   private final PredicateFactory predicateFactory;
   private final Map<String, ClauseVariableMetaData> variables;
   private final int elementIdx;
   private final ClauseMetaData clauseMetaData;

   AntecedentElementMetaData(PredicateFactory pf, Term term, Map<String, ClauseVariableMetaData> variables, ClauseMetaData clauseMetaData, int elementIdx) {
      this.predicateFactory = pf;
      this.term = term;
      this.variables = variables;
      this.clauseMetaData = clauseMetaData;
      this.elementIdx = elementIdx;
   }

   public PredicateFactory getPredicateFactory() {
      return predicateFactory;
   }

   public Term getTerm() {
      return term;
   }

   public int getElementIdx() {
      return elementIdx;
   }

   @Override
   public Collection<ClauseVariableMetaData> getVariables() {
      return variables.values();
   }

   public boolean isCut() {
      return predicateFactory instanceof Cut;
   }

   public boolean isRetryable() {
      if (predicateFactory instanceof CompiledTailRecursivePredicate) {
         boolean[] isSingleResultIfArgumentImmutable = ((CompiledTailRecursivePredicate) predicateFactory).isSingleResultIfArgumentImmutable();
         for (int i = 0; i < term.getNumberOfArguments(); i++) {
            if (isSingleResultIfArgumentImmutable[i] && term.getArgument(i).isImmutable()) {
               return false;
            }
         }
         return true;
      } else if (isCut()) {
         return false;
      } else if (isImmediatelyFollowedByCut()) {
         return false;
      } else {
         return predicateFactory.isRetryable();
      }
   }

   private boolean isImmediatelyFollowedByCut() {
      int indexOfNextElement = elementIdx + 1;
      if (indexOfNextElement == clauseMetaData.getElements().size()) {
         return false;
      } else {
         return clauseMetaData.getElements().get(indexOfNextElement).isCut();
      }
   }
}
