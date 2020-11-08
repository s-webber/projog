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
package org.projog.core.parser;

import java.util.IdentityHashMap;
import java.util.Map;

import org.projog.core.term.Term;

/** Similar to a HashSet but uses reference-equality in place of object-equality when comparing keys. */
class TermIdentitySet {
   private final Map<Term, Term> values = new IdentityHashMap<>();

   void clear() {
      values.clear();
   }

   boolean contains(Term term) {
      return values.containsKey(term);
   }

   void add(Term term) {
      values.put(term, term);
   }
}
