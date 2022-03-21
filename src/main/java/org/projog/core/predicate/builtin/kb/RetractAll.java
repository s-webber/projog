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
package org.projog.core.predicate.builtin.kb;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.term.Term;

/* TEST
%TRUE assertz(x(1,a))
%TRUE assertz(x(2,b))
%TRUE assertz(x(3,a))

%TRUE assertz(y(1,a))

%?- x(X,Y)
% X=1
% Y=a
% X=2
% Y=b
% X=3
% Y=a

%?- y(X,Y)
% X=1
% Y=a

%TRUE retractall(x(_,a))

%?- x(X,Y)
% X=2
% Y=b

%?- y(X,Y)
% X=1
% Y=a

%TRUE retractall(x(_,_))

%FAIL x(X,Y)

% Succeeds even if there are no facts to remove
%TRUE retractall(x(_,_))
%TRUE retractall(xyz(_))

% Argument must be suitably instantiated that the predicate of the clause can be determined.
%?- retractall(X)
%ERROR Expected an atom or a predicate but got a VARIABLE with value: X

%?- retractall(true)
%ERROR Cannot inspect clauses of built-in predicate: true/0
%?- retractall(is(1,2))
%ERROR Cannot inspect clauses of built-in predicate: is/2

non_dynamic_predicate(1,2,3).
%?- retractall(non_dynamic_predicate(1,2,3))
%ERROR Cannot retract clause from user defined predicate as it is not dynamic: non_dynamic_predicate/3
%?- retractall(non_dynamic_predicate(_,_,_))
%ERROR Cannot retract clause from user defined predicate as it is not dynamic: non_dynamic_predicate/3
%TRUE retractall(non_dynamic_predicate(4,5,6))
*/
/**
 * <code>retractall(X)</code> - remove clauses from the knowledge base.
 * <p>
 * <i>All</i> clauses that <code>X</code> matches are removed from the knowledge base. <code>X</code> must be suitably
 * instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class RetractAll extends AbstractSingleResultPredicate {
   private Inspect retractPredicateFactory;

   @Override
   protected void init() {
      retractPredicateFactory = Inspect.retract();
      retractPredicateFactory.setKnowledgeBase(getKnowledgeBase());
   }

   @Override
   protected boolean evaluate(Term t) {
      Predicate p = retractPredicateFactory.getPredicate(t);
      while (p.evaluate()) {
         t.backtrack();
      }
      return true;
   }
}
