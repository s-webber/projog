/*
 * Copyright 2012 - 2018 S. Webber
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
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.term.Term;

/* TEST
%?- true; true
%YES
%YES
%TRUE_NO true; fail
%TRUE fail; true
%FAIL fail; fail

%?- true; true; true
%YES
%YES
%YES
%TRUE_NO true; fail; fail
%TRUE_NO fail; true; fail
%TRUE fail; fail; true
%?- true; true; fail
%YES
%YES
%NO
%?- true; fail; true
%YES
%YES
%?- fail; true; true
%YES
%YES
%FAIL fail; fail; fail

a :- true.
b :- true.
c :- true.
d :- true.
%?- a;b;c
%YES
%YES
%YES
%?- a;b;z
%YES
%YES
%NO
%?- a;y;c
%YES
%YES
%TRUE_NO a;y;z
%?- x;b;c
%YES
%YES
%TRUE_NO x;b;z
%TRUE x;y;c
%FAIL x;y;z

p2(1) :- true.
p2(2) :- true.
p2(3) :- true.

p3(a) :- true.
p3(b) :- true.
p3(c) :- true.

p4(1, b, [a,b,c]) :- true.
p4(3, c, [1,2,3]) :- true.
p4(X, Y, [q,w,e,r,t,y]) :- true.

p1(X, Y, Z) :- p2(X); p3(Y); p4(X,Y,Z).

%?- p1(X, Y, Z)
% X=1
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=2
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=3
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% Y=a
% Z=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% Y=b
% Z=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% Y=c
% Z=UNINSTANTIATED VARIABLE
% X=1
% Y=b
% Z=[a,b,c]
% X=3
% Y=c
% Z=[1,2,3]
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
% Z=[q,w,e,r,t,y]

%?- p2(X); p2(X); p2(X)
% X=1
% X=2
% X=3
% X=1
% X=2
% X=3
% X=1
% X=2
% X=3

%?- p2(X); p3(X); p2(X)
% X=1
% X=2
% X=3
% X=a
% X=b
% X=c
% X=1
% X=2
% X=3

%?- X=12; X=27; X=56
% X=12
% X=27
% X=56

%?- p2(X); X=12; p3(X); X=27; p2(X)
% X=1
% X=2
% X=3
% X=12
% X=a
% X=b
% X=c
% X=27
% X=1
% X=2
% X=3
 */
/**
 * <code>X;Y</code> - disjunction.
 * <p>
 * <code>X;Y</code> specifies a disjunction of goals. <code>X;Y</code> succeeds if either <code>X</code> succeeds
 * <i>or</i> <code>Y</code> succeeds. If <code>X</code> fails then an attempt is made to satisfy <code>Y</code>. If
 * <code>Y</code> fails the entire disjunction fails.
 * </p>
 * <p>
 * <b>Note:</b> The behaviour of this predicate changes when its first argument is of the form <code>-&gt;/2</code>,
 * i.e. the <i>"if/then"</i> predicate. When a <code>-&gt;/2</code> predicate is the first argument of a
 * <code>;/2</code> predicate then the resulting behaviour is a <i>"if/then/else"</i> statement of the form
 * <code>((if-&gt;then);else)</code>.
 * </p>
 *
 * @see IfThen
 */
public final class Disjunction implements PredicateFactory {
   private final Predicates predicates;

   public Disjunction(KnowledgeBase kb) {
      this.predicates = kb.getPredicates();
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term arg1 = term.firstArgument();
      Term arg2 = term.secondArgument();
      if (PartialApplicationUtils.isAtomOrStructure(arg1) && PartialApplicationUtils.isAtomOrStructure(arg2)) {
         if (predicates.getPredicateFactory(arg1) instanceof IfThen) {
            Term conditionTerm = arg1.firstArgument();
            Term thenTerm = arg1.secondArgument();
            if (conditionTerm.getType().isVariable() || thenTerm.getType().isVariable()) {
               return this;
            }
            PredicateFactory condition = predicates.getPreprocessedPredicateFactory(conditionTerm);
            PredicateFactory thenPf = predicates.getPreprocessedPredicateFactory(thenTerm);
            PredicateFactory elsePf = predicates.getPreprocessedPredicateFactory(arg2);
            return new OptimisedIfThenElse(condition, thenPf, elsePf);
         } else {
            PredicateFactory pf1 = predicates.getPreprocessedPredicateFactory(arg1);
            PredicateFactory pf2 = predicates.getPreprocessedPredicateFactory(arg2);
            return new OptimisedDisjunction(pf1, pf2);
         }
      } else {
         return this;
      }
   }

