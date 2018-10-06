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
 * Superclass of "plug-in" predicates that are not re-evaluated as part of backtracking.
 * <p>
 * Provides a skeletal implementation of {@link PredicateFactory} and {@link Predicate}. No attempt to find multiple
 * solutions will be made as part of backtracking as {@link #isRetryable()} always returns {@code false} - meaning
 * {@link Predicate#evaluate(org.projog.core.term.Term...)} will never be invoked twice on a
 * {@code AbstractSingletonPredicate} for the same query. As they do not need to preserve state between calls to
 * {@link Predicate#evaluate(org.projog.core.term.Term...)} {@code AbstractSingletonPredicate}s are state-less. As
 * {@code AbstractSingletonPredicate}s are state-less the same instance can be reused for the evaluation of all queries
 * of the predicate it represents. This is implemented by
 * {@link PredicateFactory#getPredicate(org.projog.core.term.Term...)} always returning {@code this}.
 * <p>
 *
 * @see AbstractRetryablePredicate
 * @see Predicate#evaluate(Term[])
 */
public abstract class AbstractSingletonPredicate implements PredicateFactory {
   public static final Predicate TRUE = new Predicate() { // TODO
      @Override
      public boolean evaluate() {
         return true;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return false;
      }
   };
   public static final Predicate FAIL = new Predicate() { // TODO
      @Override
      public boolean evaluate() {
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return false;
      }
   };

   private KnowledgeBase knowledgeBase;

   @Override
   public final Predicate getPredicate(Term... args) {
      boolean result = evaluate(args);
      return toPredicate(result);
   }

   public static Predicate toPredicate(boolean result) { // TODO
      return result ? TRUE : FAIL;
   }

   protected boolean evaluate(Term... args) { // TODO make private
      switch (args.length) {
         case 0:
            return evaluate();
         case 1:
            return evaluate(args[0]);
         case 2:
            return evaluate(args[0], args[1]);
         case 3:
            return evaluate(args[0], args[1], args[2]);
         default:
            throw createWrongNumberOfArgumentsException(args.length);
      }
   }

   protected boolean evaluate() {
      throw createWrongNumberOfArgumentsException(0);
   }

   protected boolean evaluate(Term arg) {
      throw createWrongNumberOfArgumentsException(1);
   }

   protected boolean evaluate(Term arg1, Term arg2) {
      throw createWrongNumberOfArgumentsException(2);
   }

   protected boolean evaluate(Term arg1, Term arg2, Term arg3) {
      throw createWrongNumberOfArgumentsException(3);
   }

   private IllegalArgumentException createWrongNumberOfArgumentsException(int numberOfArguments) {
      throw new IllegalArgumentException("The predicate factory: " + getClass().getName() + " does next accept the number of arguments: " + numberOfArguments);
   }

   @Override
   public boolean isRetryable() {
      return false;
   }

   @Override
   public final void setKnowledgeBase(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
      init();
   }

   /**
    * This method is called by {@link #setKnowledgeBase(KnowledgeBase)}.
    * <p>
    * Can be overridden by subclasses to perform initialisation before any calls to {@link #evaluate(Term...)} are made.
    * As {@link #setKnowledgeBase(KnowledgeBase)} will have already been called before this method is invoked,
    * overridden versions will be able to access the {@code KnowledgeBase} using {@link #getKnowledgeBase()}.
    */
   protected void init() { // TODO remove
   }

   protected final KnowledgeBase getKnowledgeBase() {
      return knowledgeBase;
   }
}
