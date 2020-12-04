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

import org.projog.core.PreprocessablePredicateFactory;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

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
public final class Conjunction extends AbstractPredicateFactory implements PreprocessablePredicateFactory {
   @Override
   public Predicate getPredicate(Term arg1, Term arg2) {
      Predicate firstPredicate = getKnowledgeBase().getPredicateFactory(arg1).getPredicate(arg1.getArgs());
      if (firstPredicate.evaluate()) {
         return new ConjunctionPredicate(firstPredicate, getKnowledgeBase().getPredicateFactory(arg2), arg2);
      } else {
         return AbstractSingletonPredicate.FAIL;
      }
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term firstArg = term.getArgument(0);
      Term secondArg = term.getArgument(1);
      if (firstArg.getType().isVariable() || secondArg.getType().isVariable()) {
         return this;
      }

      PredicateFactory firstPredicateFactory = getKnowledgeBase().getPreprocessedPredicateFactory(firstArg);
      PredicateFactory secondPredicateFactory = getKnowledgeBase().getPreprocessedPredicateFactory(secondArg);
      if (firstPredicateFactory.isRetryable() || secondPredicateFactory.isRetryable()) {
         return new OptimisedRetryableConjuction(firstPredicateFactory, secondPredicateFactory);
      } else {
         return new OptimisedSingletonConjuction(firstPredicateFactory, secondPredicateFactory);
      }
   }

   private static final class OptimisedRetryableConjuction extends AbstractPredicateFactory {
      private final PredicateFactory firstPredicateFactory;
      private final PredicateFactory secondPredicateFactory;

      OptimisedRetryableConjuction(PredicateFactory firstPredicateFactory, PredicateFactory secondPredicateFactory) {
         this.firstPredicateFactory = firstPredicateFactory;
         this.secondPredicateFactory = secondPredicateFactory;
      }

      @Override
      public Predicate getPredicate(Term arg1, Term arg2) {
         Predicate firstPredicate = firstPredicateFactory.getPredicate(arg1.getArgs());
         if (firstPredicate.evaluate()) {
            return new ConjunctionPredicate(firstPredicate, secondPredicateFactory, arg2);
         } else {
            return AbstractSingletonPredicate.FAIL;
         }
      }
   }

   private static final class OptimisedSingletonConjuction extends AbstractSingletonPredicate {
      private final PredicateFactory firstPredicateFactory;
      private final PredicateFactory secondPredicateFactory;

      OptimisedSingletonConjuction(PredicateFactory firstPredicateFactory, PredicateFactory secondPredicateFactory) {
         this.firstPredicateFactory = firstPredicateFactory;
         this.secondPredicateFactory = secondPredicateFactory;
      }

      @Override
      public boolean evaluate(Term arg1, Term arg2) {
         return firstPredicateFactory.getPredicate(arg1.getArgs()).evaluate() && secondPredicateFactory.getPredicate(arg2.getTerm().getArgs()).evaluate();
      }
   }

   private static final class ConjunctionPredicate implements Predicate {
      private final Predicate firstPredicate;
      private final PredicateFactory secondPredicateFactory;
      private final Term originalSecondArgument;
      private Predicate secondPredicate;
      private Term copySecondArgument;

      private ConjunctionPredicate(Predicate firstPredicate, PredicateFactory secondPredicateFactory, Term secondArgument) {
         this.firstPredicate = firstPredicate;
         this.secondPredicateFactory = secondPredicateFactory;
         this.originalSecondArgument = secondArgument;
      }

      @Override
      public boolean evaluate() {
         do {
            if (secondPredicate == null) {
               copySecondArgument = originalSecondArgument.getTerm();
               secondPredicate = secondPredicateFactory.getPredicate(copySecondArgument.getArgs());
               if (secondPredicate.evaluate()) {
                  return true;
               }
            } else if (secondPredicate.couldReevaluationSucceed() && secondPredicate.evaluate()) {
               return true;
            }

            secondPredicate = null;
            TermUtils.backtrack(copySecondArgument.getArgs());
         } while (firstPredicate.couldReevaluationSucceed() && firstPredicate.evaluate());

         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return firstPredicate.couldReevaluationSucceed()
                     || (secondPredicate != null && secondPredicate.couldReevaluationSucceed())
                     || (copySecondArgument == null && secondPredicateFactory.isRetryable());
      }
   }
}
