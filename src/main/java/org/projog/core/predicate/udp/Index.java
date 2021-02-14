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

import java.util.Map;

import org.projog.core.predicate.udp.KeyFactories.KeyFactory;
import org.projog.core.term.Term;

final class Index {
   private static final ClauseAction[] NO_MATCHES = new ClauseAction[0];

   private final int[] positions;
   private final Map<Object, ClauseAction[]> result;
   private final KeyFactory keyFactory;

   Index(int[] positions, Map<Object, ClauseAction[]> result) {
      this.keyFactory = KeyFactories.getKeyFactory(positions.length);
      this.positions = positions;
      this.result = result;
   }

   ClauseAction[] getMatches(Term[] args) {
      Object key = keyFactory.createKey(positions, args);
      return result.getOrDefault(key, NO_MATCHES);
   }

   int getKeyCount() {
      return result.size();
   }
}
