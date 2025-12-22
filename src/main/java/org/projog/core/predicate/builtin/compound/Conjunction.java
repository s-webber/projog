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

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Term;

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
public final class Conjunction implements PredicateFactory {
   private final KnowledgeBase kb;
   private final PredicateFactory firstPredicateFactory;
   private final PredicateFactory secondPredicateFactory;

   public Conjunction(KnowledgeBase kb) {
      this(kb, kb.getPredicates().placeholder(), kb.getPredicates().placeholder());
   }

   private Conjunction(KnowledgeBase kb, PredicateFactory firstPredicateFactory, PredicateFactory secondPredicateFactory) {
      this.kb = kb;
      this.firstPredicateFactory = firstPredicateFactory;
      this.secondPredicateFactory = secondPredicateFactory;
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term firstArg = term.firstArgument();
      Term secondArg = term.secondArgument();
      if (firstArg.getType().isVariable() || secondArg.getType().isVariable()) {
         return this;
      }

      PredicateFactory firstPredicateFactory = kb.getPredicates().getPreprocessedPredicateFactory(firstArg);
      PredicateFactory secondPredicateFactory = kb.getPredicates().getPreprocessedPredicateFactory(secondArg);
      return new Conjunction(kb, firstPredicateFactory, secondPredicateFactory);
   }

   @Override
   public Predicate getPredicate(Term term) {
      Predicate firstPredicate = firstPredicateFactory.getPredicate(term.firstArgument());
      if (!firstPredicate.evaluate()) {
         return PredicateUtils.FALSE;
      }

      if (!firstPredicate.couldReevaluationSucceed()) {
         return secondPredicateFactory.getPredicate(term.secondArgument().getTerm());
      }

      return new ConjunctionPredicate(firstPredicate, secondPredicateFactory, term.secondArgument());
   }

   @Override
   public boolean isRetryable() {
      return firstPredicateFactory.isRetryable() || secondPredicateFactory.isRetryable();
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
               secondPredicate = secondPredicateFactory.getPredicate(copySecondArgument);
               if (secondPredicate.evaluate()) {
                  return true;
               }
            } else if (secondPredicate.couldReevaluationSucceed() && secondPredicate.evaluate()) {
               return true;
            }

            secondPredicate = null;
            copySecondArgument.backtrack();
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
