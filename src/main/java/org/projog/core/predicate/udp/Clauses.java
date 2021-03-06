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
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

// TODO add Javadoc and review method and variable names
class Clauses {
   private static final Clauses EMPTY = new Clauses(Collections.emptyList(), new int[0]);

   private final List<ClauseAction> clauses;
   private final int[] immutableColumns;

   static Clauses createFromModels(KnowledgeBase kb, List<ClauseModel> models) {
      List<ClauseAction> actions = new ArrayList<>();
      for (ClauseModel model : models) {
         ClauseAction action = ClauseActionFactory.createClauseAction(kb, model);
         actions.add(action);
      }
      return createFromActions(kb, actions, null);
   }

   static Clauses createFromActions(KnowledgeBase kb, List<ClauseAction> actions, Term arg) {
      if (actions.isEmpty()) {
         return EMPTY;
      }

      int numArgs = actions.get(0).getModel().getConsequent().getNumberOfArguments();
      boolean[] muttableColumns = createArray(numArgs, arg);
      int muttableColumnCtr = count(muttableColumns);

      List<ClauseAction> clauses = new ArrayList<>(actions.size());
      for (ClauseAction action : actions) {
         clauses.add(action);
         for (int i = 0; i < numArgs; i++) {
            if (!muttableColumns[i] && !action.getModel().getConsequent().getArgument(i).isImmutable()) {
               muttableColumns[i] = true;
               muttableColumnCtr++;
            }
         }
      }

      int[] immutableColumns = new int[numArgs - muttableColumnCtr];
      for (int i = 0, ctr = 0; ctr < immutableColumns.length; i++) {
         if (!muttableColumns[i]) {
            immutableColumns[ctr++] = i;
         }
      }

      return new Clauses(actions, immutableColumns);
   }

   private static boolean[] createArray(int numArgs, Term query) {
      boolean[] result = new boolean[numArgs];
      if (query != null) {
         for (int i = 0; i < result.length; i++) {
            Term arg = query.getArgument(i);
            result[i] = arg.isImmutable() || isAnonymousVariable(arg);
         }
      }
      return result;
   }

   private static boolean isAnonymousVariable(Term arg) { // TODO move to TermUtils?
      return arg.getType().isVariable() && ((Variable) arg.getTerm()).isAnonymous();
   }

   private static int count(boolean[] a) {
      int ctr = 0;
      for (int i = 0; i < a.length; i++) {
         if (a[i]) {
            ctr++;
         }
      }
      return ctr;
   }

   private Clauses(List<ClauseAction> actions, int[] immutableColumns) {
      this.clauses = actions;
      this.immutableColumns = immutableColumns;

   }

   int[] getImmutableColumns() {
      return immutableColumns;
   }

   ClauseAction[] getClauseActions() {
      return clauses.toArray(new ClauseAction[clauses.size()]);
   }
}
