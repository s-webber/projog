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
package org.projog.core.udp.compiler;

import java.util.List;

import org.projog.core.term.Term;
import org.projog.core.udp.compiler.model.AntecedentElementMetaData;
import org.projog.core.udp.compiler.model.ClauseVariableMetaData;

final class AntecedantElementState {
   private final ClauseState clauseState;
   private List<ClauseVariableMetaData> variables;
   private String predicateName;
   private final AntecedentElementMetaData element;

   AntecedantElementState(ClauseState clauseState, AntecedentElementMetaData element) {
      this.clauseState = clauseState;
      this.element = element;
   }

   ClauseState getClauseState() {
      return clauseState;
   }

   void setVariables(List<ClauseVariableMetaData> variables) {
      this.variables = variables;
   }

   void setPredicateName(String predicateName) {
      this.predicateName = predicateName;
   }

   String getPredicateName() {
      return predicateName;
   }

   List<ClauseVariableMetaData> getVariables() {
      return variables;
   }

   int getElementIdx() {
      return element.getElementIdx();
   }

   AntecedentElementMetaData getElement() {
      return element;
   }

   boolean hasPrecedingRetryableElements() {
      for (int i = 0; i < getElementIdx(); i++) {
         AntecedentElementMetaData precedingState = clauseState.getAntecedantElementState(i).element;
         if (precedingState.isRetryable()) {
            return true;
         }
      }
      return false;
   }

   boolean wouldBacktrackingInvokeCut() {
      for (int i = getElementIdx() - 1; i > -1; i--) {
         AntecedentElementMetaData precedingState = clauseState.getAntecedantElementState(i).element;
         if (precedingState.isRetryable()) {
            return false;
         } else if (precedingState.isCut()) {
            return true;
         }
      }
      return false;
   }

   Term getFirstArgument() {
      return getArgument(0);
   }

   Term getSecondArgument() {
      return getArgument(1);
   }

   private Term getArgument(int argIdx) {
      return element.getTerm().getArgument(argIdx);
   }
}
