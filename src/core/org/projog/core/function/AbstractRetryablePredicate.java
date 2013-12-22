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
 * Superclass of "plug-in" predicates that are re-evaluated as part of backtracking.
 * <p>
 * Provides a skeletal implementation of {@link PredicateFactory} and {@link Predicate}. As {@link #isRetryable()}
 * always returns {@code true} {@link Predicate#evaluate(org.projog.core.term.Term...)} may be invoked on a
 * {@code code AbstractRetryablePredicate} multiple times for the same query. If a {@code AbstractRetryablePredicate}
 * need to preserve state between calls to {@link Predicate#evaluate(org.projog.core.term.Term...)} then it's
 * implementation of {@link PredicateFactory#getPredicate(org.projog.core.term.Term...)} should return a new instance
 * each time.
 * 
 * @see AbstractSingletonPredicate
 * @see org.projog.core.Predicate#evaluate(Term[])
 * @see org.projog.core.PredicateFactory#getPredicate(Term[])
 * @see org.projog.core.PluginPredicateFactoryFactory
 */
public abstract class AbstractRetryablePredicate implements Predicate, PredicateFactory {
   private KnowledgeBase knowledgeBase;

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