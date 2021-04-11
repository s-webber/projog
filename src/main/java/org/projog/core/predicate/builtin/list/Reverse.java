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
import org.projog.core.term.EmptyList;
import org.projog.core.term.List;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
 %TRUE reverse([a],[a])
 %TRUE reverse([a,b,c],[c,b,a])
 %QUERY reverse([a,b,c],X)
 %ANSWER X=[c,b,a]
 %QUERY reverse(X,[a,b,c])
 %ANSWER X=[c,b,a]

 %TRUE reverse([],[])
 %QUERY reverse([],X)
 %ANSWER X=[]
 %QUERY reverse(X,[])
 %ANSWER X=[]

 %FALSE reverse([a,b,c],[])
 %FALSE reverse([a,b,c],[a,b,c])
 %FALSE reverse([a,b,c],[d,c,b,a])
 %FALSE reverse([a,b,c,d],[c,b,a])
 %FALSE reverse([a,b,c],'abc')
 %FALSE reverse('abc',X)

 %FALSE reverse([a,b|c],X)

 %QUERY reverse([a|X], Y)
 %ANSWER
 % X = []
 % Y = [a]
 %ANSWER
 %ANSWER
 % X = [_]
 % Y = [_,a]
 %ANSWER
 %ANSWER
 % X = [_,_]
 % Y = [_,_,a]
 %ANSWER
 %ANSWER
 % X = [_,_,_]
 % Y = [_,_,_,a]
 %ANSWER
 %QUIT

 %QUERY reverse([z,x,x,c,b|X],[a,b,c|Y])
 %ANSWER
 % X = [a]
 % Y = [x,x,z]
 %ANSWER
 %ANSWER
 % X = [c,b,a]
 % Y = [b,c,x,x,z]
 %ANSWER
 %ANSWER
 % X = [_,c,b,a]
 % Y = [_,b,c,x,x,z]
 %ANSWER
 %ANSWER
 % X = [_,_,c,b,a]
 % Y = [_,_,b,c,x,x,z]
 %ANSWER
 %ANSWER
 % X = [_,_,_,c,b,a]
 % Y = [_,_,_,b,c,x,x,z]
 %ANSWER
 %QUIT

 %QUERY reverse([a,b,c|Y],[z,x,x,c,b|X])
 %ANSWER
 % X = [a]
 % Y = [x,x,z]
 %ANSWER
 %ANSWER
 % X = [c,b,a]
 % Y = [b,c,x,x,z]
 %ANSWER
 %ANSWER
 % X = [_,c,b,a]
 % Y = [_,b,c,x,x,z]
 %ANSWER
 %ANSWER
 % X = [_,_,c,b,a]
 % Y = [_,_,b,c,x,x,z]
 %ANSWER
 %ANSWER
 % X = [_,_,_,c,b,a]
 % Y = [_,_,_,b,c,x,x,z]
 %ANSWER
 %QUIT

 %QUERY reverse([c,b,a|X],[c,b,a|Y])
 %ANSWER
 % X = [b,c]
 % Y = [b,c]
 %ANSWER
 %ANSWER
 % X = [a,b,c]
 % Y = [a,b,c]
 %ANSWER
 %ANSWER
 % X = [_,a,b,c]
 % Y = [_,a,b,c]
 %ANSWER
 %ANSWER
 % X = [_,_,a,b,c]
 % Y = [_,_,a,b,c]
 %ANSWER
 %ANSWER
 % X = [_,_,_,a,b,c]
 % Y = [_,_,_,a,b,c]
 %ANSWER
 %QUIT

 %QUERY reverse([b,c|X],[a,b,c|Y])
 %ANSWER
 % X = [b,a]
 % Y = [b]
 %ANSWER
 %ANSWER
 % X = [c,b,a]
 % Y = [c,b]
 %ANSWER
 %ANSWER
 % X = [_,c,b,a]
 % Y = [_,c,b]
 %ANSWER
 %ANSWER
 % X = [_,_,c,b,a]
 % Y = [_,_,c,b]
 %ANSWER
 %ANSWER
 % X = [_,_,_,c,b,a]
 % Y = [_,_,_,c,b]
 %ANSWER
 %QUIT

 %QUERY reverse([a,b,c|Y],[b,c|X])
 %ANSWER
 % X = [b,a]
 % Y = [b]
 %ANSWER
 %ANSWER
 % X = [c,b,a]
 % Y = [c,b]
 %ANSWER
 %ANSWER
 % X = [_,c,b,a]
 % Y = [_,c,b]
 %ANSWER
 %ANSWER
 % X = [_,_,c,b,a]
 % Y = [_,_,c,b]
 %ANSWER
 %ANSWER
 % X = [_,_,_,c,b,a]
 % Y = [_,_,_,c,b]
 %ANSWER
 %QUIT

 %QUERY X=[d,c|Q],Y=[a,b,c|R],reverse(X,Y)
 %ANSWER
 % Q = [b,a]
 % R = [d]
 % X = [d,c,b,a]
 % Y = [a,b,c,d]
 %ANSWER
 %ANSWER
 % Q = [c,b,a]
 % R = [c,d]
 % X = [d,c,c,b,a]
 % Y = [a,b,c,c,d]
 %ANSWER
 %ANSWER
 % Q = [_,c,b,a]
 % R = [_,c,d]
 % X = [d,c,_,c,b,a]
 % Y = [a,b,c,_,c,d]
 %ANSWER
 %ANSWER
 % Q = [_,_,c,b,a]
 % R = [_,_,c,d]
 % X = [d,c,_,_,c,b,a]
 % Y = [a,b,c,_,_,c,d]
 %ANSWER
 %ANSWER
 % Q = [_,_,_,c,b,a]
 % R = [_,_,_,c,d]
 % X = [d,c,_,_,_,c,b,a]
 % Y = [a,b,c,_,_,_,c,d]
 %ANSWER
 %QUIT

 %QUERY X=[d,c|Q],Y=[a,b,c|R],reverse(Y,X)
 %ANSWER
 % Q = [b,a]
 % R = [d]
 % X = [d,c,b,a]
 % Y = [a,b,c,d]
 %ANSWER
 %ANSWER
 % Q = [c,b,a]
 % R = [c,d]
 % X = [d,c,c,b,a]
 % Y = [a,b,c,c,d]
 %ANSWER
 %ANSWER
 % Q = [_,c,b,a]
 % R = [_,c,d]
 % X = [d,c,_,c,b,a]
 % Y = [a,b,c,_,c,d]
 %ANSWER
 %ANSWER
 % Q = [_,_,c,b,a]
 % R = [_,_,c,d]
 % X = [d,c,_,_,c,b,a]
 % Y = [a,b,c,_,_,c,d]
 %ANSWER
 %ANSWER
 % Q = [_,_,_,c,b,a]
 % R = [_,_,_,c,d]
 % X = [d,c,_,_,_,c,b,a]
 % Y = [a,b,c,_,_,_,c,d]
 %ANSWER
 %QUIT

 %QUERY X=[e,d|Q],Y=[a,b,c|R],reverse(X,Y)
 %ANSWER
 % Q = [c,b,a]
 % R = [d,e]
 % X = [e,d,c,b,a]
 % Y = [a,b,c,d,e]
 %ANSWER
 %ANSWER
 % Q = [_,c,b,a]
 % R = [_,d,e]
 % X = [e,d,_,c,b,a]
 % Y = [a,b,c,_,d,e]
 %ANSWER
 %ANSWER
 % Q = [_,_,c,b,a]
 % R = [_,_,d,e]
 % X = [e,d,_,_,c,b,a]
 % Y = [a,b,c,_,_,d,e]
 %ANSWER
 %ANSWER
 % Q = [_,_,_,c,b,a]
 % R = [_,_,_,d,e]
 % X = [e,d,_,_,_,c,b,a]
 % Y = [a,b,c,_,_,_,d,e]
 %ANSWER
 %QUIT

 %QUERY X=[e,d|Q],Y=[a,b,c|R],reverse(Y,X)
 %ANSWER
 % Q = [c,b,a]
 % R = [d,e]
 % X = [e,d,c,b,a]
 % Y = [a,b,c,d,e]
 %ANSWER
 %ANSWER
 % Q = [_,c,b,a]
 % R = [_,d,e]
 % X = [e,d,_,c,b,a]
 % Y = [a,b,c,_,d,e]
 %ANSWER
 %ANSWER
 % Q = [_,_,c,b,a]
 % R = [_,_,d,e]
 % X = [e,d,_,_,c,b,a]
 % Y = [a,b,c,_,_,d,e]
 %ANSWER
 %ANSWER
 % Q = [_,_,_,c,b,a]
 % R = [_,_,_,d,e]
 % X = [e,d,_,_,_,c,b,a]
 % Y = [a,b,c,_,_,_,d,e]
 %ANSWER
 %QUIT

 %QUERY reverse([a,b,c|Y],[X|Z])
 %ANSWER
 % X = c
 % Y = []
 % Z = [b,a]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [X]
 % Z = [c,b,a]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [_,X]
 % Z = [_,c,b,a]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [_,_,X]
 % Z = [_,_,c,b,a]
 %ANSWER
 %QUIT

 %QUERY reverse([a,b|X],[a,b|X])
 %ANSWER X = [a]
 %ANSWER X = [b,a]
 %ANSWER X = [X,b,a]
 %ANSWER X = [X,X,b,a]
 %ANSWER X = [X,X,X,b,a]
 %QUIT

 %QUERY reverse(X,X), numbervars(X)
 %ANSWER X=[]
 %ANSWER X=[$VAR(0)]
 %ANSWER X=[$VAR(0),$VAR(0)]
 %ANSWER X=[$VAR(0),$VAR(1),$VAR(0)]
 %ANSWER X=[$VAR(0),$VAR(1),$VAR(1),$VAR(0)]
 %ANSWER X=[$VAR(0),$VAR(1),$VAR(2),$VAR(1),$VAR(0)]
 %ANSWER X=[$VAR(0),$VAR(1),$VAR(2),$VAR(2),$VAR(1),$VAR(0)]
 %QUIT
 */
