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
 * Represents all predicates that a {@code KnowledgeBase} has no definition of.
 * <p>
 * Always fails to evaluate successfully.
 * 
 * @see KnowledgeBase#getPredicateFactory(PredicateKey)
 * @see KnowledgeBase#getPredicateFactory(Term)
 */
public final class UnknownPredicate extends AbstractSingletonPredicate {
   /**
    * Singleton instance
    */
   public static final UnknownPredicate UNKNOWN_PREDICATE = new UnknownPredicate();

   /**
    * Private constructor to force use of {@link #UNKNOWN_PREDICATE}
    */
   private UnknownPredicate() {
      // do nothing
   }

   @Override
   public boolean evaluate(Term... args) {
      return false;
   }
}
