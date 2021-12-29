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

import java.util.Objects;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.term.Term;

/* TEST
%FAIL \+ true
%TRUE \+ fail

% Note: "not" is a synonym for "\+".
%FAIL not(true)
%TRUE not(fail)

%?- \+ [A,B,C,9]=[1,2,3,4], A=6, B=7, C=8
% A=6
% B=7
% C=8

%?- \+ ((X=Y,1>2)), X=1, Y=2
% X=1
% Y=2

test1(X,Y) :- \+ ((X=Y,1>2)), X=1, Y=2.

%?- test1(X,Y)
% X=1
% Y=2

test2(X) :- \+ \+ X=1, X=2.

%?- test2(X)
% X=2

%FAIL test2(1)
%FAIL test2(2)
*/
/**
 * <code>\+ X</code> - "not".
 * <p>
 * The <code>\+ X</code> goal succeeds if an attempt to satisfy the goal represented by the term <code>X</code> fails.
 * The <code>\+ X</code> goal fails if an attempt to satisfy the goal represented by the term <code>X</code> succeeds.
 * </p>
 */
public final class Not extends AbstractSingleResultPredicate implements PreprocessablePredicateFactory {
   @Override
   protected boolean evaluate(Term t) {
      PredicateFactory pf = getPredicates().getPredicateFactory(t);
      return evaluateNot(t, pf);
   }

   private static boolean evaluateNot(Term t, PredicateFactory pf) {
      Predicate p = pf.getPredicate(t.getArgs());
      if (!p.evaluate()) {
         t.backtrack();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term arg = term.getArgument(0);
      if (PartialApplicationUtils.isAtomOrStructure(arg)) {
         return new OptimisedNot(getPredicates().getPreprocessedPredicateFactory(arg));
      } else {
         return this;
      }
   }

   private static final class OptimisedNot extends AbstractSingleResultPredicate {
      private final PredicateFactory pf;

      OptimisedNot(PredicateFactory pf) {
         this.pf = Objects.requireNonNull(pf);
      }

      @Override
      protected boolean evaluate(Term arg) {
         return evaluateNot(arg, pf);
      }
   }
}
