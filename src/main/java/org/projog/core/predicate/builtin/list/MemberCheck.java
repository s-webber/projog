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

import static org.projog.core.predicate.udp.PredicateUtils.toPredicate;
import static org.projog.core.term.ListUtils.isMember;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
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
public final class MemberCheck implements PredicateFactory {
   @Override
   public PredicateFactory preprocess(Term term) {
      Term prologList = term.secondArgument();
      if (prologList.getType() == TermType.LIST && prologList.isImmutable()) {
         java.util.List<Term> javaList = ListUtils.toJavaUtilList(prologList);
         if (javaList != null) {
            return new ImmutableListMemberCheck(javaList.toArray(new Term[0]));
         }
      }

      return this;
   }

   @Override
   public Predicate getPredicate(Term term) {
      Term element = term.firstArgument();
      Term list = term.secondArgument();

      boolean result;
      if (list.getType().isVariable()) {
         result = list.unify(new List(element, new Variable()));
      } else {
         result = isMember(element, list);
      }
      return toPredicate(result);
   }

   @Override
   public boolean isRetryable() {
      return false;
   }

   private static class ImmutableListMemberCheck extends AbstractSingleResultPredicate {
      private final Term[] immutableTerms;

      ImmutableListMemberCheck(Term[] immutableTerms) {
         this.immutableTerms = immutableTerms;
      }

      @Override
      protected boolean evaluate(Term element, Term notUsed) {
         for (Term immutableTerm : immutableTerms) {
            if (element.unify(immutableTerm)) {
               return true;
            }
            element.backtrack();
         }
         return false;
      }
   }
}
