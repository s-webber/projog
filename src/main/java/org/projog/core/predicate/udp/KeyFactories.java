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

import org.projog.core.term.Term;

final class KeyFactories {
   private static final KeyFactory[] FACTORIES = {
               null,
               new KeyFactory1(),
               new KeyFactory2(),
               new KeyFactory3(),
   };

   static final int MAX_ARGUMENTS_PER_INDEX = FACTORIES.length - 1;

   static KeyFactory getKeyFactory(int numArgs) {
      return FACTORIES[numArgs];
   }

   interface KeyFactory {
      Object createKey(int[] positions, Term[] args);
   }

   private static final class KeyFactory1 implements KeyFactory {
      @Override
      public Object createKey(int[] positions, Term[] args) {
         // if only one indexable term than rely on its hashCode and equals to be the key
         return args[positions[0]];
      }
   }

   private static final class KeyFactory2 implements KeyFactory {
      @Override
      public Key2 createKey(int[] positions, Term[] args) {
         return new Key2(args[positions[0]], args[positions[1]]);
      }
   }

   private static final class Key2 {
      final Term t1;
      final Term t2;
      final int hashCode;

      Key2(Term t1, Term t2) {
         this.t1 = t1;
         this.t2 = t2;
         this.hashCode = t1.hashCode() + (7 * t2.hashCode());
      }

      @Override
      public int hashCode() {
         return hashCode;
      }

      @Override
      public boolean equals(Object o) {
         Key2 k = (Key2) o;
         return t1.equals(k.t1) && t2.equals(k.t2);
      }
   }

   private static final class KeyFactory3 implements KeyFactory {
      @Override
      public Key3 createKey(int[] positions, Term[] args) {
         return new Key3(args[positions[0]], args[positions[1]], args[positions[2]]);
      }
   }

   private static final class Key3 {
      final Term t1;
      final Term t2;
      final Term t3;
      final int hashCode;

      Key3(Term t1, Term t2, Term t3) {
         this.t1 = t1;
         this.t2 = t2;
         this.t3 = t3;
         this.hashCode = t1.hashCode() + (7 * t2.hashCode()) + (11 * t3.hashCode());
      }

      @Override
      public int hashCode() {
         return hashCode;
      }

      @Override
      public boolean equals(Object o) {
         Key3 k = (Key3) o;
         return t1.equals(k.t1) && t2.equals(k.t2) && t3.equals(k.t3);
      }
   }
}
