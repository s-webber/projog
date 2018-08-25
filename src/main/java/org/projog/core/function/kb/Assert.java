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

import org.projog.core.KnowledgeBase;
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
   public boolean evaluate(Term consequent) {
      PredicateKey key = PredicateKey.createForTerm(consequent);
      KnowledgeBase kb = getKnowledgeBase();
      UserDefinedPredicateFactory userDefinedPredicate = kb.createOrReturnUserDefinedPredicate(key);
      ClauseModel clauseModel = ClauseModel.createClauseModel(consequent);
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
