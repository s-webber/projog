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
package org.projog.core.udp;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/**
 * Represents a user defined predicate that always successfully evaluates exactly once.
 * <p>
 * e.g.: {@code p(_).}
 * </p>
 * <p>
 * Note: Similar to {@link org.projog.core.function.bool.True} - but implemented as a separate class rather than reused
 * as {@code True} only works with no arguments but {@code SingleRuleAlwaysTruePredicate} works with any number of
 * arguments.
 */
public final class SingleRuleAlwaysTruePredicate implements PredicateFactory {
   public final static SingleRuleAlwaysTruePredicate SINGLETON = new SingleRuleAlwaysTruePredicate();

   /** @see #SINGLETON */
   private SingleRuleAlwaysTruePredicate() {
   }

   @Override
   public Predicate getPredicate(Term... args) {
      return AbstractSingletonPredicate.toPredicate(true);
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }

   @Override
   public boolean isRetryable() {
      return false;
   }
}
