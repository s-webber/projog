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
package org.projog.core.function.compound;

import java.util.HashMap;

import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Unifier;
import org.projog.core.term.Variable;

/* TEST
 %TRUE true, true
 %FALSE true, fail
 %FALSE fail, true
 %FALSE fail, fail

 %TRUE true, true, true
 %FALSE true, fail, fail
 %FALSE fail, true, fail
 %FALSE fail, fail, true
 %FALSE true, true, fail
 %FALSE true, fail, true
 %FALSE fail, true, true
 %FALSE fail, fail, fail

 b :- true.
 c :- true.
 d :- true.
 y :- true.
 a :- b,c,d.
 x :- y,z.
 %TRUE a
 %FALSE x

 p2(1) :- true.
 p2(2) :- true.
 p2(3) :- true.

 p3(a) :- true.
 p3(b) :- true.
 p3(c) :- true.

 p4(1, b, [a,b,c]) :- true.
 p4(3, c, [1,2,3]) :- true.
 p4(X, Y, [q,w,e,r,t,y]) :- true.

 p1(X, Y, Z) :- p2(X), p3(Y), p4(X,Y,Z).

 %QUERY p1(X, Y, Z)
 %ANSWER
 % X=1
 % Y=a
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=1
 % Y=b
 % Z=[a,b,c]
 %ANSWER
 %ANSWER
 % X=1
 % Y=b
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=1
 % Y=c
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=2
 % Y=a
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=2
 % Y=b
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=2
 % Y=c
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=3
 % Y=a
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=3
 % Y=b
 % Z=[q,w,e,r,t,y]
 %ANSWER
 %ANSWER
 % X=3
 % Y=c
 % Z=[1,2,3]
 %ANSWER
 %ANSWER
 % X=3
 % Y=c
 % Z=[q,w,e,r,t,y]
 %ANSWER

 %QUERY p2(X), p2(X), p2(X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3

 %FALSE p2(X), p3(X), p2(X)
 */
/**
 * <code>X,Y</code> - conjunction.
 * <p>
 * <code>X,Y</code> specifies a conjunction of goals. <code>X,Y</code> succeeds if <code>X</code> succeeds <i>and</i>
 * <code>Y</code> succeeds. If <code>X</code> succeeds and <code>Y</code> fails then an attempt is made to re-satisfy
 * <code>X</code>. If <code>X</code> fails the entire conjunction fails.
 * </p>
 */
public final class Conjunction extends AbstractPredicateFactory {
   // TODO test using a junit test rather than just a Prolog script
   // as over complexity in internal workings (e.g. when and what it backtracks)
   // may not be detectable via a system test.

   @Override
   public Predicate getPredicate(Term arg1, Term arg2) {
      return new ConjunctionPredicate(arg1, arg2);
   }

   private final class ConjunctionPredicate implements Predicate {
      private final Term inputArg1;
      private final Term inputArg2;
      private PredicateFactory secondPredicateFactory;
      private Predicate firstPredicate;
      private Predicate secondPredicate;
      private boolean firstGo = true;
      private Term secondArg;
      private Term tmpInputArg2;

      public ConjunctionPredicate(Term inputArg1, Term inputArg2) {
         this.inputArg1 = inputArg1;
         this.inputArg2 = inputArg2;
      }

      @Override
      public boolean evaluate() {
         if (firstGo) {
            firstPredicate = getKnowledgeBase().getPredicateFactory(inputArg1).getPredicate(inputArg1.getArgs());

            while ((firstGo || firstPredicate.couldReevaluationSucceed()) && firstPredicate.evaluate()) {
               firstGo = false;
               if (preMatch(inputArg2) && secondPredicate.evaluate()) {
                  return true;
               }
               TermUtils.backtrack(tmpInputArg2.getArgs());
            }

            return false;
         }

         do {
            final boolean evaluateSecondPredicate;
            if (secondArg == null) {
               evaluateSecondPredicate = preMatch(inputArg2);
            } else {
               evaluateSecondPredicate = secondPredicate.couldReevaluationSucceed();
            }

            if (evaluateSecondPredicate && secondPredicate.evaluate()) {
               return true;
            }

            TermUtils.backtrack(tmpInputArg2.getArgs());
            secondArg = null;
         } while (firstPredicate.couldReevaluationSucceed() && firstPredicate.evaluate());

         return false;
      }

      private boolean preMatch(Term inputArg2) {
         tmpInputArg2 = inputArg2.getTerm();
         secondArg = tmpInputArg2.copy(new HashMap<Variable, Variable>());
         if (Unifier.preMatch(tmpInputArg2.getArgs(), secondArg.getArgs())) {
            if (secondPredicateFactory == null) {
               secondPredicateFactory = getKnowledgeBase().getPredicateFactory(secondArg);
            }
            secondPredicate = secondPredicateFactory.getPredicate(secondArg.getArgs());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return firstPredicate == null || firstPredicate.couldReevaluationSucceed() || secondPredicate == null || secondPredicate.couldReevaluationSucceed();
      }
   }
}
