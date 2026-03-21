/*
 * Copyright 2026 S. Webber
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
package org.projog.core.predicate.builtin.compound;

import static org.projog.core.term.TermComparator.TERM_COMPARATOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/* TEST
z(r).
z(t).
z(y).

x(a,b,c).
x(q,X,e) :- z(X).
x(1,2,3).
x(w,b,c).
x(d,b,c).
x(a,b,c).

p(a,1).
p(b,2).
p(c,3).
p(d,2).
p(d,2).

%?- bagof(X,x(X,Y,Z),L)
% L=[a,w,d,a]
% X=UNINSTANTIATED VARIABLE
% Y=b
% Z=c
% L=[q]
% X=UNINSTANTIATED VARIABLE
% Y=r
% Z=e
% L=[q]
% X=UNINSTANTIATED VARIABLE
% Y=t
% Z=e
% L=[q]
% X=UNINSTANTIATED VARIABLE
% Y=y
% Z=e
% L=[1]
% X=UNINSTANTIATED VARIABLE
% Y=2
% Z=3

%FAIL bagof(X,x(X,y,z),L)

%?- bagof(Y, (member(X,[6,3,7,2,5,4,3]), X<4, Y is X*X), L)
% L=[9,9]
% X=3
% Y=UNINSTANTIATED VARIABLE
% L=[4]
% X=2
% Y=UNINSTANTIATED VARIABLE

%?- bagof(X, p(X,Y), List)
% List=[a]
% X=UNINSTANTIATED VARIABLE
% Y=1
% List=[b,d,d]
% X=UNINSTANTIATED VARIABLE
% Y=2
% List=[c]
% X=UNINSTANTIATED VARIABLE
% Y=3

%?- setof(X,x(X,Y,Z),L)
% L=[a,d,w]
% X=UNINSTANTIATED VARIABLE
% Y=b
% Z=c
% L=[q]
% X=UNINSTANTIATED VARIABLE
% Y=r
% Z=e
% L=[q]
% X=UNINSTANTIATED VARIABLE
% Y=t
% Z=e
% L=[q]
% X=UNINSTANTIATED VARIABLE
% Y=y
% Z=e
% L=[1]
% X=UNINSTANTIATED VARIABLE
% Y=2
% Z=3

%FAIL setof(X,x(X,y,z),L)

%?- setof(Y, (member(X,[6,3,7,2,5,4,3]), X<4, Y is X*X), L)
% L=[9]
% X=3
% Y=UNINSTANTIATED VARIABLE
% L=[4]
% X=2
% Y=UNINSTANTIATED VARIABLE

%?- setof(X, p(X,Y), List)
% List=[a]
% X=UNINSTANTIATED VARIABLE
% Y=1
% List=[b,d]
% X=UNINSTANTIATED VARIABLE
% Y=2
% List=[c]
% X=UNINSTANTIATED VARIABLE
% Y=3

% TODO setof(X, Y ^ p(X,Y), List)
% TODO bagof(X, Y ^ p(X,Y), List)
*/
/**
 * <code>bagof(X,P,L)</code> / <code>setof(X,P,L)</code> - find all solutions that satisfy the goal.
 * <p>
 * <code>bagof(X,P,L)</code> produces a list (<code>L</code>) of <code>X</code> for each possible solution of the goal
 * <code>P</code>. If <code>P</code> contains uninstantiated variables, other than <code>X</code>, it is possible that
 * <code>bagof</code> can be successfully evaluated multiple times - for each possible values of the uninstantiated
 * variables. The elements in <code>L</code> will appear in the order they were found and may include duplicates. Fails
 * if <code>P</code> has no solutions.
 * </p>
 * <p>
 * <code>setof(X,P,L)</code> produces a list (<code>L</code>) of <code>X</code> for each possible solution of the goal
 * <code>P</code>. If <code>P</code> contains uninstantiated variables, other than <code>X</code>, it is possible that
 * <code>setof</code> can be successfully evaluated multiple times - for each possible values of the uninstantiated
 * variables. The elements in <code>L</code> will appear in sorted order and will not include duplicates. Fails if
 * <code>P</code> has no solutions.
 * </p>
 */
public final class CollectionOf implements PredicateFactory {
   public static CollectionOf bagOf(KnowledgeBase kb) {
      return new CollectionOf(kb, true);
   }

   public static CollectionOf setOf(KnowledgeBase kb) {
      return new CollectionOf(kb, false);
   }

