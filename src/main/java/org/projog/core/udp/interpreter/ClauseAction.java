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
package org.projog.core.udp.interpreter;

import org.projog.core.term.Term;

/**
 * Contains the logic for how a clause is evaluated.
 * <p>
 * Called {@code ClauseAction} to differentiate it from {@link org.projog.core.udp.ClauseModel}.
 * <p>
 * <img src="doc-files/ClauseAction.png">
 */
public interface ClauseAction {
   /**
    * Returns a version of this object that is safe to use for evaluation.
    * <p>
    * When code needs to use a ClauseAction it should call the getFree() method and use the copy (i.e. same class and
    * attributes) returned rather than the original. As calling evaluate(Term[]) on a ClauseAction can alter the objects
    * internal attributes it is necessary to always use a copy rather than the original.
    * 
    * @return a copy version of this object that is safe to use
    */
   public ClauseAction getFree();

   /**
    * Attempts to satisfy the clause this instance represents.
    * <p>
    * Calling this method multiple times on a single instance allows all possible answers to be identified. An attempt
    * to find a solution carries on from where the last successful call finished.
    * 
    * @param queryArgs the arguments to use in the evaluation of this clause
    * @return {@code true} if it was possible to satisfy the clause, {@code false} otherwise
    * @see #getFree()
    */
   public boolean evaluate(Term[] queryArgs);

   /**
    * Could the next re-evaluation of this instance succeed?
    * <p>
    * Should be called after the {@link #evaluate(Term[])} method of this instance has already been called and returned
    * {@code true} to determine whether it is worth calling {@link #evaluate(Term[])} again to find alternative
    * solutions.
    * <p>
    * Similar to {@link org.projog.core.Predicate#couldReevaluationSucceed()} but on a clause rather than predicate
    * basis.
    * 
    * @return {@code true} if an attempt to re-evaluate this instance could possible succeed, {@code false} otherwise
    */
   public boolean couldReevaluationSucceed();
}
