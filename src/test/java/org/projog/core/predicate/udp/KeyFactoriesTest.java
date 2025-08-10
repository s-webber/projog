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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.structure;

import org.junit.Test;
import org.projog.core.predicate.udp.KeyFactories.KeyFactory;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class KeyFactoriesTest {
   private static final Atom A = atom("a");
   private static final Atom B = atom("b");
   private static final Atom C = atom("c");
   private static final Atom D = atom("d");
   private static final Atom E = atom("e");

   @Test
   public void testLimit() {
      assertEquals(3, KeyFactories.MAX_ARGUMENTS_PER_INDEX);
   }

   @Test
   public void testOne() {
      Term[] args = new Term[] {A, B, C};
      KeyFactory kf = KeyFactories.getKeyFactory(1);

      assertSame(A, kf.createKey(new int[] {0}, structure("p", args)));
      assertSame(B, kf.createKey(new int[] {1}, structure("p", args)));
      assertSame(C, kf.createKey(new int[] {2}, structure("p", args)));
   }

   @Test
   public void testTwo() {
      Term[] args = new Term[] {A, B, C};
      KeyFactory kf = KeyFactories.getKeyFactory(2);

      Object k = kf.createKey(new int[] {0, 1}, structure("p", args));

      assertNotEqualsHashCode(k, kf.createKey(new int[] {0, 2}, structure("p", args)));
      assertNotEqualsHashCode(k, kf.createKey(new int[] {1, 2}, structure("p", args)));
      assertNotEqualsHashCode(k, kf.createKey(new int[] {0, 1}, structure("p", new Term[] {B, A, C})));

      assertEqualsHashCode(k, kf.createKey(new int[] {0, 1}, structure("p", args)));
      assertEqualsHashCode(k, kf.createKey(new int[] {0, 1}, structure("p", new Term[] {A, B, D})));
   }

   @Test
   public void testThree() {
      Term[] args = new Term[] {A, B, C, D};
      KeyFactory kf = KeyFactories.getKeyFactory(3);

      Object k = kf.createKey(new int[] {0, 1, 2}, structure("p", args));

      assertNotEqualsHashCode(k, kf.createKey(new int[] {0, 2, 3}, structure("p", args)));
      assertNotEqualsHashCode(k, kf.createKey(new int[] {1, 2, 3}, structure("p", args)));
      assertNotEqualsHashCode(k, kf.createKey(new int[] {0, 1, 2}, structure("p", new Term[] {A, C, B, D})));

      assertEqualsHashCode(k, kf.createKey(new int[] {0, 1, 2}, structure("p", args)));
      assertEqualsHashCode(k, kf.createKey(new int[] {0, 1, 2}, structure("p", new Term[] {A, B, C, E})));
   }

   private void assertNotEqualsHashCode(Object o1, Object o2) { // TODO move to TestUtils
      assertFalse(o1.equals(o2));
      assertFalse(o2.equals(o1));
      assertNotEquals(o1.hashCode(), o2.hashCode());
   }

   private void assertEqualsHashCode(Object o1, Object o2) { // TODO move to TestUtils
      assertTrue(o1.equals(o2));
      assertTrue(o2.equals(o1));
      assertEquals(o1.hashCode(), o2.hashCode());
   }
}
