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

import java.util.Iterator;

import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;

/**
 * Maintains a record of the clauses that define a user defined predicate.
 * <p>
 * A user defined predicate is a predicate that is constructed from Prolog syntax consulted at runtime.
 * <p>
 * <img src="doc-files/UserDefinedPredicateFactory.png">
 */
public interface UserDefinedPredicateFactory extends PredicateFactory {
   /**
    * Adds a clause to the beginning of the predicate's list of clauses.
    * 
    * @param clauseModel the clause to add to the beginning of the predicate
    */
   void addFirst(ClauseModel clauseModel);

   /**
    * Adds a clause to the end of the predicate's list of clauses.
    * 
    * @param clauseModel the clause to add to the end of the predicate
    */
   void addLast(ClauseModel clauseModel);

   /**
    * Returns the key for the predicate this object represents
    * 
    * @return the key for the predicate this object represents
    */
   PredicateKey getPredicateKey();

   /**
    * Returns an iterator over the clauses in the predicate in proper sequence.
    * 
    * @return an iterator over the clauses in the predicate in proper sequence.
    */
   Iterator<ClauseModel> getImplications();

   /**
    * Returns {@code true} is this predicate is dynamic.
    * <p>
    * A "dynamic" predicate is a user defined predicate that can have clauses added or removed after is first defined.
    * 
    * @return {@code true} is this predicate is dynamic
    */
   boolean isDynamic();

   /**
    * Returns the clause at the specified position in this predicate's list of clauses.
    * 
    * @param index index of the clause to return
    * @return the clause at the specified position in this predicate's list of clauses or {@code null} if out of bounds
    */
   public ClauseModel getClauseModel(int index);
}
