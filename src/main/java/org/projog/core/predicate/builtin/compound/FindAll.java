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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.EmptyList;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
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

%?- findall(X,x(X,Y,Z),L)
% L=[a,q,q,q,1,w,d,a]
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE

%?- findall(X,x(X,y,z),L)
% L=[]
% X=UNINSTANTIATED VARIABLE

q(a(W)).
q(C).
q(1).
y(X) :- X = o(T,R), q(T), q(R).

%?- findall(X,y(X),L)
% L=[o(a(W), a(W)),o(a(W), R),o(a(W), 1),o(T, a(W)),o(T, R),o(T, 1),o(1, a(W)),o(1, R),o(1, 1)]
% X=UNINSTANTIATED VARIABLE

%?- findall(X,y(X),L), L=[H|_], H=o(a(q),a(z))
% L=[o(a(q), a(z)),o(a(W), R),o(a(W), 1),o(T, a(W)),o(T, R),o(T, 1),o(1, a(W)),o(1, R),o(1, 1)]
% H=o(a(q), a(z))
% X=UNINSTANTIATED VARIABLE

%?- findall(Y, (member(X,[6,3,7,2,5,4,3]), X<4, Y is X*X), L)
% L=[9,4,9]
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE

%?- findall(X,P,L)
%ERROR Expected an atom or a predicate but got a VARIABLE with value: P
*/
/**
 * <code>findall(X,P,L)</code> - find all solutions that satisfy the goal.
 * <p>
 * <code>findall(X,P,L)</code> produces a list (<code>L</code>) of <code>X</code> for each possible solution of the goal
 * <code>P</code>. Succeeds with <code>L</code> unified to an empty list if <code>P</code> has no solutions.
 */
public final class FindAll extends AbstractSingleResultPredicate implements PreprocessablePredicateFactory {
   @Override
   protected boolean evaluate(Term template, Term goal, Term output) {
      return evaluateFindAll(getPredicates().getPredicateFactory(goal), template, goal, output);
   }

   private static boolean evaluateFindAll(PredicateFactory pf, Term template, Term goal, Term output) {
      final Predicate predicate = pf.getPredicate(goal.getArgs());
      final Term solutions;
      if (predicate.evaluate()) {
         solutions = createListOfAllSolutions(template, predicate);
      } else {
         solutions = EmptyList.EMPTY_LIST;
      }
      template.backtrack();
      goal.backtrack();
      return output.unify(solutions);
   }

   private static Term createListOfAllSolutions(Term template, final Predicate predicate) {
      final List<Term> solutions = new ArrayList<>();
      do {
         solutions.add(template.copy(new HashMap<Variable, Variable>()));
      } while (hasFoundAnotherSolution(predicate));
      final Term output = ListFactory.createList(solutions);
      output.backtrack();
      return output;
   }

   private static boolean hasFoundAnotherSolution(final Predicate predicate) {
      return predicate.couldReevaluationSucceed() && predicate.evaluate();
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term goal = term.getArgument(1);
      if (PartialApplicationUtils.isAtomOrStructure(goal)) {
         return new PreprocessedFindAll(getPredicates().getPreprocessedPredicateFactory(goal));
      } else {
         return this;
      }
   }

   private static class PreprocessedFindAll implements PredicateFactory {
      private final PredicateFactory pf;

      public PreprocessedFindAll(PredicateFactory pf) {
         this.pf = pf;
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return PredicateUtils.toPredicate(evaluateFindAll(pf, args[0], args[1], args[2]));
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }
}
