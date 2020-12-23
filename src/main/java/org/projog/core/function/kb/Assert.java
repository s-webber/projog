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

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/* TEST
 %QUERY X=p(1), asserta(X), asserta(p(2)), asserta(p(3))
 %ANSWER X=p(1)
 %QUERY p(X)
 %ANSWER X=3
 %ANSWER X=2
 %ANSWER X=1

 %QUERY retract(p(X))
 %ANSWER X=3
 %ANSWER X=2
 %ANSWER X=1
 %FALSE p(X)

 %QUERY X=p(1), assertz(X), assertz(p(2)), assertz(p(3))
 %ANSWER X=p(1)
 %QUERY p(X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3

 %QUERY retract(p(X))
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 %FALSE p(X)

 % Note: "assert" is a synonym for "assertz".
 %FALSE z(X)
 %TRUE assert(z(a)), assert(z(b)), assert(z(c))
 %QUERY z(X)
 %ANSWER X=a
 %ANSWER X=b
 %ANSWER X=c

 % rules can be asserted, but have to be surrounded by brackets
 %FALSE q(X,Y,Z)
 %QUERY assert((q(X,Y,Z) :- Z is X+Y))
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY assert((q(X,Y,Z) :- Z is X-Y))
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY assert((q(X,Y,Z) :- Z is X*Y, repeat(3)))
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY assert((q(X,Y,Z) :- Z is -X))
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %TRUE_NO q(6,3,9)
 %QUERY q(6,3,18)
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %NO
 %FALSE q(6,3,17)
 %QUERY q(5,2,Q)
 %ANSWER Q=7
 %ANSWER Q=3
 %ANSWER Q=10
 %ANSWER Q=10
 %ANSWER Q=10
 %ANSWER Q=-5
 %QUERY q(6,7,Q)
 %ANSWER Q=13
 %ANSWER Q=-1
 %ANSWER Q=42
 %ANSWER Q=42
 %ANSWER Q=42
 %ANSWER Q=-6
 %QUERY q(8,4,32)
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %NO
 %QUERY asserta((q(X,Y,Z) :- Z is Y-X))
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY q(5,2,Q)
 %ANSWER Q=-3
 %ANSWER Q=7
 %ANSWER Q=3
 %ANSWER Q=10
 %ANSWER Q=10
 %ANSWER Q=10
 %ANSWER Q=-5
 */
/**
 * <code>asserta(X)</code> / <code>assertz(X)</code> - adds a clause to the knowledge base.
 * <p>
 * <code>asserta(X)</code> adds the clause <code>X</code> to the front of the knowledge base. <code>assertz(X)</code>
 * adds the clause <code>X</code> to the end of the knowledge base. <code>X</code> must be suitably instantiated that
 * the predicate of the clause can be determined.
 * </p>
 * <p>
 * This is <i>not</i> undone as part of backtracking.
 * </p>
 */
public final class Assert extends AbstractSingletonPredicate {
   public static Assert assertA() {
      return new Assert(false);
   }

   public static Assert assertZ() {
      return new Assert(true);
   }

   private final boolean addLast;

   private Assert(boolean addLast) {
      this.addLast = addLast;
   }

   @Override
   public boolean evaluate(Term clause) {
      ClauseModel clauseModel = ClauseModel.createClauseModel(clause);
      PredicateKey key = PredicateKey.createForTerm(clauseModel.getConsequent());
      UserDefinedPredicateFactory userDefinedPredicate = getPredicates().createOrReturnUserDefinedPredicate(key);
      add(userDefinedPredicate, clauseModel);
      return true;
   }

   private void add(UserDefinedPredicateFactory userDefinedPredicate, ClauseModel clauseModel) {
      if (addLast) {
         userDefinedPredicate.addLast(clauseModel);
      } else {
         userDefinedPredicate.addFirst(clauseModel);
      }
   }
}
