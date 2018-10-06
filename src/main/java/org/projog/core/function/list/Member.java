/*
 * Copyright 2013-2014 S. Webber
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
package org.projog.core.function.list;

import org.projog.core.Predicate;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

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
   public Predicate getPredicate(Term element, Term list) {
      // TODO what if partial list? e.g. member(a,[a,b|X])
      if (list.getType() != TermType.LIST && list.getType() != TermType.EMPTY_LIST) {
         throw new ProjogException("Expected list but got: " + list);
      }
      return new MemberPredicate(element, list);
   }

   private final class MemberPredicate implements Predicate {
      private final Term element;
      private final Term originalList;
      private Term currentList;

      private MemberPredicate(Term element, Term originalList) {
         this.element = element;
         this.originalList = originalList;
         this.currentList = originalList;
      }

      @Override
      public boolean evaluate() {
         while (true) {
            if (couldReevaluationSucceed()) {
               element.backtrack();
               originalList.backtrack();
               Term head = currentList.getArgument(0);
               currentList = currentList.getArgument(1);
               if (element.unify(head)) {
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return currentList.getType() == TermType.LIST;
      }
   }
}
