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

 %FALSE member(d, [a,b,c])
 %FALSE member(d, [])
 %FALSE member([], [])

 %QUERY member(X, [a,b,c])
 %ANSWER X=a
 %ANSWER X=b
 %ANSWER X=c

 %QUERY member(p(X,b), [p(a,b), p(z,Y), p(x(Y), Y)])
 %ANSWER
 % X=a
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=z
 % Y=b
 %ANSWER
 %ANSWER
 % X=x(b)
 % Y=b
 %ANSWER

 %QUERY member(X, [a,b,c,a|d])
 %ANSWER X=a
 %ANSWER X=b
 %ANSWER X=c
 %ANSWER X=a
 %QUERY member(a, [a,b,c,a|a])
 %ANSWER/
 %ANSWER/
 %QUERY member(a, [a,b,c,a|d])
 %ANSWER/
 %ANSWER/
 %TRUE_NO member(b, [a,b,c,a|d])
 %TRUE_NO member(c, [a,b,c,a|d])
 %FALSE member(d, [a,b,c,a|d])
 %FALSE member(z, [a,b,c,a|d])

 %FALSE member(X, a)
 %FALSE member(X, p(a,b))
 %FALSE member(X, 1)
 %FALSE member(X, 1.5)

 %QUERY member(a, [a,a,a|X])
 %ANSWER X=UNINSTANTIATED VARIABLE
 %ANSWER X=UNINSTANTIATED VARIABLE
 %ANSWER X=UNINSTANTIATED VARIABLE
 %ANSWER X=[a|_]
 %ANSWER X=[_,a|_]
 %ANSWER X=[_,_,a|_]
 %ANSWER X=[_,_,_,a|_]
 %QUIT
 %QUERY member(a, [a,b,c|X])
 %ANSWER X=UNINSTANTIATED VARIABLE
 %ANSWER X=[a|_]
 %ANSWER X=[_,a|_]
 %ANSWER X=[_,_,a|_]
 %ANSWER X=[_,_,_,a|_]
 %QUIT
 %QUERY member(d, [a,b,c|X])
 %ANSWER X=[d|_]
 %ANSWER X=[_,d|_]
 %ANSWER X=[_,_,d|_]
 %ANSWER X=[_,_,_,d|_]
 %QUIT
 %QUERY member(a, X)
 %ANSWER X=[a|_]
 %ANSWER X=[_,a|_]
 %ANSWER X=[_,_,a|_]
 %QUIT
 %QUERY member(X, Y)
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=[X|_]
 %ANSWER
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=[_,X|_]
 %ANSWER
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=[_,_,X|_]
 %ANSWER
 %QUIT
 %QUERY X=[a,b,c|Z], member(a,X)
 %ANSWER
 % X = [a,b,c|Z]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = [a,b,c,a|_]
 % Z = [a|_]
 %ANSWER
 %ANSWER
 % X = [a,b,c,_,a|_]
 % Z = [_,a|_]
 %ANSWER
 %ANSWER
 % X = [a,b,c,_,_,a|_]
 % Z = [_,_,a|_]
 %ANSWER
 %QUIT
 %QUERY member(p(X),[p(a),p(b),p(c)|Z])
 %ANSWER
 % X = a
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = b
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = c
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Z = [p(X)|_]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Z = [_,p(X)|_]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Z = [_,_,p(X)|_]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Z = [_,_,_,p(X)|_]
 %ANSWER
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
            List n = new List(new Variable("_"), currentList.getTerm());
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
               List n = new List(element, new Variable("_"));
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
