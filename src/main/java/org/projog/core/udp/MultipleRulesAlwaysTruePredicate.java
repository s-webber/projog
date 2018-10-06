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
import org.projog.core.term.Term;

/**
 * Represents a user defined predicate that has a number of rules - each of which always successfully evaluate.
 * <p>
 * e.g.: <pre>
 * p(_).
 * p(_).
 * p(_).
 * </pre>
 * </p>
 * <p>
 * Note: Similar to {@link org.projog.core.function.flow.RepeatSetAmount} - but implemented as a separate class rather
 * than reused as {@code RepeatSetAmount} only works with one argument but {@code MulitpleRulesAlwaysTruePredicate}
 * works with any number of arguments.
 * </p>
 */
public final class MultipleRulesAlwaysTruePredicate implements Predicate, PredicateFactory {
   private final int limit;
   private int ctr;

   public MultipleRulesAlwaysTruePredicate(int limit) {
      this.limit = limit;
   }

   @Override
   public boolean evaluate() {
      return ctr++ < limit;
   }

   @Override
   public MultipleRulesAlwaysTruePredicate getPredicate(Term... args) {
      return new MultipleRulesAlwaysTruePredicate(limit);
   }

   @Override
   public boolean couldReevaluationSucceed() {
      return ctr < limit;
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }
}
