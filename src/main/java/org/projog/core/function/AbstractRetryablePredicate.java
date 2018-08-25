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
package org.projog.core.function;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;

/**
 * Superclass of "plug-in" predicates that are re-evaluated as part of backtracking.
 * <p>
 * Provides a skeletal implementation of {@link PredicateFactory} and {@link Predicate}. As {@link #isRetryable()}
 * always returns {@code true} {@link Predicate#evaluate(org.projog.core.term.Term...)} may be invoked on a
 * {@code code AbstractRetryablePredicate} multiple times for the same query. If a {@code AbstractRetryablePredicate}
 * need to preserve state between calls to {@link Predicate#evaluate(org.projog.core.term.Term...)} then its
 * implementation of {@link PredicateFactory#getPredicate(org.projog.core.term.Term...)} should return a new instance
 * each time.
 * 
 * @see AbstractSingletonPredicate
 * @see org.projog.core.Predicate#evaluate(Term[])
 * @see org.projog.core.PredicateFactory#getPredicate(Term[])
 */
public abstract class AbstractRetryablePredicate extends AbstractPredicate implements PredicateFactory {
   private KnowledgeBase knowledgeBase;

   @Override
   public Predicate getPredicate(Term... args) {
      switch (args.length) {
         case 0:
            return getPredicate();
         case 1:
            return getPredicate(args[0]);
         case 2:
            return getPredicate(args[0], args[1]);
         case 3:
            return getPredicate(args[0], args[1], args[2]);
         default:
            throw createWrongNumberOfArgumentsException(args.length);
      }
   }

   protected Predicate getPredicate() {
      throw createWrongNumberOfArgumentsException(0);
   }

   protected Predicate getPredicate(Term arg) {
      throw createWrongNumberOfArgumentsException(1);
   }

   protected Predicate getPredicate(Term arg1, Term arg2) {
      throw createWrongNumberOfArgumentsException(2);
   }

   protected Predicate getPredicate(Term arg1, Term arg2, Term arg3) {
      throw createWrongNumberOfArgumentsException(3);
   }

   private IllegalArgumentException createWrongNumberOfArgumentsException(int numberOfArguments) {
      throw new IllegalArgumentException("The predicate factory: " + getClass() + " does next accept the number of arguments: " + numberOfArguments);
   }

   @Override
   public final void setKnowledgeBase(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
   }

   protected final KnowledgeBase getKnowledgeBase() {
      return knowledgeBase;
   }

   @Override
   public final boolean isRetryable() {
      return true;
   }

   @Override
   public boolean couldReEvaluationSucceed() {
      return true;
   }
}
