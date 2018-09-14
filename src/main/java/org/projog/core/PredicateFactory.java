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

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/**
 * Returns specialised implementations of {@link Predicate}.
 * <p>
 * There are two general types of predicates:
 * <ul>
 * <li><i>User defined predicates</i> are defined by a mixture of rules and facts constructed from Prolog syntax
 * consulted at runtime.</li>
 * <li><i>Plugin predicates</i> are written in Java. Plugin predicates can provide facilities that would not be possible
 * using pure Prolog syntax. The two predicates that are always available in Projog are {@code pj_add_predicate/2} and
 * {@code pj_add_calculatable/2}. The {@code pj_add_predicate/2} predicate allows other predicates to be "plugged-in" to
 * Projog.</li>
 * </ul>
 * <p>
 * <b>Note:</b> Rather than directly implementing {@code PredicateFactory} it is recommended to extend either
 * {@link org.projog.core.function.AbstractSingletonPredicate} or
 * {@link org.projog.core.function.AbstractRetryablePredicate}.
 * </p>
 * <p>
 * <a href="doc-files/PredicateFactory.png">View Class Diagram</a>
 * </p>
 *
 * @see KnowledgeBase#addPredicateFactory(PredicateKey, String)
 */
public interface PredicateFactory {
   /**
    * Provides a reference to a {@code KnowledgeBase}.
    * <p>
    * This method will be called by {@link KnowledgeBase#addPredicateFactory(PredicateKey, String)} when this class is
    * registered with a {@code KnowledgeBase} - meaning this object will always have access to a {@code KnowledgeBase}
    * by the time its {@code getPredicate} method is invoked.
    */
   void setKnowledgeBase(KnowledgeBase kb);

   /**
    * Returns a {@link Predicate} to be used in the evaluation of a goal.
    * <p>
    * <b>Note:</b> It is recommended that implementations of {@code PredicateFactory} also implement an overloaded
    * version of {@code getPredicate} that, instead of having a single varargs parameter, accepts a number of individual
    * {@code Term} parameters. The exact number of parameters accepted should be the same as the number of arguments
    * expected when evaluating the goal this object represents. For example, a {@code PredicateFactory} that does not
    * expect any arguments should implement {@code getPredicate()} while a {@code PredicateFactory} that expects three
    * arguments should implement {@code getPredicate(Term, Term, Term)}. The reason why this is recommended is so that
    * java code generated at runtime for user defined predicates will be able to use the overloaded method rather than
    * the varargs version and thus avoid the unnecessary overhead of creating a new {@code Term} array for each method
    * invocation.
    * </p>
    * <p>
    * <b>Note:</b> The above recommendations are <i>not</i> required for subclasses of
    * {@link AbstractSingletonPredicate}. (As the compiler is aware that
    * {@link AbstractSingletonPredicate#getPredicate(Term...)} always returns {@code this}.
    * </p>
    *
    * @param args the arguments to use in the evaluation of the goal
    * @return Predicate to be used in the evaluation of the goal
    * @see Predicate#evaluate(Term[])
    */
   Predicate getPredicate(Term... args);

   /**
    * Should instances of this implementation be re-evaluated when backtracking?
    * <p>
    * Some goals (e.g. {@code X is 1}) are only meant to be evaluated once (the statement is either true or false) while
    * others (e.g. {@code repeat(3)}) are meant to be evaluated multiple times. For instances of {@code Predicate} that
    * are designed to possibly have {@link #evaluate(Term[])} called on them multiple times for the same individual
    * query this method should return {@code true}. For instances of {@code Predicate} that are designed to only be
    * evaluated once per individual query this method should return {@code false}.
    *
    * @return {@code true} if an attempt should be made to re-evaluate instances of implementing classes when
    * backtracking, {@code false} otherwise
    */
   boolean isRetryable();
}
