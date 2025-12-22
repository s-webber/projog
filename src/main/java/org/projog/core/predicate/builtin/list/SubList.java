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
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.getPreprocessedPartiallyAppliedPredicateFactory;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.isAtomOrStructure;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.isList;
import static org.projog.core.term.ListFactory.createList;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
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

% First argument must be an atom or structure, unless second arg is an empty list.. Second argument must be a list.
%?- include(X, [a], Z)
%ERROR Expected an atom or a predicate but got a VARIABLE with value: X
%?- include(1, [a], Z)
%ERROR Expected an atom or a predicate but got a INTEGER with value: 1
%FAIL include(atom, X, Z)
%?- maplist(X, [], Z)
% X=UNINSTANTIATED VARIABLE
% Z=[]
%?- maplist(1, [], Z)
% Z=[]

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

%?- (X = >(7) ; X = <(7) ; X = =(7)), include(X, [5,6,1,8,7,4,2,9,3], Y)
% X=>(7)
% Y=[5,6,1,4,2,3]
% X=<(7)
% Y=[8,9]
% X==(7)
% Y=[7]
*/
/**
 * <code>include(X,Y,Z)</code> - filters a list by a goal.
 * <p>
 * <code>include(X,Y,Z)</code> succeeds if the list <code>Z</code> consists of the elements of the list <code>Y</code>
 * for which the goal <code>X</code> can be successfully applied.
 * </p>
 */
public final class SubList implements PredicateFactory {
   private final KnowledgeBase kb;
   private final PredicateFactory pf;

   public SubList(KnowledgeBase kb) {
      this(kb, kb.getPredicates().placeholder());
   }

   private SubList(KnowledgeBase kb, PredicateFactory pf) {
      this.kb = kb;
      this.pf = pf;
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term goal = term.firstArgument();
      if (isAtomOrStructure(goal)) {
         return new SubList(kb, getPreprocessedPartiallyAppliedPredicateFactory(kb.getPredicates(), goal, 1));
      } else {
         return this;
      }
   }

   @Override
   public Predicate getPredicate(Term term) {
      Term list = term.secondArgument();
      if (isList(list)) {
         return PredicateUtils.toPredicate(evaluateSubList(pf, term.firstArgument(), list, term.thirdArgument()));
      } else {
         return PredicateUtils.FALSE;
      }
   }

   private static boolean evaluateSubList(PredicateFactory pf, Term partiallyAppliedFunction, Term term, Term filteredOutput) {
      final List<Term> matches = new ArrayList<>();
      Term next = term;
      while (next.getType() == TermType.LIST) {
         Term arg = next.firstArgument();
         if (apply(pf, createArguments(partiallyAppliedFunction, arg))) {
            matches.add(arg);
         }
         next = next.secondArgument();
      }
      return next.getType() == TermType.EMPTY_LIST && filteredOutput.unify(createList(matches));
   }

   @Override
   public boolean isRetryable() {
      return false;
   }
}
