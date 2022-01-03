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
package org.projog.core.predicate;

import org.projog.core.term.Term;

/**
 * Returns specialised implementations of {@link Predicate}.
 * <p>
 * There are two general types of predicates:
 * <ul>
 * <li><i>User defined predicates</i> are defined by a mixture of rules and facts constructed from Prolog syntax
 * consulted at runtime.</li>
 * <li><i>Built-in predicates</i> are written in Java. Built-in predicates can provide facilities that would not be
 * possible using pure Prolog syntax. The two predicates that are always available in Projog are
 * {@code pj_add_predicate/2} and {@code pj_add_arithmetic_operator/2}. The {@code pj_add_predicate/2} predicate allows
 * other predicates to be "plugged-in" to Projog.</li>
 * </ul>
 * <p>
 * <b>Note:</b> Rather than directly implementing {@code PredicateFactory} it is recommended to extend either
 * {@link org.projog.core.predicate.AbstractSingleResultPredicate} or
 * {@link org.projog.core.predicate.AbstractPredicateFactory}.
 * </p>
 *
 * @see Predicates#addPredicateFactory(PredicateKey, String)
 */
public interface PredicateFactory {
   /**
    * Returns a {@link Predicate} to be used in the evaluation of a goal.
    *
    * @param args the arguments to use in the evaluation of the goal
    * @return Predicate to be used in the evaluation of the goal
    * @see Predicate#evaluate()
    */
   Predicate getPredicate(Term[] args);

   /**
    * Should instances of this implementation be re-evaluated when backtracking?
    * <p>
    * Some goals (e.g. {@code X is 1}) are only meant to be evaluated once (the statement is either true or false) while
    * others (e.g. {@code repeat(3)}) are meant to be evaluated multiple times. For instances of {@code Predicate} that
    * are designed to possibly have {@link Predicate#evaluate()} called on them multiple times for the same individual
    * query this method should return {@code true}. For instances of {@code Predicate} that are designed to only be
    * evaluated once per individual query this method should return {@code false}.
    *
    * @return {@code true} if an attempt should be made to re-evaluate instances of implementing classes when
    * backtracking, {@code false} otherwise
    */
   boolean isRetryable();

   /**
    * Will attempting to re-evaluate this implementation always result in a cut?
    *
    * @return {@code true} if a cut will always be encountered when attempting to re-evaluate, {@code false} otherwise
    */
   default boolean isAlwaysCutOnBacktrack() {
      return false;
   }
}
