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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.apply;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.createArguments;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.getCurriedPredicateFactory;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.getPreprocessedCurriedPredicateFactory;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.isAtomOrStructure;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.isList;
import static org.projog.core.term.ListFactory.createList;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%TRUE include(atom, [], [])
%TRUE include(atom, [a], [a])
%?- include(atom, [X], [])
% X=UNINSTANTIATED VARIABLE
%TRUE include(atom, [1], [])
%TRUE include(integer, [1], [1])

%?- include(atom, [a,a,a], X)
% X=[a,a,a]

%?- include(atom, [a,b,c],X)
% X=[a,b,c]


%?- include(atom, [1,b,c], X)
% X=[b,c]

%?- include(atom, [a,2,c], X)
% X=[a,c]

%?- include(atom, [a,b,3], X)
% X=[a,b]

%?- include(atom, [a,2,3], X)
% X=[a]

%?- include(atom, [1,b,3], X)
% X=[b]

%?- include(atom, [1,2,c], X)
% X=[c]

%?- include(atom, [1,2,3], X)
% X=[]

%TRUE include(<(0), [5,6,1,8,7,4,2,9,3], [5,6,1,8,7,4,2,9,3])
%?- include(<(0), [5,6,1,8,7,4,2,9,3], X)
% X=[5,6,1,8,7,4,2,9,3]
%?- include(>(5), [5,6,1,8,7,4,2,9,3], X)
% X=[1,4,2,3]
%?- include(>(7), [5,6,1,8,7,4,2,9,3], X)
% X=[5,6,1,4,2,3]
%TRUE include(>(7), [5,6,1,8,7,4,2,9,3], [5,6,1,4,2,3])
%?- include(=(7), [5,6,1,8,7,4,2,9,3], X)
% X=[7]
%?- include(=(0), [5,6,1,8,7,4,2,9,3], X)
% X=[]

%?- include(=(p(W)), [p(1),p(2),p(3)], Z)
% W=1
% Z=[p(1)]
%?- include(=(p(1,A,3)), [p(W,a,4), p(X,b,3), p(X,Y,3), p(Z,c,3)], B)
% A=b
% B=[p(1, b, 3),p(1, b, 3)]
% W=UNINSTANTIATED VARIABLE
% X=1
% Y=b
% Z=UNINSTANTIATED VARIABLE

% First argument must be an atom or structure. Second argument must be a list.
%FAIL include(X, [], Z)
%FAIL include(1, [], Z)
%FAIL include(atom, X, Z)

% Note: "sublist" is a synonym for "include".
%?- sublist(atom, [a,b,c], X)
% X=[a,b,c]
%?- sublist(atom, [a,2,c], X)
% X=[a,c]

p(a) :- writeln('rule 1').
p(b) :- writeln('rule 2').
p(X) :- atom(X), writeln('rule 3').
%?- include(p,[1,a,2,b,3,c,c,4,c,b,5,a,z,b,b,6], Z)
%OUTPUT
%rule 1
%rule 2
%rule 3
%rule 3
%rule 3
%rule 2
%rule 1
%rule 3
%rule 2
%rule 2
%
%OUTPUT
% Z=[a,b,c,c,c,b,a,z,b,b]
*/
/**
 * <code>include(X,Y,Z)</code> - filters a list by a goal.
 * <p>
 * <code>include(X,Y,Z)</code> succeeds if the list <code>Z</code> consists of the elements of the list <code>Y</code>
 * for which the goal <code>X</code> can be successfully applied.
 * </p>
 */
public final class SubList extends AbstractSingleResultPredicate implements PreprocessablePredicateFactory {
   @Override
   protected boolean evaluate(Term partiallyAppliedFunction, Term args, Term filteredOutput) {
      if (isValidArguments(partiallyAppliedFunction, args)) {
         final PredicateFactory pf = getCurriedPredicateFactory(getPredicates(), partiallyAppliedFunction);
         return evaluateSubList(pf, partiallyAppliedFunction, args, filteredOutput);
      } else {
         return false;
      }
   }

   private static boolean evaluateSubList(PredicateFactory pf, Term partiallyAppliedFunction, Term args, Term filteredOutput) {
      final List<Term> matches = new ArrayList<>();
      Term next = args;
      while (next.getType() == TermType.LIST) {
         Term arg = next.getArgument(0);
         if (apply(pf, createArguments(partiallyAppliedFunction, arg))) {
            matches.add(arg);
         }
         next = next.getArgument(1);
      }
      return next.getType() == TermType.EMPTY_LIST && filteredOutput.unify(createList(matches));
   }

   private boolean isValidArguments(Term partiallyAppliedFunction, Term arg) {
      return isAtomOrStructure(partiallyAppliedFunction) && isList(arg);
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term goal = term.getArgument(0);
      if (isAtomOrStructure(goal)) {
         return new PreprocessedSubList(getPreprocessedCurriedPredicateFactory(getPredicates(), goal));
      } else {
         return this;
      }
   }

   private static final class PreprocessedSubList implements PredicateFactory {
      private final PredicateFactory predicateFactory;

      PreprocessedSubList(PredicateFactory predicateFactory) {
         this.predicateFactory = predicateFactory;
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         Term list = args[1];
         if (isList(list)) {
            return PredicateUtils.toPredicate(evaluateSubList(predicateFactory, args[0], list, args[2]));
         } else {
            return PredicateUtils.FALSE;
         }
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }
}
