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
package org.projog.core.udp.interpreter;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.udp.ClauseModel;

public class Clauses {
   private final int numClauses;
   private final List<ClauseAction> clauses;
   private final int[] immutableColumns;

   public Clauses(KnowledgeBase kb, List<ClauseModel> models) {
      this.numClauses = models.size();
      this.clauses = new ArrayList<>(numClauses);
      boolean isImmutableFacts = true;
      int numArgs = models.get(0).getConsequent().getNumberOfArguments();
      boolean[] muttableColumns = new boolean[numArgs];
      int muttableColumnCtr = 0;
      for (ClauseModel model : models) {
         ClauseAction action = ClauseActionFactory.createClauseAction(kb, model);
         this.clauses.add(action);
         for (int i = 0; i < numArgs; i++) {
            if (!muttableColumns[i] && !model.getConsequent().getArgument(i).isImmutable()) {
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

   public int[] getImmutableColumns() {
      return immutableColumns;
   }

   public ClauseAction[] getClauseActions() {
      return clauses.toArray(new ClauseAction[clauses.size()]);
   }
}
