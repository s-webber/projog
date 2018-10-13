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
package org.projog.core.udp.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.PredicateFactory;
import org.projog.core.SpyPoints.SpyPoint;
import org.projog.core.term.Term;
import org.projog.core.term.Unifier;
import org.projog.core.term.Variable;
import org.projog.core.udp.TailRecursivePredicate;
import org.projog.core.udp.TailRecursivePredicateMetaData;

/**
 * A implementation of {@link TailRecursivePredicate} for interpreted user defined predicates.
 * <p>
 * The user defined predicate must be judged as eligible for <i>tail recursion optimisation</i> using the criteria used
 * by {@link TailRecursivePredicateMetaData}.
 * </p>
 * <img src="doc-files/InterpretedTailRecursivePredicateFactory.png">
 *
 * @see InterpretedTailRecursivePredicateFactory
 * @see TailRecursivePredicateMetaData
 */
final class InterpretedTailRecursivePredicate extends TailRecursivePredicate {
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

      if (Unifier.preMatch(currentQueryArgs, newConsequentArgs) == false) {
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

      if (Unifier.preMatch(currentQueryArgs, newConsequentArgs) == false) {
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

   @Override
   protected void logCall() {
      if (spyPoint != null) {
         spyPoint.logCall(this, currentQueryArgs);
      }
   }

   @Override
   protected void logRedo() {
      if (spyPoint != null) {
         spyPoint.logCall(this, currentQueryArgs);
      }
   }

   @Override
   protected void logExit() {
      if (spyPoint != null) {
         spyPoint.logExit(this, currentQueryArgs, 1);
      }
   }

   @Override
   protected void logFail() {
      if (spyPoint != null) {
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
