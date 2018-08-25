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
package org.projog.core.function.db;

import static org.projog.core.term.TermUtils.createAnonymousVariable;

import java.util.Arrays;

import org.projog.core.PredicateKey;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/** Represents a record stored in a {@code RecordedDatabase}. */
class Record {
   private final PredicateKey key;
   private final IntegerNumber reference;
   private final Term value;

   Record(PredicateKey key, IntegerNumber reference, Term value) {
      this.key = key;
      this.reference = reference;
      this.value = value;
   }

   Term getKey() {
      String name = key.getName();
      int numArgs = key.getNumArgs();
      if (numArgs == 0) {
         return new Atom(name);
      } else {
         Term[] args = new Term[numArgs];
         Arrays.fill(args, createAnonymousVariable());
         return Structure.createStructure(name, args);
      }
   }

   IntegerNumber getReference() {
      return reference;
   }

   Term getValue() {
      return value;
   }
}
