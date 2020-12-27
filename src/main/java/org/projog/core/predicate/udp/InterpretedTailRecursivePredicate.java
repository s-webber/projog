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
package org.projog.core.predicate.udp;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.event.SpyPoints.SpyPoint;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/**
 * A implementation of {@link TailRecursivePredicate} for interpreted user defined predicates.
 * <p>
 * The user defined predicate must be judged as eligible for <i>tail recursion optimisation</i> using the criteria used
 * by {@link TailRecursivePredicateMetaData}.
 *
 * @see InterpretedTailRecursivePredicateFactory
 * @see TailRecursivePredicateMetaData
 */
final class InterpretedTailRecursivePredicate extends TailRecursivePredicate {
   // TODO add exception handling ProjogException and CutException
   private final boolean isSpyPointEnabled;
   private final SpyPoint spyPoint;
   private final int numArgs;
   private final Term[] currentQueryArgs;
   private final boolean isRetryable;
   private final PredicateFactory[] firstClausePredicateFactories;
   private final Term[] firstClauseConsequentArgs;
   private final Term[] firstClauseOriginalTerms;
   private final PredicateFactory[] secondClausePredicateFactories;
   private final Term[] secondClauseConsequentArgs;
   private final Term[] secondClauseOriginalTerms;

   InterpretedTailRecursivePredicate(SpyPoint spyPoint, Term[] inputArgs, PredicateFactory[] firstClausePredicateFactories, Term[] firstClauseConsequentArgs,
               Term[] firstClauseOriginalTerms, PredicateFactory[] secondClausePredicateFactories, Term[] secondClauseConsequentArgs, Term[] secondClauseOriginalTerms,
               boolean isRetryable) {
      this.isSpyPointEnabled = spyPoint.isEnabled();
      this.spyPoint = spyPoint;
      this.numArgs = inputArgs.length;
      this.currentQueryArgs = new Term[numArgs];
      for (int i = 0; i < numArgs; i++) {
         currentQueryArgs[i] = inputArgs[i].getTerm();
      }

      this.firstClausePredicateFactories = firstClausePredicateFactories;
      this.firstClauseConsequentArgs = firstClauseConsequentArgs;
      this.firstClauseOriginalTerms = firstClauseOriginalTerms;
      this.secondClausePredicateFactories = secondClausePredicateFactories;
      this.secondClauseConsequentArgs = secondClauseConsequentArgs;
      this.secondClauseOriginalTerms = secondClauseOriginalTerms;
      this.isRetryable = isRetryable;
   }

   @Override
   protected boolean matchFirstRule() {
      final Map<Variable, Variable> sharedVariables = new HashMap<>();
      final Term[] newConsequentArgs = new Term[numArgs];
      for (int i = 0; i < numArgs; i++) {
         newConsequentArgs[i] = firstClauseConsequentArgs[i].copy(sharedVariables);
      }

      if (unify(currentQueryArgs, newConsequentArgs) == false) {
         return false;
      }

      for (int i = 0; i < firstClauseOriginalTerms.length; i++) {
         Term t = firstClauseOriginalTerms[i].copy(sharedVariables);
         if (!firstClausePredicateFactories[i].getPredicate(t.getArgs()).evaluate()) {
            return false;
         }
      }

      return true;
   }

   @Override
   protected boolean matchSecondRule() {
      final Map<Variable, Variable> sharedVariables = new HashMap<>();
      final Term[] newConsequentArgs = new Term[numArgs];
      for (int i = 0; i < numArgs; i++) {
         newConsequentArgs[i] = secondClauseConsequentArgs[i].copy(sharedVariables);
      }

      if (unify(currentQueryArgs, newConsequentArgs) == false) {
         return false;
      }

      for (int i = 0; i < secondClauseOriginalTerms.length - 1; i++) {
         Term t = secondClauseOriginalTerms[i].copy(sharedVariables);
         if (!secondClausePredicateFactories[i].getPredicate(t.getArgs()).evaluate()) {
            return false;
         }
      }

      Term finalTermArgs[] = secondClauseOriginalTerms[secondClauseOriginalTerms.length - 1].getArgs();
      for (int i = 0; i < numArgs; i++) {
         currentQueryArgs[i] = finalTermArgs[i].copy(sharedVariables);
      }

      return true;
   }

   /**
    * Unifies the arguments in the head (consequent) of a clause with a query.
    * <p>
    * When Prolog attempts to answer a query it searches its knowledge base for all rules with the same functor and
    * arity. For each rule founds it attempts to unify the arguments in the query with the arguments in the head
    * (consequent) of the rule. Only if the query and rule's head can be unified can it attempt to evaluate the body
    * (antecedent) of the rule to determine if the rule is true.
    *
    * @param inputArgs the arguments contained in the query
    * @param consequentArgs the arguments contained in the head (consequent) of the clause
    * @return {@code true} if the attempt to unify the arguments was successful
    * @see Term#unify(Term)
    */
   public static boolean unify(Term[] inputArgs, Term[] consequentArgs) {
      for (int i = 0; i < inputArgs.length; i++) {
         if (!inputArgs[i].unify(consequentArgs[i])) {
            return false;
         }
      }
      return true;
   }

   @Override
   protected void logCall() {
      if (isSpyPointEnabled) {
         spyPoint.logCall(this, currentQueryArgs);
      }
   }

   @Override
   protected void logRedo() {
      if (isSpyPointEnabled) {
         spyPoint.logCall(this, currentQueryArgs);
      }
   }

   @Override
   protected void logExit() {
      if (isSpyPointEnabled) {
         spyPoint.logExit(this, currentQueryArgs, 1);
      }
   }

   @Override
   protected void logFail() {
      if (isSpyPointEnabled) {
         spyPoint.logFail(this, currentQueryArgs);
      }
   }

   @Override
   protected void backtrack() {
      for (int i = 0; i < numArgs; i++) {
         currentQueryArgs[i].backtrack();
      }
   }

   @Override
   public boolean couldReevaluationSucceed() {
      return isRetryable;
   }
}
