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
import org.projog.core.term.List;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
%TRUE_NO member(a, [a,b,c])
%TRUE_NO member(b, [a,b,c])
%TRUE member(c, [a,b,c])

%FAIL member(d, [a,b,c])
%FAIL member(d, [])
%FAIL member([], [])

%?- member(X, [a,b,c])
% X=a
% X=b
% X=c

%?- member(p(X,b), [p(a,b), p(z,Y), p(x(Y), Y)])
% X=a
% Y=UNINSTANTIATED VARIABLE
% X=z
% Y=b
% X=x(b)
% Y=b

%?- member(X, [a,b,c,a|d])
% X=a
% X=b
% X=c
% X=a
%?- member(a, [a,b,c,a|a])
%YES
%YES
%?- member(a, [a,b,c,a|d])
%YES
%YES
%TRUE_NO member(b, [a,b,c,a|d])
%TRUE_NO member(c, [a,b,c,a|d])
%FAIL member(d, [a,b,c,a|d])
%FAIL member(z, [a,b,c,a|d])

%FAIL member(X, a)
%FAIL member(X, p(a,b))
%FAIL member(X, 1)
%FAIL member(X, 1.5)

%?- member(a, [a,a,a|X])
% X=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% X=[a|_]
% X=[_,a|_]
% X=[_,_,a|_]
% X=[_,_,_,a|_]
%QUIT
%?- member(a, [a,b,c|X])
% X=UNINSTANTIATED VARIABLE
% X=[a|_]
% X=[_,a|_]
% X=[_,_,a|_]
% X=[_,_,_,a|_]
%QUIT
%?- member(d, [a,b,c|X])
% X=[d|_]
% X=[_,d|_]
% X=[_,_,d|_]
% X=[_,_,_,d|_]
%QUIT
%?- member(a, X)
% X=[a|_]
% X=[_,a|_]
% X=[_,_,a|_]
%QUIT
%?- member(X, Y)
% X=UNINSTANTIATED VARIABLE
% Y=[X|_]
% X=UNINSTANTIATED VARIABLE
% Y=[_,X|_]
% X=UNINSTANTIATED VARIABLE
% Y=[_,_,X|_]
%QUIT
%?- X=[a,b,c|Z], member(a,X)
% X=[a,b,c|Z]
% Z=UNINSTANTIATED VARIABLE
% X=[a,b,c,a|_]
% Z=[a|_]
% X=[a,b,c,_,a|_]
% Z=[_,a|_]
% X=[a,b,c,_,_,a|_]
% Z=[_,_,a|_]
%QUIT
%?- member(p(X),[p(a),p(b),p(c)|Z])
% X=a
% Z=UNINSTANTIATED VARIABLE
% X=b
% Z=UNINSTANTIATED VARIABLE
% X=c
% Z=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% Z=[p(X)|_]
% X=UNINSTANTIATED VARIABLE
% Z=[_,p(X)|_]
% X=UNINSTANTIATED VARIABLE
% Z=[_,_,p(X)|_]
% X=UNINSTANTIATED VARIABLE
% Z=[_,_,_,p(X)|_]
%QUIT
*/
/**
 * <code>member(E, L)</code> - enumerates members of a list.
 * <p>
 * <code>member(E, L)</code> succeeds if <code>E</code> is a member of the list <code>L</code>. An attempt is made to
 * retry the goal during backtracking - so it can be used to enumerate the members of a list.
 * </p>
 */
public final class Member extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term element, Term list) {
      return new MemberPredicate(element, list);
   }

   private final class MemberPredicate implements Predicate {
      private final Term element;
      private final Term originalList;
      private Term currentList;
      private boolean isTailVariable;

      private MemberPredicate(Term element, Term originalList) {
         this.element = element;
         this.originalList = originalList;
         this.currentList = originalList;
      }

      @Override
      public boolean evaluate() {
         if (isTailVariable) {
            List n = new List(new Variable(), currentList.getTerm());
            currentList.backtrack();
            currentList.unify(n);
            return true;
         }

         while (true) {
            if (currentList.getType() == TermType.LIST) {
               element.backtrack();
               originalList.backtrack();
               Term head = currentList.getArgument(0);
               currentList = currentList.getArgument(1);
               if (element.unify(head)) {
                  return true;
               }
            } else if (currentList.getType().isVariable()) {
               isTailVariable = true;
               element.backtrack();
               List n = new List(element, new Variable());
               currentList.unify(n);
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return currentList.getType() == TermType.LIST || currentList.getType().isVariable();
      }
   }
}
