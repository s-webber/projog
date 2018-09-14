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
package org.projog.core.function.kb;

import org.projog.core.Predicate;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE assertz(x(1,a))
 %TRUE assertz(x(2,b))
 %TRUE assertz(x(3,a))

 %TRUE assertz(y(1,a))

 %QUERY x(X,Y)
 %ANSWER
 % X=1
 % Y=a
 %ANSWER
 %ANSWER
 % X=2
 % Y=b
 %ANSWER
 %ANSWER
 % X=3
 % Y=a
 %ANSWER

 %QUERY y(X,Y)
 %ANSWER
 % X=1
 % Y=a
 %ANSWER

 %TRUE retractall(x(_,a))

 %QUERY x(X,Y)
 %ANSWER
 % X=2
 % Y=b
 %ANSWER

 %QUERY y(X,Y)
 %ANSWER
 % X=1
 % Y=a
 %ANSWER

 %TRUE retractall(x(_,_))

 %FALSE x(X,Y)

 % Succeeds even if there are no facts to remove
 %TRUE retractall(x(_,_))
 %TRUE retractall(xyz(_))

 % Argument must be suitably instantiated that the predicate of the clause can be determined.
 %QUERY retractall(X)
 %ERROR Expected an atom or a predicate but got a NAMED_VARIABLE with value: X
 */
/**
 * <code>retractall(X)</code> - remove clauses from the knowledge base.
 * <p>
 * <i>All</i> clauses that <code>X</code> matches are removed from the knowledge base. <code>X</code> must be suitably
 * instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class RetractAll extends AbstractSingletonPredicate {
   private Inspect retractPredicateFactory;

   @Override
   protected void init() {
      retractPredicateFactory = Inspect.retract();
      retractPredicateFactory.setKnowledgeBase(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term t) {
      Predicate p = retractPredicateFactory.getPredicate(t);
      while (p.evaluate()) {
         t.backtrack();
      }
      return true;
   }
}
