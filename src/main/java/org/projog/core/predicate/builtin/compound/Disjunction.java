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

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.term.Term;

/* TEST
 %QUERY true; true
 %ANSWER/
 %ANSWER/
 %TRUE_NO true; fail
 %TRUE fail; true
 %FALSE fail; fail

 %QUERY true; true; true
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %TRUE_NO true; fail; fail
 %TRUE_NO fail; true; fail
 %TRUE fail; fail; true
 %QUERY true; true; fail
 %ANSWER/
 %ANSWER/
 %NO
 %QUERY true; fail; true
 %ANSWER/
 %ANSWER/
 %QUERY fail; true; true
 %ANSWER/
 %ANSWER/
 %FALSE fail; fail; fail

 a :- true.
 b :- true.
 c :- true.
 d :- true.
 %QUERY a;b;c
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %QUERY a;b;z
 %ANSWER/
 %ANSWER/
 %NO
 %QUERY a;y;c
 %ANSWER/
 %ANSWER/
 %TRUE_NO a;y;z
 %QUERY x;b;c
 %ANSWER/
 %ANSWER/
 %TRUE_NO x;b;z
 %TRUE x;y;c
 %FALSE x;y;z

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

 %QUERY p1(X, Y, Z)
 %ANSWER
 % X=1
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=2
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=3
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=a
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=b
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=c
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=1
 % Y=b
 % Z=[a,b,c]
 %ANSWER
 %ANSWER
 % X=3
 % Y=c
 % Z=[1,2,3]
 %ANSWER
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 % Z=[q,w,e,r,t,y]
 %ANSWER

 %QUERY p2(X); p2(X); p2(X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3

 %QUERY p2(X); p3(X); p2(X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 %ANSWER X=a
 %ANSWER X=b
 %ANSWER X=c
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3

 %QUERY X=12; X=27; X=56
 %ANSWER X=12
 %ANSWER X=27
 %ANSWER X=56

 %QUERY p2(X); X=12; p3(X); X=27; p2(X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 %ANSWER X=12
 %ANSWER X=a
 %ANSWER X=b
 %ANSWER X=c
 %ANSWER X=27
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
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
public final class Disjunction extends AbstractPredicateFactory implements PreprocessablePredicateFactory {
   @Override
   public PredicateFactory preprocess(Term term) {
      Term arg1 = term.getArgument(0);
      Term arg2 = term.getArgument(1);
      if (PartialApplicationUtils.isAtomOrStructure(arg1) && PartialApplicationUtils.isAtomOrStructure(arg2)) {
         if (getPredicates().getPredicateFactory(arg1) instanceof IfThen) {
            Term conditionTerm = arg1.getArgument(0);
            Term thenTerm = arg1.getArgument(1);
            PredicateFactory condition = getPredicates().getPreprocessedPredicateFactory(conditionTerm);
            PredicateFactory thenPf = getPredicates().getPreprocessedPredicateFactory(thenTerm);
            PredicateFactory elsePf = getPredicates().getPreprocessedPredicateFactory(arg2);
            return new OptimisedIfThenElse(condition, thenPf, elsePf);
         } else {
            PredicateFactory pf1 = getPredicates().getPreprocessedPredicateFactory(arg1);
            PredicateFactory pf2 = getPredicates().getPreprocessedPredicateFactory(arg2);
            return new OptimisedDisjunction(pf1, pf2);
         }
      } else {
         return this;
      }
   }

   private class OptimisedDisjunction implements PredicateFactory {
      private final PredicateFactory pf1;
      private final PredicateFactory pf2;

      OptimisedDisjunction(PredicateFactory pf1, PredicateFactory pf2) {
         this.pf1 = pf1;
         this.pf2 = pf2;
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return new DisjunctionPredicate(pf1, pf2, args[0], args[1]);
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
      public Predicate getPredicate(Term[] args) {
         Term ifThenTerm = args[0];
         Term conditionTerm = ifThenTerm.getArgument(0);
         Predicate conditionPredicate = condition.getPredicate(conditionTerm.getArgs());
         if (conditionPredicate.evaluate()) {
            return thenPf.getPredicate(ifThenTerm.getArgument(1).getTerm().getArgs());
         } else {
            conditionTerm.backtrack();
            return elsePf.getPredicate(args[1].getArgs());
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

   @Override
   protected Predicate getPredicate(Term firstArg, Term secondArg) {
      if (getPredicates().getPredicateFactory(firstArg) instanceof IfThen) {
         return createIfThenElse(firstArg, secondArg);
      } else {
         return createDisjunction(firstArg, secondArg);
      }
   }

   private Predicate createIfThenElse(Term ifThenTerm, Term elseTerm) {
      Term conditionTerm = ifThenTerm.getArgument(0);
      Term thenTerm = ifThenTerm.getArgument(1);

      Predicates p = getPredicates();
      Predicate conditionPredicate = p.getPredicate(conditionTerm);
      if (conditionPredicate.evaluate()) {
         return p.getPredicate(thenTerm.getTerm());
      } else {
         conditionTerm.backtrack();
         return p.getPredicate(elseTerm);
      }
   }

   private DisjunctionPredicate createDisjunction(Term firstArg, Term secondArg) {
      return new DisjunctionPredicate(null, null, firstArg, secondArg);
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
            firstPredicate = getPredicate(pf1, inputArg1);
            if (firstPredicate.evaluate()) {
               return true;
            }
         } else if (secondPredicate == null && firstPredicate.couldReevaluationSucceed() && firstPredicate.evaluate()) {
            return true;
         }

         if (secondPredicate == null) {
            inputArg1.backtrack();
            secondPredicate = getPredicate(pf2, inputArg2);
            return secondPredicate.evaluate();
         } else {
            return secondPredicate.couldReevaluationSucceed() && secondPredicate.evaluate();
         }
      }

      private Predicate getPredicate(PredicateFactory pf, Term t) {
         if (pf == null) {
            return getPredicates().getPredicate(t.getTerm());
         } else {
            return pf.getPredicate(t.getTerm().getArgs());
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return secondPredicate == null || secondPredicate.couldReevaluationSucceed();
      }
   }
}
