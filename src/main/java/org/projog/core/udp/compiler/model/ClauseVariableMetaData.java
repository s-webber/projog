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

/** Contains meta-data for a Prolog variable contained in the clause of a user-defined Prolog predicate. */
public final class ClauseVariableMetaData {
   private final String prologVariableName;
   private final ClauseVariableMetaData previous;
   private final ClauseMetaData clauseMetaData;
   private final int elementIdx;

   ClauseVariableMetaData(String prologVariableName, ClauseVariableMetaData previous, ClauseMetaData clauseMetaData, int elementIdx) {
      this.prologVariableName = prologVariableName;
      this.previous = previous;
      this.clauseMetaData = clauseMetaData;
      this.elementIdx = elementIdx;
   }

   public ClauseVariableMetaData previous() {
      return previous;
   }

   public String getPrologVariableName() {
      return prologVariableName;
   }

   public boolean isMemberVariable() {
      return doesRequireBacktracking() || isAssignedToMemberVariable();
   }

   private boolean doesRequireBacktracking() {
      if (isConsequentVariable()) {
         return false;
      }

      if (elementIdx < clauseMetaData.getIndexOfFirstRetryableElement()) {
         return false;
      }

      if (clauseMetaData.hasCutWhichStopsReevaluationOfElement(elementIdx)) {
         return false;
      }

      return true;
   }

   /** Is this variable assigned to a later version of itself after a backtrack point */
   private boolean isAssignedToMemberVariable() {
      ClauseVariableMetaData orphanedChild = clauseMetaData.getAllVariablesInClause().get(prologVariableName);
      if (orphanedChild == this) {
         return false;
      }

      for (int i = elementIdx + 1; i <= orphanedChild.elementIdx; i++) {
         if (clauseMetaData.getElements().get(i).isRetryable()) {
            return true;
         }
      }

      return false;
   }

   private boolean isConsequentVariable() {
      return elementIdx == -1;
   }
}
