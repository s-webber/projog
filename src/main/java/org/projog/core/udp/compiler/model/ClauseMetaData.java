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

import static org.projog.core.KnowledgeBaseUtils.toArrayOfConjunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;

public final class ClauseMetaData {
   private final int clauseIdx;
   private final ConsequentMetaData consequentElement;
   private final List<AntecedentElementMetaData> elements;
   private final Map<String, ClauseVariableMetaData> allVariablesInClause = new HashMap<>();

   ClauseMetaData(ClauseModel cm, KnowledgeBase kb, int clauseIdx) {
      this.clauseIdx = clauseIdx;
      this.consequentElement = createConsequentElement(cm);
      this.elements = createAntecedentElements(kb, cm);
   }

   public int getClauseIdx() {
      return clauseIdx;
   }

   public ConsequentMetaData getConsequentElement() {
      return consequentElement;
   }

   public List<AntecedentElementMetaData> getElements() {
      return elements;
   }

   public Map<String, ClauseVariableMetaData> getAllVariablesInClause() {
      return allVariablesInClause;
   }

   private ConsequentMetaData createConsequentElement(ClauseModel cm) {
      Term consequent = cm.getConsequent();
      Map<String, ClauseVariableMetaData> variablesInConsequent = getVariablesInTerm(consequent, -1);
      return new ConsequentMetaData(consequent, variablesInConsequent);
   }

   private List<AntecedentElementMetaData> createAntecedentElements(KnowledgeBase kb, ClauseModel cm) {
      Term[] conjunctions = toArrayOfConjunctions(cm.getAntecedant());
      List<AntecedentElementMetaData> elements = new ArrayList<>(conjunctions.length);
      for (Term term : conjunctions) {
         int elementIdx = elements.size();
         Map<String, ClauseVariableMetaData> variablesInAntecedant = getVariablesInTerm(term, elementIdx);
         PredicateFactory predicateFactory = kb.getPredicateFactory(PredicateKey.createForTerm(term));
         AntecedentElementMetaData element = new AntecedentElementMetaData(predicateFactory, term, variablesInAntecedant, this, elementIdx);
         elements.add(element);
      }

      return elements;
   }

   private Map<String, ClauseVariableMetaData> getVariablesInTerm(Term term, int elementIdx) {
      Map<String, ClauseVariableMetaData> variablesInAntecedant = new HashMap<>();

      for (Variable v : TermUtils.getAllVariablesInTerm(term)) {
         if (!v.isAnonymous()) {
            String id = v.getId();
            ClauseVariableMetaData cmdv = new ClauseVariableMetaData(id, allVariablesInClause.get(id), this, elementIdx);
            allVariablesInClause.put(id, cmdv);
            variablesInAntecedant.put(id, cmdv);
         }
      }

      return variablesInAntecedant;
   }

   public int getIndexOfFirstRetryableElement() {
      for (int i = 0; i < elements.size(); i++) {
         if (elements.get(i).isRetryable()) {
            return i;
         }
      }
      return elements.size();
   }

   public boolean hasCutWhichStopsReevaluation() {
      return hasCutWhichStopsReevaluationOfElement(elements.size() - 1);
   }

   public boolean hasCutWhichStopsReevaluationOfElement(int elementIdx) {
      for (int i = elementIdx; i > -1; i--) {
         AntecedentElementMetaData element = elements.get(i);
         if (element.isCut()) {
            return true;
         }
         if (element.isRetryable()) {
            return false;
         }
      }
      return false;
   }

   public boolean containsCut() {
      for (AntecedentElementMetaData md : elements) {
         if (md.isCut()) {
            return true;
         }
      }
      return false;
   }

   public boolean hasRetryableElements() {
      for (AntecedentElementMetaData md : elements) {
         if (md.isRetryable()) {
            return true;
         }
      }
      return false;
   }
}
