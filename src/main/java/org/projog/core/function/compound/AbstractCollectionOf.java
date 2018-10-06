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
package org.projog.core.function.compound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

abstract class AbstractCollectionOf extends AbstractPredicate {
   private final Term template;
   private final Term goal;
   private final Term bag;
   private final KnowledgeBase kb;
   private List<Variable> variablesNotInTemplate;
   private Iterator<Entry<Key, List<Term>>> itr;

   protected AbstractCollectionOf(Term template, Term goal, Term bag, KnowledgeBase kb) {
      this.template = template;
      this.goal = goal;
      this.bag = bag;
      this.kb = kb;
   }

   @Override
   public final boolean evaluate() {
      if (itr == null) {
         init(template, goal);
      }

      if (itr.hasNext()) {
         template.backtrack();
         Entry<Key, List<Term>> e = itr.next();
         bag.backtrack();
         bag.unify(ListFactory.createList(e.getValue()));
         for (int i = 0; i < variablesNotInTemplate.size(); i++) {
            Variable v = variablesNotInTemplate.get(i);
            v.backtrack();
            v.unify(e.getKey().terms.get(i));
         }
         return true;
      } else {
         return false;
      }
   }

   private void init(Term template, Term goal) {
      variablesNotInTemplate = getVariablesNotInTemplate(template, goal);

      Predicate predicate = KnowledgeBaseUtils.getPredicate(kb, goal);

      Map<Key, List<Term>> m = new LinkedHashMap<>();
      if (predicate.evaluate()) {
         do {
            Key key = new Key(variablesNotInTemplate);
            List<Term> l = m.get(key);
            if (l == null) {
               l = new ArrayList<>();
               m.put(key, l);
            }
            add(l, template.getTerm());
         } while (hasFoundAnotherSolution(predicate));
      }

      goal.backtrack();

      itr = m.entrySet().iterator();
   }

   protected abstract void add(List<Term> l, Term t);

   private List<Variable> getVariablesNotInTemplate(Term template, Term goal) {
      Set<Variable> variablesInGoal = TermUtils.getAllVariablesInTerm(goal);
      Set<Variable> variablesInTemplate = TermUtils.getAllVariablesInTerm(template);
      variablesInGoal.removeAll(variablesInTemplate);
      return new ArrayList<>(variablesInGoal);
   }

   private boolean hasFoundAnotherSolution(final Predicate predicate) {
      return predicate.couldReevaluationSucceed() && predicate.evaluate();
   }

   @Override
   public final boolean couldReevaluationSucceed() {
      return itr == null || itr.hasNext();
   }

   /** Represents a combination of possible values for the variables contained in the goal. */
   private static class Key {
      final List<Term> terms;

      Key(List<Variable> variables) {
         terms = new ArrayList<>(variables.size());
         for (Variable v : variables) {
            terms.add(v.getTerm());
         }
      }

      @Override
      public boolean equals(Object o) {
         Key k = (Key) o;
         for (int i = 0; i < terms.size(); i++) {
            if (!terms.get(i).strictEquality(k.terms.get(i))) {
               return false;
            }
         }
         return true;
      }

      @Override
      public int hashCode() {
         // TODO is it possible to improve on returning the same hashCode for all instances?
         return 0;
      }
   }
}