   @Override
   public Predicate getPredicate(Term term) {
      Term firstArgument = term.firstArgument();
      Term secondArgument = term.secondArgument();

      if (predicates.getPredicateFactory(firstArgument) instanceof IfThen) {
         return createIfThenElse(firstArgument, secondArgument);
      } else {
         return new DisjunctionPredicate(predicates.getPredicateFactory(firstArgument), predicates.getPredicateFactory(secondArgument), firstArgument, secondArgument);
      }
   }

   private Predicate createIfThenElse(Term ifThenTerm, Term elseTerm) {
      Term conditionTerm = ifThenTerm.firstArgument();
      Term thenTerm = ifThenTerm.secondArgument();

      Predicate conditionPredicate = predicates.getPredicate(conditionTerm);
      if (conditionPredicate.evaluate()) {
         return predicates.getPredicate(thenTerm.getTerm());
      } else {
         conditionTerm.backtrack();
         return predicates.getPredicate(elseTerm);
      }
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   private class OptimisedDisjunction implements PredicateFactory {
      private final PredicateFactory pf1;
      private final PredicateFactory pf2;

      OptimisedDisjunction(PredicateFactory pf1, PredicateFactory pf2) {
         this.pf1 = pf1;
         this.pf2 = pf2;
      }

      @Override
      public Predicate getPredicate(Term term) {
         return new DisjunctionPredicate(pf1, pf2, term.firstArgument(), term.secondArgument());
      }

      @Override
      public boolean isRetryable() {
         return true;
      }
   }

   private static class OptimisedIfThenElse implements PredicateFactory {
      private final PredicateFactory condition;
      private final PredicateFactory thenPf;
      private final PredicateFactory elsePf;

      OptimisedIfThenElse(PredicateFactory condition, PredicateFactory thenPf, PredicateFactory elsePf) {
         this.condition = condition;
         this.thenPf = thenPf;
         this.elsePf = elsePf;
      }

      @Override
      public Predicate getPredicate(Term term) {
         Term ifThenTerm = term.firstArgument();
         Term conditionTerm = ifThenTerm.firstArgument();
         Predicate conditionPredicate = condition.getPredicate(conditionTerm);
         if (conditionPredicate.evaluate()) {
            return thenPf.getPredicate(ifThenTerm.secondArgument().getTerm());
         } else {
            conditionTerm.backtrack();
            return elsePf.getPredicate(term.secondArgument());
         }
      }

      @Override
      public boolean isRetryable() {
         return thenPf.isRetryable() || elsePf.isRetryable();
      }

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return thenPf.isAlwaysCutOnBacktrack() && elsePf.isAlwaysCutOnBacktrack();
      }
   }

   private final class DisjunctionPredicate implements Predicate {
      private final PredicateFactory pf1;
      private final PredicateFactory pf2;
      private final Term inputArg1;
      private final Term inputArg2;
      private Predicate firstPredicate;
      private Predicate secondPredicate;

      private DisjunctionPredicate(PredicateFactory pf1, PredicateFactory pf2, Term inputArg1, Term inputArg2) {
         this.pf1 = pf1;
         this.pf2 = pf2;
         this.inputArg1 = inputArg1;
         this.inputArg2 = inputArg2;
      }

      @Override
      public boolean evaluate() {
         if (firstPredicate == null) {
            firstPredicate = pf1.getPredicate(inputArg1);
            if (firstPredicate.evaluate()) {
               return true;
            }
         } else if (secondPredicate == null && firstPredicate.couldReevaluationSucceed() && firstPredicate.evaluate()) {
            return true;
         }

         if (secondPredicate == null) {
            inputArg1.backtrack();
            secondPredicate = pf2.getPredicate(inputArg2);
            return secondPredicate.evaluate();
         } else {
            return secondPredicate.couldReevaluationSucceed() && secondPredicate.evaluate();
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return secondPredicate == null || secondPredicate.couldReevaluationSucceed();
      }
   }
}
