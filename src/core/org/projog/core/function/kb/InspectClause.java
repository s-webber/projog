/*
 * Copyright 2013 S Webber
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
import org.projog.core.term.Term;

/* SYSTEM TEST
 :- test(a,b).
 :- test(1,2).
 :- test(A,B,C).
 :- test(p(),p(1)).
 % %QUERY% clause(test(X,Y), Z)
 % %ANSWER%
 % X=a
 % Y=b
 % Z=true
 % %ANSWER%
 % %ANSWER%
 % X=1
 % Y=2
 % Z=true
 % %ANSWER%
 % %ANSWER%
 % X=p()
 % Y=p(1)
 % Z=true
 % %ANSWER%
 
 % %TRUE% clause(test(a,b,c), true)
 % %TRUE% clause(test(1,2,3), true)
 % %FALSE% clause(tset(1,2,3), true)
 */
/**
 * <code>clause(X,Y)</code> - matches terms to existing clauses.
 * <p>
 * <code>clause(X,Y)</code> causes <code>X</code> and <code>Y</code> to be matched to the head and body of an existing
 * clause. If no clauses are found for the predicate represented by <code>X</code> then the goal fails. If there are
 * more than one that matches, the clauses will be matched one at a time as the goal is re-satisfied. <code>X</code>
 * must be suitably instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class InspectClause extends AbstractUserDefinedPredicateInspectionFunction {
   public InspectClause() {
   }

   protected InspectClause(KnowledgeBase kb) {
      setKnowledgeBase(kb);
   }

   @Override
   public InspectClause getPredicate(Term... args) {
      return getPredicate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public InspectClause getPredicate(Term arg1, Term arg2) {
      return new InspectClause(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      return internalEvaluate(arg1, arg2);
   }

   @Override
   protected boolean doRemoveMatches() {
      return false;
   }
}