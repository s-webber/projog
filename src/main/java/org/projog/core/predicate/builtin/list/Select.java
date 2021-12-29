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

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.EmptyList;
import org.projog.core.term.List;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
%?- select(X,[h,e,l,l,o],Z)
% X=h
% Z=[e,l,l,o]
% X=e
% Z=[h,l,l,o]
% X=l
% Z=[h,e,l,o]
% X=l
% Z=[h,e,l,o]
% X=o
% Z=[h,e,l,l]

%?- select(l,[h,e,l,l,o],Z)
% Z=[h,e,l,o]
% Z=[h,e,l,o]
%NO

%?- select(l,[h,e,l,l,o],[h,e,l,o])
%YES
%YES
%NO

%?- select(p(a,B),[p(X,q), p(a,X)],Z)
% B=q
% X=a
% Z=[p(a, a)]
% B=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% Z=[p(B, q)]

%?- select(a, Result, [x,y,z])
% Result=[a,x,y,z]
% Result=[x,a,y,z]
% Result=[x,y,a,z]
% Result=[x,y,z,a]

%?- select(a, [x|X], [x,y,z])
% X=[a,y,z]
% X=[y,a,z]
% X=[y,z,a]
*/
/**
 * <code>select(X,Y,Z)</code> - removes an element from a list.
 * <p>
 * Attempts to unify <code>Z</code> with the result of removing an occurrence of <code>X</code> from the list
 * represented by <code>Y</code>. An attempt is made to retry the goal during backtracking.
 * </p>
 */
public final class Select extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term element, Term inputList, Term outputList) {
      // select(X, [Head|Tail], Rest) implemented as: select(Tail, Head, X, Rest)
      if (inputList.getType() == TermType.LIST) {
         return new SelectPredicate(inputList.getArgument(1), inputList.getArgument(0), element, outputList);
      } else if (inputList.getType().isVariable()) {
         Term head = new Variable("Head");
         Term tail = new Variable("Tail");
         Term newList = new List(head, tail);
         inputList.unify(newList);
         return new SelectPredicate(tail, head, element, outputList);
      } else {
         return PredicateUtils.FALSE;
      }
   }

   private static final class SelectPredicate implements Predicate {
      Term firstArg;
      Term secondArg;
      final Term thirdArg;
      Term fourthArg;
      boolean retrying;

      SelectPredicate(Term firstArg, Term secondArg, Term thirdArg, Term fourthArg) {
         this.firstArg = firstArg;
         this.secondArg = secondArg;
         this.thirdArg = thirdArg;
         this.fourthArg = fourthArg;
      }

      @Override
      public boolean evaluate() {
         while (true) {
            //select3_(Tail, Head, Head, Tail).
            if (!retrying && firstArg.unify(fourthArg) && secondArg.unify(thirdArg)) {
               retrying = true;
               return true;
            }
            retrying = false;

            firstArg.backtrack();
            secondArg.backtrack();
            thirdArg.backtrack();
            fourthArg.backtrack();

            // select3_([Head2|Tail], Head, X, [Head|Rest]) :-
            //   select3_(Tail, Head2, X, Rest).
            Term tail;
            Term head2;
            if (firstArg.getType() == TermType.LIST) {
               head2 = firstArg.getArgument(0);
               tail = firstArg.getArgument(1);
            } else if (firstArg.getType().isVariable()) {
               head2 = new Variable("Head2");
               tail = new Variable("Tail");
               firstArg.unify(new List(head2, tail));
            } else {
               return false;
            }

            Term rest;
            if (fourthArg.getType() == TermType.LIST) {
               if (!secondArg.unify(fourthArg.getArgument(0))) {
                  return false;
               }
               rest = fourthArg.getArgument(1);
            } else if (fourthArg.getType().isVariable()) {
               rest = new Variable("Rest");
               fourthArg.unify(new List(secondArg, rest));
            } else {
               return false;
            }

            firstArg = tail.getTerm();
            secondArg = head2.getTerm();
            fourthArg = rest.getTerm();
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return !retrying || (firstArg != EmptyList.EMPTY_LIST && fourthArg != EmptyList.EMPTY_LIST);
      }
   }
}
