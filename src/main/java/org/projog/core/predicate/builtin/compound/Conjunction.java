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
package org.projog.core.predicate.builtin.compound;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* TEST
%TRUE true, true
%FAIL true, fail
%FAIL fail, true
%FAIL fail, fail

%TRUE true, true, true
%FAIL true, fail, fail
%FAIL fail, true, fail
%FAIL fail, fail, true
%FAIL true, true, fail
%FAIL true, fail, true
%FAIL fail, true, true
%FAIL fail, fail, fail

b :- true.
c :- true.
d :- true.
y :- true.
a :- b,c,d.
x :- y,z.
%TRUE a
%FAIL x

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

%?- p1(X, Y, Z)
% X=1
% Y=a
% Z=[q,w,e,r,t,y]
% X=1
% Y=b
% Z=[a,b,c]
% X=1
% Y=b
% Z=[q,w,e,r,t,y]
% X=1
% Y=c
% Z=[q,w,e,r,t,y]
% X=2
% Y=a
% Z=[q,w,e,r,t,y]
% X=2
% Y=b
% Z=[q,w,e,r,t,y]
% X=2
% Y=c
% Z=[q,w,e,r,t,y]
% X=3
% Y=a
% Z=[q,w,e,r,t,y]
% X=3
% Y=b
% Z=[q,w,e,r,t,y]
% X=3
% Y=c
% Z=[1,2,3]
% X=3
% Y=c
% Z=[q,w,e,r,t,y]

%?- p2(X), p2(X), p2(X)
% X=1
% X=2
% X=3

%FAIL p2(X), p3(X), p2(X)
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
   protected Predicate getPredicate(Term arg1, Term arg2) {
      Predicate firstPredicate = getPredicates().getPredicateFactory(arg1).getPredicate(arg1.getArgs());
      if (firstPredicate.evaluate()) {
         return new ConjunctionPredicate(firstPredicate, getPredicates().getPredicateFactory(arg2), arg2);
      } else {
         return PredicateUtils.FALSE;
      }
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term firstArg = term.getArgument(0);
      Term secondArg = term.getArgument(1);
      if (firstArg.getType().isVariable() || secondArg.getType().isVariable()) {
         return this;
      }

      PredicateFactory firstPredicateFactory = getPredicates().getPreprocessedPredicateFactory(firstArg);
      PredicateFactory secondPredicateFactory = getPredicates().getPreprocessedPredicateFactory(secondArg);
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
      protected Predicate getPredicate(Term arg1, Term arg2) {
         Predicate firstPredicate = firstPredicateFactory.getPredicate(arg1.getArgs());
         if (firstPredicate.evaluate()) {
            return new ConjunctionPredicate(firstPredicate, secondPredicateFactory, arg2);
         } else {
            return PredicateUtils.FALSE;
         }
      }

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         if (secondPredicateFactory.isAlwaysCutOnBacktrack()) {
            return true;
         } else if (firstPredicateFactory.isAlwaysCutOnBacktrack() && !secondPredicateFactory.isRetryable()) {
            return true;
         } else {
            return false;
         }
      }
   }

   private static final class OptimisedSingletonConjuction extends AbstractSingleResultPredicate {
      private final PredicateFactory firstPredicateFactory;
      private final PredicateFactory secondPredicateFactory;

      OptimisedSingletonConjuction(PredicateFactory firstPredicateFactory, PredicateFactory secondPredicateFactory) {
         this.firstPredicateFactory = firstPredicateFactory;
         this.secondPredicateFactory = secondPredicateFactory;
      }

      @Override
      protected boolean evaluate(Term arg1, Term arg2) {
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
