/*
 * Copyright 2020 S. Webber
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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projog.core.predicate.udp.KeyFactories.KeyFactory;
import org.projog.core.term.Term;

/**
 * Supports term indexing of user defined predicates.
 * <p>
 * See: https://en.wikipedia.org/wiki/Prolog#Term_indexing
 */
final class Indexes {
   /**
    * Maximum number of arguments of a clause that will be considered indexable.
    * <p>
    * Note that this is not the same as the maximum number of arguments that can be included in a single index.
    */
   private static final int MAX_INDEXABLE_ARGS = 9;

   private final ClauseAction[] masterData;
   private final Object lock = new Object();
   private final SoftReference<Index>[] indexes;
   private final int[] indexableArgs;
   private final int numIndexableArgs;

   @SuppressWarnings("unchecked")
   Indexes(Clauses clauses) {
      this.indexableArgs = clauses.getImmutableColumns();
      this.masterData = clauses.getClauseActions();
      this.numIndexableArgs = Math.min(indexableArgs.length, MAX_INDEXABLE_ARGS);
      if (numIndexableArgs == 0) {
         throw new IllegalArgumentException();
      }
      int size = 0;
      for (int i = 0, b = 1; i < numIndexableArgs; i++, b *= 2) {
         size += b;
      }
      indexes = new SoftReference[size + 1];
   }

   ClauseAction[] index(Term[] args) { // TODO rename
      int bitmask = createBitmask(args);

      if (bitmask == 0) {
         return masterData;
      } else {
         return getOrCreateIndex(bitmask).getMatches(args);
      }
   }

   int getClauseCount() {
      return masterData.length;
   }

   private int createBitmask(Term[] args) {
      int bitmask = 0;
      for (int i = 0, b = 1, bitCount = 0; i < numIndexableArgs; i++, b *= 2) {
         if (args[indexableArgs[i]].isImmutable()) {
            bitmask += b;
            if (++bitCount == KeyFactories.MAX_ARGUMENTS_PER_INDEX) {
               return bitmask;
            }
         }
      }
      return bitmask;
   }

   public Index getOrCreateIndex(int bitmask) {
      SoftReference<Index> ref = indexes[bitmask];
      Index index = ref != null ? ref.get() : null;

      if (index == null) {
         synchronized (lock) {
            while (index == null) {
               ref = indexes[bitmask];
               index = ref != null ? ref.get() : null;

               if (index == null) {
                  index = createIndex(bitmask);
                  indexes[bitmask] = new SoftReference<>(index);
               }
            }
         }
      }

      return index;
   }

   private Index createIndex(int bitmask) {
      int[] positions = createPositionsFromBitmask(bitmask);

      Map<Object, List<ClauseAction>> map = groupDataByPositions(positions);

      return new Index(positions, convertListsToArrays(map));
   }

   private int[] createPositionsFromBitmask(int bitmask) {
      int bitCount = Integer.bitCount(bitmask);
      int[] positions = new int[bitCount];
      for (int b = 1, ctr = 0, idx = 0; idx < bitCount; b *= 2, ctr++) {
         if ((b & bitmask) == b) {
            positions[idx++] = indexableArgs[ctr];
         }
      }
      return positions;
   }

   private Map<Object, List<ClauseAction>> groupDataByPositions(int[] positions) {
      Map<Object, List<ClauseAction>> map = new HashMap<>();
      KeyFactory keyFactory = KeyFactories.getKeyFactory(positions.length);
      for (ClauseAction clause : masterData) {
         Object key = keyFactory.createKey(positions, clause.getModel().getConsequent().getArgs());
         List<ClauseAction> list = map.get(key);
         if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
         }
         list.add(clause);
      }
      return map;
   }

   private Map<Object, ClauseAction[]> convertListsToArrays(Map<Object, List<ClauseAction>> map) {
      Map<Object, ClauseAction[]> result = new HashMap<>(map.size());
      for (Map.Entry<Object, List<ClauseAction>> e : map.entrySet()) {
         result.put(e.getKey(), e.getValue().toArray(new ClauseAction[e.getValue().size()]));
      }
      return result;
   }

   // only used by tests
   int countReferences() {
      int ctr = 0;
      for (SoftReference<Index> index : indexes) {
         if (index != null) {
            ctr++;
         }
      }
      return ctr;
   }

   // only used by tests
   int countClearedReferences() {
      int ctr = 0;
      for (SoftReference<Index> index : indexes) {
         if (index != null && index.get() == null) {
            ctr++;
         }
      }
      return ctr;
   }
}
