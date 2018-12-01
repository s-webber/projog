/*
 * Copyright 2018 S. Webber
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
package org.projog.core.udp.compiler.model;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateKey;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.TailRecursivePredicateMetaData;

/** Contains meta-data for a user-defined Prolog predicate. */
public final class PredicateMetaData {
   private final PredicateKey key;
   private final List<ClauseMetaData> clauses;
   private final TailRecursivePredicateMetaData recursiveFunctionMetaData;

   public PredicateMetaData(PredicateKey key, KnowledgeBase kb, List<ClauseModel> clauseModels) {
      this.key = key;
      this.clauses = new ArrayList<>(clauseModels.size());
      for (ClauseModel m : clauseModels) {
         ClauseMetaData md = new ClauseMetaData(m, kb, clauses.size());
         clauses.add(md);
      }
      this.recursiveFunctionMetaData = TailRecursivePredicateMetaData.create(kb, clauseModels);
   }

   public PredicateKey getPredicateKey() {
      return key;
   }

   public List<ClauseMetaData> getClauses() {
      return clauses;
   }

   public TailRecursivePredicateMetaData getTailRecursivePredicateMetaData() {
      return recursiveFunctionMetaData;
   }

   public boolean isTailRecursive() {
      return recursiveFunctionMetaData != null;
   }

   public boolean isRetryable() {
      for (ClauseMetaData cs : clauses) {
         if (cs.hasCutWhichStopsReevaluation()) {
            // continue
         } else if (cs.getClauseIdx() == clauses.size() - 1 && !cs.hasRetryableElements()) {
            // continue
         } else {
            return true;
         }
      }

      return false;
   }
}
