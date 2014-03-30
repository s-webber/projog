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
package org.projog.core.function.compound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/* SYSTEM TEST
 z(r).
 z(t).
 z(y).

 x(a,b,c).
 x(q,X,e) :- z(X).
 x(1,2,3).
 x(w,b,c).
 x(d,b,c).
 x(a,b,c).

 % %QUERY% bagof(X,x(X,Y,Z),L)
 % %ANSWER%
 % L=[a,w,d,a]
 % X=UNINSTANTIATED VARIABLE
 % Y=b
 % Z=c
 % %ANSWER%
 % %ANSWER%
 % L=[q]
 % X=UNINSTANTIATED VARIABLE
 % Y=r
 % Z=e
 % %ANSWER%
 % %ANSWER%
 % L=[q]
 % X=UNINSTANTIATED VARIABLE
 % Y=t
 % Z=e
 % %ANSWER%
 % %ANSWER%
 % L=[q]
 % X=UNINSTANTIATED VARIABLE
 % Y=y
 % Z=e
 % %ANSWER%
 % %ANSWER%
 % L=[1]
 % X=UNINSTANTIATED VARIABLE
 % Y=2
 % Z=3
 % %ANSWER%
 
 % %FALSE% bagof(X,x(X,y,z),L)
 */
/**
 * <code>bagof(X,P,L)</code> - find all solutions that satisfy the goal.
 * <p>
 * <code>bagof(X,P,L)</code> produces a list (<code>L</code>) of <code>X</code> for each possible solution of the goal
 * <code>P</code>.
 */
public class BagOf extends AbstractRetryablePredicate {
   private List<Variable> variablesNotInTemplate;
   private Iterator<Entry<Key, List<Term>>> itr;

   /** needed to create prototype actual instances can be created from */
   public BagOf() {
   }

   private BagOf(KnowledgeBase kb) {
      setKnowledgeBase(kb);
   }

   @Override
   public BagOf getPredicate(Term... args) {
      return getPredicate(args[0], args[1], args[2]);
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public BagOf getPredicate(Term template, Term goal, Term bag) {
      return new BagOf(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1], args[2]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term template, Term goal, Term bag) {
      if (itr == null) {
         init(template, goal);
      }

      if (itr.hasNext()) {
         template.backtrack();
         Entry<Key, List<Term>> e = itr.next();
         bag.backtrack();
         bag.unify(toListTerm(e.getValue()));
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

      Predicate predicate = KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), goal);
      Term[] goalArguments = goal.getArgs();

      Map<Key, List<Term>> m = new LinkedHashMap<>();
      if (predicate.evaluate(goalArguments)) {
         do {
            Key key = new Key(variablesNotInTemplate);
            List<Term> l = m.get(key);
            if (l == null) {
               l = new ArrayList<Term>();
               m.put(key, l);
            }
            l.add(template.getTerm());
         } while (hasFoundAnotherSolution(predicate, goalArguments));
      }

      goal.backtrack();

      itr = m.entrySet().iterator();
   }

   private List<Variable> getVariablesNotInTemplate(Term template, Term goal) {
      Set<Variable> variablesInGoal = TermUtils.getAllVariablesInTerm(goal);
      Set<Variable> variablesInTemplate = TermUtils.getAllVariablesInTerm(template);
      variablesInGoal.removeAll(variablesInTemplate);
      return new ArrayList<Variable>(variablesInGoal);
   }

   private boolean hasFoundAnotherSolution(final Predicate predicate, final Term[] goalArguments) {
      return predicate.isRetryable() && predicate.couldReEvaluationSucceed() && predicate.evaluate(goalArguments);
   }

   private Term toListTerm(final Collection<Term> solutions) {
      return ListFactory.create(solutions.toArray(new Term[solutions.size()]));
   }

   @Override
   public boolean couldReEvaluationSucceed() {
      return itr == null || itr.hasNext();
   }

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
         return 0; // TODO
      }
   }
}