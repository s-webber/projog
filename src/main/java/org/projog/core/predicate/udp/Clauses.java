/*
 * Copyright 2020 S. Webber
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
package org.projog.core.predicate.udp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.projog.core.kb.KnowledgeBase;

class Clauses {
   private final int numClauses;
   private final List<ClauseAction> clauses;
   private final int[] immutableColumns;

   static Clauses createFromModels(KnowledgeBase kb, List<ClauseModel> models) {
      List<ClauseAction> actions = new ArrayList<>();
      for (ClauseModel model : models) {
         ClauseAction action = ClauseActionFactory.createClauseAction(kb, model);
         actions.add(action);
      }
      return new Clauses(kb, actions);
   }

   Clauses(KnowledgeBase kb, List<ClauseAction> actions) {
      if (actions.isEmpty()) {
         this.numClauses = 0;
         this.clauses = Collections.emptyList();
         this.immutableColumns = new int[0];
         return;
      }

      this.numClauses = actions.size();
      this.clauses = new ArrayList<>(numClauses);

      int numArgs = actions.get(0).getModel().getConsequent().getNumberOfArguments();
      boolean[] muttableColumns = new boolean[numArgs];
      int muttableColumnCtr = 0;
      for (ClauseAction action : actions) {
         this.clauses.add(action);
         for (int i = 0; i < numArgs; i++) {
            if (!muttableColumns[i] && !action.getModel().getConsequent().getArgument(i).isImmutable()) {
               muttableColumns[i] = true;
               muttableColumnCtr++;
            }
         }
      }
      this.immutableColumns = new int[numArgs - muttableColumnCtr];
      for (int i = 0, ctr = 0; ctr < immutableColumns.length; i++) {
         if (!muttableColumns[i]) {
            immutableColumns[ctr++] = i;
         }
      }
   }

   int[] getImmutableColumns() {
      return immutableColumns;
   }

   ClauseAction[] getClauseActions() {
      return clauses.toArray(new ClauseAction[clauses.size()]);
   }
}
