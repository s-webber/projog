/*
 * Copyright 2013-2014 S. Webber
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
package org.projog.core;

import org.projog.core.term.Term;

/**
 * Represents a goal.
 * <p>
 * <b>Note:</b> Rather than directly implementing {@code Predicate} it is recommended to extend either
 * {@link org.projog.core.function.AbstractSingletonPredicate} or
 * {@link org.projog.core.function.AbstractRetryablePredicate}.
 *
 * @see PredicateFactory
 * @see KnowledgeBase#addPredicateFactory(PredicateKey, String)
 */
public interface Predicate {
   /**
    * Attempts to satisfy the goal this instance represents.
    * <p>
    * Calling this method multiple times on a single instance allows all possible answers to be identified. An attempt
    * to find a solution carries on from where the last successful call finished.
    * <p>
    * If {@link #isRetryable()} returns {@code false} then this method should only be called once per individual query
    * (no attempt should be made to find alternative solutions).
    * <p>
    * If {@link #isRetryable()} returns {@code true} then, in order to find all possible solutions for an individual
    * query, this method should be recalled on backtracking until it returns {@code false}.
    * <p>
    * <b>Note:</b> It is recommended that implementations of {@code Predicate} also implement an overloaded version of
    * {@code evaluate} that, instead of having a single varargs parameter, accepts a number of individual {@code Term}
    * parameters. The exact number of parameters accepted should be the same as the number of arguments expected when
    * evaluating the goal this object represents. For example, a {@code Predicate} that does not expect any arguments
    * should implement {@code public boolean evaluate()} while a {@code Predicate} that expects three arguments should
    * implement {@code public boolean evaluate(Term, Term, Term)}. The reason why this is recommended is so that java
    * code generated at runtime for user defined predicates will be able to use the overloaded method rather than the
    * varargs version and thus avoid the unnecessary overhead of creating a new {@code Term} array for each method
    * invocation.
    *
    * @param args the arguments to use in the evaluation of this goal
    * @return {@code true} if it was possible to satisfy the clause, {@code false} otherwise
    * @see PredicateFactory#getPredicate(Term[])
    */
   boolean evaluate();

   /**
    * Could the next re-evaluation of this instance succeed?
    * <p>
    * Specifies whether a specific instance of a specific implementation of {@code Predicate}, that has already had
    * {@link #evaluate(Term[])} called on it at least once, could possibly return {@code true} the next time
    * {@link #evaluate(Term[])} is called on it. i.e. is it worth trying to continue to find solutions for the specific
    * query this particular instance represents and has been evaluating?
    * <p>
    * (Note: the difference between this method and {@link #isRetryable()} is that {@link #isRetryable()} deals with
    * whether, in general, a specific <i>implementation</i> (rather than <i>instance</i>) of {@code Predicate} could
    * <i>ever</i> produce multiple answers for an individual query.)
    *
    * @return {@code true} if an attempt to re-evaluate this instance could possible succeed, {@code false} otherwise
    */
   boolean couldReevaluationSucceed();
}
