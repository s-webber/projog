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
package org.projog.core.function.construct;

import java.util.HashMap;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/* TEST
%QUERY copy_term(X, Y), X \== Y
%ANSWER
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
%ANSWER

%QUERY copy_term(X, Y), X=a, Y=b
%ANSWER
% X=a
% Y=b
%ANSWER

%QUERY X=a, copy_term(X, Y)
%ANSWER
% X=a
% Y=a
%ANSWER

%QUERY X=p(A,B,p(C)), copy_term(X, Y), A=1, B=2, C=3
%ANSWER
% A=1
% B=2
% C=3
% X=p(1, 2, p(3))
% Y=p(A, B, p(C))
%ANSWER

%QUERY X=p(A,B,p(3)), copy_term(X, Y), Y=p(1,2,p(C))
%ANSWER
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=3
% X=p(A, B, p(3))
% Y=p(1, 2, p(3))
%ANSWER

%QUERY X=[A,B,C], copy_term(X, Y), A=1, B=2, C=3
%ANSWER
% A=1
% B=2
% C=3
% X=[1,2,3]
% Y=[A,B,C]
%ANSWER

%TRUE copy_term(a, a)
%FALSE copy_term(a, b)
%TRUE copy_term(p(1,2,p(3)), p(1,2,p(3)))
%FALSE copy_term(p(1,2,p(3)), p(1,2,p(4)))

%QUERY X=p(A,B,3), copy_term(X, p(1,E,F)), B=b, E=e
%ANSWER
% A=UNINSTANTIATED VARIABLE
% B=b
% E=e
% F=3
% X=p(A, b, 3)
%ANSWER
 */
/**
 * <code>copy_term(X,Y)</code> - makes a copy of a term.
 * <p>
 * <code>copy_term(X,Y)</code> makes a copy of <code>X</code> and attempts to unify it with <code>Y</code>. Any
 * variables in term <code>X</code> will be replaced with new variables in the copied version of the term.
 * </p>
 */
public final class CopyTerm extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      Term copy = arg1.copy(new HashMap<Variable, Variable>());
      return arg2.unify(copy);
   }
}
