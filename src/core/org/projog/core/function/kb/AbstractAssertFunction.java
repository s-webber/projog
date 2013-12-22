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
package org.projog.core.function.kb;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/**
 * Extended by {@code Predicate}s that add new clauses to a dynamic user defined predicate.
 */
abstract class AbstractAssertFunction extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term consequent) {
      PredicateKey key = PredicateKey.createForTerm(consequent);
      KnowledgeBase kb = getKnowledgeBase();
      UserDefinedPredicateFactory userDefinedPredicate = kb.createOrReturnUserDefinedPredicate(key);
      ClauseModel clauseModel = ClauseModel.createClauseModel(consequent);
      add(userDefinedPredicate, clauseModel);
      return true;
   }

   /**
    * Adds the specified {@code ClauseModel} to the specified {@code UserDefinedPredicateFactory}.
    */
   protected abstract void add(UserDefinedPredicateFactory userDefinedPredicate, ClauseModel clauseModel);
}