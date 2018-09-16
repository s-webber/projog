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
public final class UnknownPredicate implements PredicateFactory {
   private final KnowledgeBase kb;
   private final PredicateKey key;
   private PredicateFactory actualPredicateFactory;

   public UnknownPredicate(KnowledgeBase kb, PredicateKey key) {
      this.kb = kb;
      this.key = key;
   }

   @Override
   public Predicate getPredicate(Term... args) {
      if (actualPredicateFactory == null) {
         instantiatePredicateFactory();
      }

      if (actualPredicateFactory == null) {
         return AbstractSingletonPredicate.FAIL;
      } else {
         return actualPredicateFactory.getPredicate(args);
      }
   }

   private void instantiatePredicateFactory() {
      synchronized (key) {
         if (actualPredicateFactory == null) {
            PredicateFactory pf = kb.getPredicateFactory(key);
            if (!(pf instanceof UnknownPredicate)) {
               actualPredicateFactory = pf;
            }
         }
      }
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }
}
