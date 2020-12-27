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
 * Represents a goal.
 *
 * @see PredicateFactory
 * @see Predicates#addPredicateFactory(PredicateKey, String)
 */
public interface Predicate {
   /**
    * Attempts to satisfy the goal this instance represents.
    * <p>
    * Calling this method multiple times on a single instance allows all possible answers to be identified. An attempt
    * to find a solution carries on from where the last successful call finished.
    * <p>
    * If {@link PredicateFactory#isRetryable()} returns {@code false} then this method should only be called once per
    * individual query (no attempt should be made to find alternative solutions).
    * <p>
    * If {@link PredicateFactory#isRetryable()} returns {@code true} then, in order to find all possible solutions for
    * an individual query, this method should be recalled on backtracking until it returns {@code false}.
    *
    * @return {@code true} if it was possible to satisfy the clause, {@code false} otherwise
    * @see PredicateFactory#getPredicate(Term[])
    */
   boolean evaluate();

   /**
    * Could the next re-evaluation of this instance succeed?
    * <p>
    * Specifies whether a specific instance of a specific implementation of {@code Predicate}, that has already had
    * {@link #evaluate()} called on it at least once, could possibly return {@code true} the next time
    * {@link #evaluate()} is called on it. i.e. is it worth trying to continue to find solutions for the specific query
    * this particular instance represents and has been evaluating?
    * <p>
    * (Note: the difference between this method and {@link PredicateFactory#isRetryable()} is that
    * {@link PredicateFactory#isRetryable()} deals with whether, in general, a specific <i>implementation</i> (rather
    * than <i>instance</i>) of {@code Predicate} could <i>ever</i> produce multiple answers for an individual query.)
    *
    * @return {@code true} if an attempt to re-evaluate this instance could possible succeed, {@code false} otherwise
    */
   boolean couldReevaluationSucceed();
}