/**
 * <code>reverse(X,Y)</code> - reverses the order of elements in a list.
 * <p>
 * The <code>reverse(X,Y)</code> goal succeeds if the elements of list <code>X</code> are in reverse order of the
 * elements in list <code>Y</code>.
 * </p>
 */
public final class Reverse extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(final Term list1, final Term list2) {
      return new ReversePredicate(list1, list2);
   }

   private final static class ReversePredicate implements Predicate {
      Term arg1;
      Term arg2;
      Term arg3;
      Term arg4;
      boolean retrying;

      ReversePredicate(Term list1, Term list2) {
         // reverse(Xs, [], Ys, Ys).
         this.arg1 = list1;
         this.arg2 = EmptyList.EMPTY_LIST;
         this.arg3 = list2;
         this.arg4 = list2;
      }

      @Override
      public boolean evaluate() {
         while (true) {
            // reverse([], Ys, Ys, []).
            if (!retrying && arg1.unify(EmptyList.EMPTY_LIST) && arg4.unify(EmptyList.EMPTY_LIST) && arg2.unify(arg3)) {
               retrying = true;
               return true;
            }
            retrying = false;

            arg1.backtrack();
            arg2.backtrack();
            arg3.backtrack();
            arg4.backtrack();

            // reverse([X|Xs], Rs, Ys, [_|Bound]) :-
            //   reverse(Xs, [X|Rs], Ys, Bound).
            Term x;
            Term xs;
            if (arg1.getType() == TermType.LIST) {
               x = arg1.getArgument(0);
               xs = arg1.getArgument(1);
            } else if (arg1.getType().isVariable()) {
               x = new Variable("X");
               xs = new Variable("Xs");
               arg1.unify(new List(x, xs));
            } else {
               return false;
            }

            if (arg4.getType() == TermType.LIST) {
               arg4 = arg4.getArgument(1);
            } else if (arg4.getType().isVariable()) {
               Variable v = new Variable("Bound");
               arg4.unify(new List(new Variable("_"), v));
               arg4 = v;
            } else {
               return false;
            }

            arg1 = xs.getTerm();
            arg2 = new List(x.getTerm(), arg2.getTerm());
            arg3 = arg3.getTerm();
            arg4 = arg4.getTerm();
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return !retrying || (arg1 != EmptyList.EMPTY_LIST && arg4 != EmptyList.EMPTY_LIST);
      }
   }
}
