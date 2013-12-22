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
 % %TRUE% assertz(p(a,b,c))
 % %TRUE% assertz(p(1,2,3))
 % %TRUE% assertz(p(a,c,e))
 
 % %FALSE% retract(p(x,y,z))
 % %FALSE% retract(p(a,b,e))
 
 % %QUERY% p(X,Y,Z)
 % %ANSWER%
 % X=a
 % Y=b
 % Z=c
 % %ANSWER%
 % %ANSWER%
 % X=1
 % Y=2
 % Z=3
 % %ANSWER%
 % %ANSWER%
 % X=a
 % Y=c
 % Z=e
 % %ANSWER%

 % %QUERY% retract(p(a,Y,Z))
 % %ANSWER%
 % Y=b
 % Z=c
 % %ANSWER%
 % %ANSWER%
 % Y=c
 % Z=e
 % %ANSWER%

 % %QUERY% p(X,Y,Z)
 % %ANSWER%
 % X=1
 % Y=2
 % Z=3
 % %ANSWER%
 
 % %QUERY% retract(p(X,Y,Z))
 % %ANSWER%
 % X=1
 % Y=2
 % Z=3
 % %ANSWER%
 
 % %FALSE% p(X,Y,Z)
 */
/**
 * <code>retract(X)</code> - remove clauses from the knowledge base.
 * <p>
 * The first clause that <code>X</code> matches is removed from the knowledge base. When an attempt is made to
 * re-satisfy the goal, the next clause that <code>X</code> matches is removed. <code>X</code> must be suitably
 * instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class Retract extends AbstractUserDefinedPredicateInspectionFunction {
   public Retract() {
   }

   protected Retract(KnowledgeBase kb) {
      setKnowledgeBase(kb);
   }

   @Override
   public Retract getPredicate(Term... args) {
      return getPredicate(args[0]);
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public Retract getPredicate(Term arg) {
      return new Retract(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg) {
      return internalEvaluate(arg, null);
   }

   @Override
   protected boolean doRemoveMatches() {
      return true;
   }
}