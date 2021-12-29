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
package org.projog.core.predicate.builtin.construct;

import java.util.HashMap;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/* TEST
%?- copy_term(X, Y), X \== Y
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE

%?- copy_term(X, Y), X=a, Y=b
% X=a
% Y=b

%?- X=a, copy_term(X, Y)
% X=a
% Y=a

%?- X=p(A,B,p(C)), copy_term(X, Y), A=1, B=2, C=3
% A=1
% B=2
% C=3
% X=p(1, 2, p(3))
% Y=p(A, B, p(C))

%?- X=p(A,B,p(3)), copy_term(X, Y), Y=p(1,2,p(C))
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=3
% X=p(A, B, p(3))
% Y=p(1, 2, p(3))

%?- X=[A,B,C], copy_term(X, Y), A=1, B=2, C=3
% A=1
% B=2
% C=3
% X=[1,2,3]
% Y=[A,B,C]

%TRUE copy_term(a, a)
%FAIL copy_term(a, b)
%TRUE copy_term(p(1,2,p(3)), p(1,2,p(3)))
%FAIL copy_term(p(1,2,p(3)), p(1,2,p(4)))

%?- X=p(A,B,3), copy_term(X, p(1,E,F)), B=b, E=e
% A=UNINSTANTIATED VARIABLE
% B=b
% E=e
% F=3
% X=p(A, b, 3)
*/
/**
 * <code>copy_term(X,Y)</code> - makes a copy of a term.
 * <p>
 * <code>copy_term(X,Y)</code> makes a copy of <code>X</code> and attempts to unify it with <code>Y</code>. Any
 * variables in term <code>X</code> will be replaced with new variables in the copied version of the term.
 * </p>
 */
public final class CopyTerm extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      Term copy = arg1.copy(new HashMap<Variable, Variable>());
      return arg2.unify(copy);
   }
}
