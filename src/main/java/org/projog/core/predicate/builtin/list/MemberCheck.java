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

import static org.projog.core.term.ListUtils.isMember;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.List;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
%TRUE memberchk(a, [a,b,c])
%TRUE memberchk(b, [a,b,c])
%TRUE memberchk(c, [a,b,c])

%FAIL memberchk(d, [a,b,c])
%FAIL memberchk(d, [])
%FAIL memberchk(X, [])
%FAIL memberchk([], [])

%?- memberchk(X, [a,b,c|d])
% X=a
%TRUE memberchk(a, [a,b,c|d])
%TRUE memberchk(b, [a,b,c|d])
%TRUE memberchk(c, [a,b,c|d])
%?- memberchk(d, [a,b,c|d])
%ERROR Expected empty list or variable but got: ATOM with value: d
%?- memberchk(z, [a,b,c|d])
%ERROR Expected empty list or variable but got: ATOM with value: d

%?- memberchk(X, [a,b,c])
% X=a

%?- memberchk(p(X,b), [p(a,b), p(z,Y), p(x(Y), Y)])
% X=a
% Y=UNINSTANTIATED VARIABLE

%?- memberchk(p(a,X),[p(x,y),b,p(Y,Y)])
% X=a
% Y=a

%?- memberchk(a, X)
% X=[a|_]

%?- memberchk(p(a,X),a)
%ERROR Expected list or empty list but got: ATOM with value: a

%TRUE memberchk(something, [something|_])
%TRUE memberchk(anything, [something|_])
%?- memberchk(anything, [something|X])
% X=[anything|_]
*/
/**
 * <code>memberchk(E, L)</code> - checks is a term is a member of a list.
 * <p>
 * <code>memberchk(E, L)</code> succeeds if <code>E</code> is a member of the list <code>L</code>. No attempt is made to
 * retry the goal during backtracking - so if <code>E</code> appears multiple times in <code>L</code> only the first
 * occurrence will be matched.
 * </p>
 */
public final class MemberCheck extends AbstractSingleResultPredicate implements PreprocessablePredicateFactory {
   @Override
   protected boolean evaluate(Term element, Term list) {
      if (list.getType().isVariable()) {
         return list.unify(new List(element, new Variable()));
      } else {
         return isMember(element, list);
      }
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      // TODO if EMPTY_LIST then return a PredicateFactory that always uses PredicateUtils.FALSE.
      Term prologList = term.getArgument(1);
      if (prologList.getType() == TermType.LIST && prologList.isImmutable()) {
         java.util.List<Term> javaList = ListUtils.toJavaUtilList(prologList);
         if (javaList != null) { // i.e. if not a partial list
            // TODO if no duplicates then could replace List with LinkedHashSet
            return new PreprocessedMemberCheck(javaList);
         }
      }

      return this;
   }

   private static class PreprocessedMemberCheck extends AbstractSingleResultPredicate {
      private final java.util.List<Term> javaList;

      PreprocessedMemberCheck(java.util.List<Term> javaList) {
         this.javaList = javaList;
      }

      @Override
      protected boolean evaluate(Term element, Term notUsed) {
         if (element.isImmutable()) {
            return javaList.contains(element);
         } else {
            for (Term next : javaList) {
               if (element.unify(next)) {
                  return true;
               }
               element.backtrack();
            }
            return false;
         }
      }
   }
}
