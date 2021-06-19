/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.udp;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.projog.core.event.SpyPoints;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

/**
 * Maintains a record of the clauses that represents a "dynamic" user defined predicate.
 * <p>
 * A "dynamic" user defined predicate is one that can have clauses added and removed <i>after</i> it has been first
 * defined. This is normally done using the {@code asserta/1}, {@code assertz/1} and {@code retract/1} predicates.
 *
 * @see org.projog.core.predicate.udp.InterpretedUserDefinedPredicate
 */
public final class DynamicUserDefinedPredicateFactory implements UserDefinedPredicateFactory {
   // use array rather than two instances so that references not lost between
   // copies when heads or tails alter
   private static final int FIRST = 0;
   private static final int LAST = 1;

   private final Object LOCK = new Object();
   private final KnowledgeBase kb;
   private final SpyPoints.SpyPoint spyPoint;
   private final ClauseActionMetaData[] ends = new ClauseActionMetaData[2];
   private ConcurrentHashMap<Term, ClauseActionMetaData> index;
   private boolean hasPrimaryKey;

   public DynamicUserDefinedPredicateFactory(KnowledgeBase kb, PredicateKey predicateKey) {
      this.kb = kb;
      if (predicateKey.getNumArgs() == 0) {
         this.hasPrimaryKey = false;
         this.index = null;
      } else {
         this.hasPrimaryKey = true;
         index = new ConcurrentHashMap<>();
      }
      this.spyPoint = kb.getSpyPoints().getSpyPoint(predicateKey);
   }

   @Override
   public Predicate getPredicate(Term[] args) {
      if (hasPrimaryKey) {
         Term firstArg = args[0];
         if (firstArg.isImmutable()) {
            ClauseActionMetaData match = index.get(firstArg);
            if (match == null) {
               return PredicateUtils.createFailurePredicate(spyPoint, args);
            } else {
               return PredicateUtils.createSingleClausePredicate(match.clause, spyPoint, args);
            }
         }
      }

      ClauseActionIterator itr = new ClauseActionIterator(ends[FIRST]);
      return new InterpretedUserDefinedPredicate(itr, spyPoint, args);
   }

   @Override
   public PredicateKey getPredicateKey() {
      return spyPoint.getPredicateKey();
   }

   @Override
   public boolean isDynamic() {
      return true;
   }

   /**
    * Returns an iterator over the clauses of this user defined predicate.
    * <p>
    * The iterator returned will have the following characteristics:
    * <ul>
    * <li>Calls to {@link java.util.Iterator#next()} return a <i>new copy</i> of the {@link ClauseModel} to avoid the
    * original being altered.</li>
    * <li>Calls to {@link java.util.Iterator#remove()} <i>do</i> alter the underlying structure of this user defined
    * predicate.</li>
    * <li></li>
    * </ul>
    */
   @Override
   public Iterator<ClauseModel> getImplications() {
      return new ImplicationsIterator();
   }

   @Override
   public void addFirst(ClauseModel clauseModel) {
      synchronized (LOCK) {
         ClauseActionMetaData newClause = createClauseActionMetaData(clauseModel);
         addToIndex(clauseModel, newClause);

         // if first used in a implication antecedent before being used as a consequent,
         // it will originally been created with first and last both null
         ClauseActionMetaData first = ends[FIRST];
         if (first == null) {
            ends[FIRST] = newClause;
            ends[LAST] = newClause;
            return;
         }
         newClause.next = first;
         first.previous = newClause;
         ends[FIRST] = newClause;
      }
   }

   @Override
   public void addLast(ClauseModel clauseModel) {
      synchronized (LOCK) {
         ClauseActionMetaData newClause = createClauseActionMetaData(clauseModel);
         addToIndex(clauseModel, newClause);

         // if first used in a implication antecedent before being used as a consequent,
         // it will originally been created with first and last both null
         ClauseActionMetaData last = ends[LAST];
         if (last == null) {
            ends[FIRST] = newClause;
            ends[LAST] = newClause;
            return;
         }
         last.next = newClause;
         newClause.previous = last;
         ends[LAST] = newClause;
      }
   }

   private void addToIndex(ClauseModel clauseModel, ClauseActionMetaData metaData) {
      if (hasPrimaryKey) {
         Term firstArg = clauseModel.getConsequent().getArgument(0);
         if (!firstArg.isImmutable() || index.put(firstArg, metaData) != null) {
            hasPrimaryKey = false;
            index.clear();
         }
      }
   }

   @Override
   public ClauseModel getClauseModel(int index) {
      ClauseActionMetaData next = ends[FIRST];
      for (int i = 0; i < index; i++) {
         if (next == null) {
            return null;
         }
         next = next.next;
      }
      if (next == null) {
         return null;
      }
      return next.clause.getModel().copy();
   }

   private ClauseActionMetaData createClauseActionMetaData(ClauseModel clauseModel) {
      return new ClauseActionMetaData(kb, clauseModel);
   }

   private static class ClauseActionIterator implements Iterator<ClauseAction> {
      private ClauseActionMetaData next;

      ClauseActionIterator(ClauseActionMetaData first) {
         next = first;
      }

      @Override
      public boolean hasNext() {
         return next != null;
      }

      /** need to call getFree on result */
      @Override
      public ClauseAction next() {
         ClauseAction c = next.clause;
         next = next.next;
         return c;
      }
   }

   private class ImplicationsIterator implements Iterator<ClauseModel> {
      private ClauseActionMetaData previous;

      @Override
      public boolean hasNext() {
         return getNext() != null;
      }

      /**
       * Returns a <i>new copy</i> to avoid the original being altered.
       */
      @Override
      public ClauseModel next() {
         ClauseActionMetaData next = getNext();
         ClauseModel clauseModel = next.clause.getModel();
         previous = next;
         return clauseModel.copy();
      }

      private ClauseActionMetaData getNext() {
         return previous == null ? ends[FIRST] : previous.next;
      }

      @Override
      public void remove() { // TODO find way to use index when retracting
         synchronized (LOCK) {
            if (hasPrimaryKey) {
               Term firstArg = previous.clause.getModel().getConsequent().getArgument(0);
               if (index.remove(firstArg) == null) {
                  throw new IllegalStateException();
               }
            }
            if (previous.previous != null) {
               previous.previous.next = previous.next;
            } else {
               ClauseActionMetaData newHead = previous.next;
               if (newHead != null) {
                  newHead.previous = null;
               }
               ends[FIRST] = newHead;
            }
            if (previous.next != null) {
               previous.next.previous = previous.previous;
            } else {
               ClauseActionMetaData newTail = previous.previous;
               if (newTail != null) {
                  newTail.next = null;
               }
               ends[LAST] = newTail;
            }
            if (ends[FIRST] == null && ends[LAST] == null) {
               hasPrimaryKey = index != null;
            }
         }
      }
   }

   private static class ClauseActionMetaData {
      final ClauseAction clause;
      ClauseActionMetaData previous;
      ClauseActionMetaData next;

      ClauseActionMetaData(KnowledgeBase kb, ClauseModel clauseModel) {
         this.clause = ClauseActionFactory.createClauseAction(kb, clauseModel);
      }
   }

   @Override
   public boolean isRetryable() {
      return true;
   }
}
