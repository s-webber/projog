/*
 * Copyright 2013 S Webber
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
 * @see org.projog.core.Predicate#evaluate(Term[])
 * @see org.projog.core.PluginPredicateFactoryFactory
 */
public abstract class AbstractSingletonPredicate implements Predicate, PredicateFactory {
   private KnowledgeBase knowledgeBase;

   @Override
   public final Predicate getPredicate(Term... args) {
      return this;
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
      return false;
   }

   @Override
   public final boolean couldReEvaluationSucceed() {
      return false;
   }
}