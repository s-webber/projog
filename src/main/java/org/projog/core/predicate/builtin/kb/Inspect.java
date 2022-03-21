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

import java.util.Iterator;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.UnknownPredicate;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.Term;

/* TEST
% Examples of using "clause":
test(a,b) :- true.
test(1,2) :- true.
test(A,B,C) :- true.
test([_|Y],p(1)) :- true.
%?- clause(test(X,Y), Z)
% X=a
% Y=b
% Z=true
% X=1
% Y=2
% Z=true
% X=[_|Y]
% Y=p(1)
% Z=true

%TRUE clause(test(a,b,c), true)
%TRUE clause(test(1,2,3), true)
%FAIL clause(tset(1,2,3), true)

% Examples of using "retract":

%TRUE assertz(p(a,b,c))
%TRUE assertz(p(1,2,3))
%TRUE assertz(p(a,c,e))

%FAIL retract(p(x,y,z))
%FAIL retract(p(a,b,e))

%?- p(X,Y,Z)
% X=a
% Y=b
% Z=c
% X=1
% Y=2
% Z=3
% X=a
% Y=c
% Z=e

%?- retract(p(a,Y,Z))
% Y=b
% Z=c
% Y=c
% Z=e

%?- p(X,Y,Z)
% X=1
% Y=2
% Z=3

%?- retract(p(X,Y,Z))
% X=1
% Y=2
% Z=3

%FAIL p(X,Y,Z)

% retract and clause will fail if predicate does not exist
%FAIL retract(unknown_predicate(1,2,3))
%FAIL clause(unknown_predicate(1,2,3),X)

% Argument must be suitably instantiated that the predicate of the clause can be determined.
%?- retract(X)
%ERROR Expected an atom or a predicate but got a VARIABLE with value: X
%?- clause(X,Y)
%ERROR Expected an atom or a predicate but got a VARIABLE with value: X

%?- retract(true)
%ERROR Cannot inspect clauses of built-in predicate: true/0
%?- clause(true,X)
%ERROR Cannot inspect clauses of built-in predicate: true/0
%?- retract(is(1,2))
%ERROR Cannot inspect clauses of built-in predicate: is/2
%?- clause(is(1,2),X)
%ERROR Cannot inspect clauses of built-in predicate: is/2

non_dynamic_predicate(1,2,3).
%?- retract(non_dynamic_predicate(1,2,3))
%ERROR Cannot retract clause from user defined predicate as it is not dynamic: non_dynamic_predicate/3
%?- retract(non_dynamic_predicate(_,_,_))
%ERROR Cannot retract clause from user defined predicate as it is not dynamic: non_dynamic_predicate/3
%FAIL retract(non_dynamic_predicate(4,5,6))
*/
/**
 * <code>clause(X,Y)</code> / <code>retract(X)</code> - matches terms to existing clauses.
 * <p>
 * <code>clause(X,Y)</code> causes <code>X</code> and <code>Y</code> to be matched to the head and body of an existing
 * clause. If no clauses are found for the predicate represented by <code>X</code> then the goal fails. If there are
 * more than one that matches, the clauses will be matched one at a time as the goal is re-satisfied. <code>X</code>
 * must be suitably instantiated that the predicate of the clause can be determined.
 * </p>
 * <p>
 * <code>retract(X)</code> - remove clauses from the knowledge base. The first clause that <code>X</code> matches is
 * removed from the knowledge base. When an attempt is made to re-satisfy the goal, the next clause that <code>X</code>
 * matches is removed. <code>X</code> must be suitably instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class Inspect extends AbstractPredicateFactory {
   /**
    * {@code true} if matching rules should be removed (retracted) from the knowledge base as part of calls to
    * {@link #evaluate(Term, Term)} or {@code false} if the knowledge base should remain unaltered.
    */
   private final boolean doRemoveMatches;

   public static Inspect inspectClause() {
      return new Inspect(false);
   }

   public static Inspect retract() {
      return new Inspect(true);
   }

   private Inspect(boolean doRemoveMatches) {
      this.doRemoveMatches = doRemoveMatches;
   }

   @Override
   protected Predicate getPredicate(Term clauseHead) {
      return getPredicate(clauseHead, null);
   }

   @Override
   protected Predicate getPredicate(Term clauseHead, Term clauseBody) {
      PredicateFactory predicateFactory = getPredicates().getPredicateFactory(clauseHead);
      if (predicateFactory instanceof UserDefinedPredicateFactory) {
         UserDefinedPredicateFactory userDefinedPredicate = (UserDefinedPredicateFactory) predicateFactory;
         return new InspectPredicate(clauseHead, clauseBody, userDefinedPredicate.getImplications());
      } else if (predicateFactory instanceof UnknownPredicate) {
         return PredicateUtils.FALSE;
      } else {
         PredicateKey key = PredicateKey.createForTerm(clauseHead);
         throw new ProjogException("Cannot inspect clauses of built-in predicate: " + key);
      }
   }

   private final class InspectPredicate implements Predicate {
      private final Term clauseHead;
      private final Term clauseBody;
      private final Iterator<ClauseModel> implications;

      private InspectPredicate(Term clauseHead, Term clauseBody, Iterator<ClauseModel> implications) {
         this.clauseHead = clauseHead;
         this.clauseBody = clauseBody;
         this.implications = implications;
      }

      /**
       * @return {@code true} if there is a rule in the knowledge base whose consequent can be unified with
       * {@code clauseHead} and, if {@code clauseBody} is not {@code null}, whose antecedent can be unified with
       * {@code clauseBody}.
       */
      @Override
      public boolean evaluate() {
         while (implications.hasNext()) {
            backtrack(clauseHead, clauseBody);

            ClauseModel clauseModel = implications.next();
            if (unifiable(clauseHead, clauseBody, clauseModel)) {
               if (doRemoveMatches) {
                  remove();
               }
               return true;
            }
         }
         return false;
      }

      private void remove() {
         try {
            implications.remove();
         } catch (UnsupportedOperationException e) {
            throw new ProjogException("Cannot retract clause from user defined predicate as it is not dynamic: " + PredicateKey.createForTerm(clauseHead));
         }
      }

      private void backtrack(Term clauseHead, Term clauseBody) {
         clauseHead.backtrack();
         if (clauseBody != null) {
            clauseBody.backtrack();
         }
      }

      private boolean unifiable(Term clauseHead, Term clauseBody, ClauseModel clauseModel) {
         Term consequent = clauseModel.getConsequent();
         Term antecedent = clauseModel.getAntecedent();
         return clauseHead.unify(consequent) && (clauseBody == null || clauseBody.unify(antecedent));
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return implications == null || implications.hasNext();
      }
   }
}
