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
package org.projog.core.udp;

import org.projog.core.Predicate;

/**
 * A template for implementations of {@code Predicate} that are tail recursive.
 * <p>
 * It is common for Prolog developers to define predicates using recursion. Although recursive programs can be concise
 * and elegant they do require increased stack space for each iteration - which after many iterations will cause a
 * {@code java.lang.StackOverflowError}. Where it can determine it is safe to do, Projog converts recursive user defined
 * predicates into iterative versions - requiring a constant stack space regardless of the number of iterations. This
 * technique is known as <i>tail recursion optimisation</i> or <i>last call optimisation</i>. The algorithm for
 * implementing tail recursion optimisation is encapsulated in {@code TailRecursivePredicate}. Subclasses of
 * {@code TailRecursivePredicate} can implement the logic of evaluating the clauses of a specific user defined predicate
 * without having to redefine the tail recursion optimisation algorithm.
 * </p>
 * <p>
 * For a user defined predicate to be implemented using {@code TailRecursivePredicate} it must be judged as eligible for
 * <i>tail recursion optimisation</i> using the criteria used by {@link TailRecursivePredicateMetaData}.
 * </p>
 * <img src="doc-files/TailRecursivePredicate.png">
 *
 * @see TailRecursivePredicateMetaData
 */
public abstract class TailRecursivePredicate implements Predicate {
   private boolean retrying;
   private boolean succededOnPreviousGo;

   @Override
   public final boolean evaluate() {
      if (retrying) {
         logRedo();
      } else {
         logCall();
         retrying = false;
      }

      while (true) {
         if (succededOnPreviousGo) {
            backtrack();
            succededOnPreviousGo = false;
         } else {
            if (matchFirstRule()) {
               succededOnPreviousGo = true;
               logExit();
               return true;
            } else {
               backtrack();
            }
         }

         if (!matchSecondRule()) {
            logFail();
            return false;
         }
      }
   }

   /**
    * Match the first rule of the tail recursive predicate.
    * <p>
    * If the head of the first rule is matched then the rule has been successfully evaluated.
    *
    * @return {@code true} if the first rule is matched, else {@code false}
    */
   protected abstract boolean matchFirstRule();

   /**
    * Match the second rule of the tail recursive predicate.
    * <p>
    * If the second rule is matched then the attempt at evaluating the rule continues for another level of recursion.
    *
    * @return {@code true} if the second rule is matched, else {@code false}
    */
   protected abstract boolean matchSecondRule();

   /**
    * Backtracks the arguments to before the last attempt to match the first rule.
    */
   protected abstract void backtrack();

   protected abstract void logCall();

   protected abstract void logRedo();

   protected abstract void logExit();

   protected abstract void logFail();
}
