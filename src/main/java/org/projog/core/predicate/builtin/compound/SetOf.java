/*
 * Copyright 2013 S. Webber
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

import java.util.List;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;
import org.projog.core.term.TermUtils;

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

p(a,1).
p(b,2).
p(c,3).
p(d,2).
p(d,2).

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
*/
/**
 * <code>setof(X,P,L)</code> - find all solutions that satisfy the goal.
 * <p>
 * <code>setof(X,P,L)</code> produces a list (<code>L</code>) of <code>X</code> for each possible solution of the goal
 * <code>P</code>. If <code>P</code> contains uninstantiated variables, other than <code>X</code>, it is possible that
 * <code>setof</code> can be successfully evaluated multiple times - for each possible values of the uninstantiated
 * variables. The elements in <code>L</code> will appear in sorted order and will not include duplicates. Fails if
 * <code>P</code> has no solutions.
 */
public final class SetOf extends AbstractPredicateFactory implements PreprocessablePredicateFactory {
   @Override
   protected Predicate getPredicate(Term template, Term goal, Term bag) {
      return new SetOfPredicate(getPredicates().getPredicateFactory(goal), template, goal, bag);
   }

   private static final class SetOfPredicate extends AbstractCollectionOf {
      private SetOfPredicate(PredicateFactory pf, Term template, Term goal, Term bag) {
         super(pf, template, goal, bag);
      }

      /** "setof" excludes duplicates and orders elements using {@link TermComparator}. */
      @Override
      protected void add(List<Term> list, Term newTerm) {
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
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term goal = term.getArgument(1);
      if (PartialApplicationUtils.isAtomOrStructure(goal)) {
         return new PreprocessedSetOf(getPredicates().getPreprocessedPredicateFactory(goal));
      } else {
         return this;
      }
   }

   private static class PreprocessedSetOf implements PredicateFactory {
      private final PredicateFactory pf;

      PreprocessedSetOf(PredicateFactory pf) {
         this.pf = pf;
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return new SetOfPredicate(pf, args[0], args[1], args[2]);
      }

      @Override
      public boolean isRetryable() {
         return true; // TODO
      }
   }
}
