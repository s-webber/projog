/*
 * Copyright 2018 S. Webber
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
package org.projog.core.predicate.builtin.compare;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%FAIL unify_with_occurs_check(A, f(A))

%FAIL unify_with_occurs_check(f(A), A)

%FAIL unify_with_occurs_check(A, [1, A, 3])

%FAIL unify_with_occurs_check([1, A, 3], A)

%FAIL unify_with_occurs_check(A, f([1, A, 3]))

%FAIL unify_with_occurs_check(f([1, A, 3]), A)

%FAIL unify_with_occurs_check(A, [1, f(A), 3])

%FAIL unify_with_occurs_check([1, f(A), 3], A)

%FAIL unify_with_occurs_check(f(A), f([1, A, 3]))

%FAIL unify_with_occurs_check(f([1, A, 3]), f(A))

%?- unify_with_occurs_check(X, X)
% X=UNINSTANTIATED VARIABLE

%?- unify_with_occurs_check(X, Y)
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE

%?- X=1, unify_with_occurs_check(X, Y)
% X=1
% Y=1

%?- unify_with_occurs_check(X, Y), Y=2
% X=2
% Y=2

%?- unify_with_occurs_check(X, f(Y))
% X=f(Y)
% Y=UNINSTANTIATED VARIABLE

%?- unify_with_occurs_check(f(X), Y)
% X=UNINSTANTIATED VARIABLE
% Y=f(X)

%?- unify_with_occurs_check(X, [1, Y, 3])
% X=[1,Y,3]
% Y=UNINSTANTIATED VARIABLE

%?- unify_with_occurs_check([1, X, 3], Y)
% X=UNINSTANTIATED VARIABLE
% Y=[1,X,3]

%TRUE unify_with_occurs_check(a, a)
%TRUE unify_with_occurs_check(1, 1)
%TRUE unify_with_occurs_check(1.0, 1.0)
%TRUE unify_with_occurs_check(1.5, 1.5)
%TRUE unify_with_occurs_check([], [])
%TRUE unify_with_occurs_check([a], [a])
%TRUE unify_with_occurs_check([a, b, c], [a, b, c])
%TRUE unify_with_occurs_check(p(a), p(a))
%TRUE unify_with_occurs_check(p(a, b, c), p(a, b, c))
%FAIL unify_with_occurs_check(a, b)
%FAIL unify_with_occurs_check(1, 2)
%FAIL unify_with_occurs_check(1, -1)
%FAIL unify_with_occurs_check(1, '1')
%FAIL unify_with_occurs_check(1, 1.0)
%FAIL unify_with_occurs_check(1.0, -1.0)
%FAIL unify_with_occurs_check(1.0, 1.5)
%FAIL unify_with_occurs_check(2, 1+1)
%FAIL unify_with_occurs_check([a], [x])
%FAIL unify_with_occurs_check([a, b, c], [a, b, x])
%FAIL unify_with_occurs_check([a, b, c], [a, x, c])
%FAIL unify_with_occurs_check([a, b, c], [x, b, c])
%FAIL unify_with_occurs_check([a, b, c], [a, c, b])
%FAIL unify_with_occurs_check([a, b, c], [b, a, c])
%FAIL unify_with_occurs_check([a, b, c], [b, c, a])
%FAIL unify_with_occurs_check([a, b, c], [c, a, b])
%FAIL unify_with_occurs_check([a, b, c], [c, b, a])
%FAIL unify_with_occurs_check(p(a), p(x))
%FAIL unify_with_occurs_check(p(a, b), p(a))
%FAIL unify_with_occurs_check(p(a), p(a, b))
%FAIL unify_with_occurs_check(p(a, b, c), p(a, b, x))
%FAIL unify_with_occurs_check(p(a, b, c), p(a, x, c))
%FAIL unify_with_occurs_check(p(a, b, c), p(x, b, c))
%FAIL unify_with_occurs_check(p(a, b, c), p(a, c, b))
%FAIL unify_with_occurs_check(p(a, b, c), p(b, a, c))
%FAIL unify_with_occurs_check(p(a, b, c), p(b, c, a))
%FAIL unify_with_occurs_check(p(a, b, c), p(c, a, b))
%FAIL unify_with_occurs_check(p(a, b, c), p(c, b, a))
*/
/**
 * <code>unify_with_occurs_check(X, Y)</code> - an equality test using sound unification.
 * <p>
 * Works like <code>X = Y</code> but with an additional check to avoid cyclic terms. When using
 * <code>unify_with_occurs_check</code> a variable can only be unified with a term if that term does not contain the
 * variable.
 * </p>
 */
public final class UnifyWithOccursCheck extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      return arg1.unify(arg2) && !isCyclic(arg1) && !isCyclic(arg2);
   }

   private boolean isCyclic(Term t) {
      return isCyclic(t, Collections.<Term> emptySet());
   }

   private boolean isCyclic(Term t, Set<Term> parents) {
      Set<Term> s = new HashSet<>(parents);
      if (!s.add(t)) {
         return true;
      }

      if (t.getType().isStructure()) {
         for (int i = 0; i < t.getNumberOfArguments(); i++) {
            if (isCyclic(t.getArgument(i), s)) {
               return true;
            }
         }
      }

      return false;
   }
}