   private final KnowledgeBase kb;
   private final PredicateFactory pf;
   private final boolean includeDuplicates;

   private CollectionOf(KnowledgeBase kb, boolean includeDuplicates) {
      this(kb, kb.getPredicates().placeholder(), includeDuplicates);
   }

   private CollectionOf(KnowledgeBase kb, PredicateFactory pf, boolean includeDuplicates) {
      this.kb = kb;
      this.pf = pf;
      this.includeDuplicates = includeDuplicates;
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term goal = term.secondArgument();
      if (PartialApplicationUtils.isAtomOrStructure(goal)) {
         return new CollectionOf(kb, kb.getPredicates().getPreprocessedPredicateFactory(goal), includeDuplicates);
      } else {
         return this;
      }
   }

   @Override
   public Predicate getPredicate(Term term) {
      return new CollectionOfPredicate(pf, term.firstArgument(), term.secondArgument(), term.thirdArgument(), includeDuplicates);
   }

   @Override
   public boolean isRetryable() {
      return pf.isRetryable();
   }

   private static final class CollectionOfPredicate implements Predicate {
      private final PredicateFactory pf;
      private final Term template;
      private final Term goal;
      private final Term bag;
      private final boolean includeDuplicates;
      private List<Variable> variablesNotInTemplate;
      private Iterator<Entry<Key, List<Term>>> itr;

      protected CollectionOfPredicate(PredicateFactory pf, Term template, Term goal, Term bag, boolean includeDuplicates) {
         this.pf = pf;
         this.template = template;
         this.goal = goal;
         this.bag = bag;
         this.includeDuplicates = includeDuplicates;
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

         Predicate predicate = pf.getPredicate(goal);

         Map<Key, List<Term>> m = new LinkedHashMap<>();
         if (predicate.evaluate()) {
            do {
               Key key = new Key(variablesNotInTemplate);
               List<Term> l = m.get(key);
               if (l == null) {
                  l = new ArrayList<>();
                  m.put(key, l);
               }

               if (includeDuplicates) {
                  addToBag(l, template.getTerm());
               } else {
                  addToSet(l, template.getTerm());
               }
            } while (hasFoundAnotherSolution(predicate));
         }

         goal.backtrack();

         itr = m.entrySet().iterator();
      }

      /** "bagof" returns all elements (including duplicates) in the order they were found. */
      private void addToBag(List<Term> l, Term t) {
         l.add(t);
      }

      /** "setof" excludes duplicates and orders elements using {@link TermComparator}. */
      private void addToSet(List<Term> list, Term newTerm) {
         final int numberOfElements = list.size();
         for (int i = 0; i < numberOfElements; i++) {
            final Term next = list.get(i);
            final int comparison = TERM_COMPARATOR.compare(newTerm, next);
            if (comparison < 0) {
               // found correct position - so add
               list.add(i, newTerm);
               return;
            } else if (comparison == 0 && TermUtils.termsEqual(newTerm, next)) {
               // duplicate - so ignore
               return;
            }
         }
         list.add(newTerm);
      }

      private List<Variable> getVariablesNotInTemplate(Term template, Term goal) {
         Set<Variable> variablesInGoal = getAllVariablesInTerm(goal);
         Set<Variable> variablesInTemplate = getAllVariablesInTerm(template);
         variablesInGoal.removeAll(variablesInTemplate);
         return new ArrayList<>(variablesInGoal);
      }

      /**
       * Returns all {@link Variable}s contained in the specified term.
       *
       * @param argument the term to find variables for
       * @return all {@link Variable}s contained in the specified term.
       */
      private static Set<Variable> getAllVariablesInTerm(final Term argument) {
         HashMap<Variable, Term> variables = new HashMap<>();
         argument.copy(variables);
         return new HashSet<>(variables.keySet());
      }

      private boolean hasFoundAnotherSolution(final Predicate predicate) {
         return predicate.couldReevaluationSucceed() && predicate.evaluate();
      }

      @Override
      public final boolean couldReevaluationSucceed() {
         return itr == null || itr.hasNext();
      }
   }

   /** Represents a combination of possible values for the variables contained in the goal. */
   private static final class Key {
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
            if (!TermUtils.termsEqual(terms.get(i), k.terms.get(i))) {
               return false;
            }
         }
         return true;
      }

      @Override
      public int hashCode() {
         // NOTE is it possible to improve on returning the same hashCode for all instances?
         return 0;
      }
   }
}
