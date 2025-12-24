/*
 * Copyright 2025 S. Webber
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.createClauseModel;
import static org.projog.TestUtils.write;

import java.util.Arrays;

import org.junit.Test;

public class ImplicationsIteratorTest {
   @Test
   public void test() {
      ClauseModel cm1 = createClauseModel("test(X) :- write(1).");
      ClauseModel cm2 = createClauseModel("test(X) :- write(2).");
      ClauseModel cm3 = createClauseModel("test(X) :- write(3).");

      ImplicationsIterator iterator = new ImplicationsIterator(Arrays.asList(cm1, cm2, cm3));

      assertThrows(UnsupportedOperationException.class, () -> iterator.remove());

      assertTrue(iterator.hasNext());
      ClauseModel next = iterator.next();
      assertNotSame(cm1, next);
      assertNotSame(cm1.getOriginal(), next.getOriginal());
      assertEquals("test(X) :- write(1)", write(next.getOriginal()));

      assertTrue(iterator.hasNext());
      next = iterator.next();
      assertNotSame(cm2, next);
      assertNotSame(cm2.getOriginal(), next.getOriginal());
      assertEquals("test(X) :- write(2)", write(next.getOriginal()));

      assertTrue(iterator.hasNext());
      next = iterator.next();
      assertNotSame(cm3, next);
      assertNotSame(cm3.getOriginal(), next.getOriginal());
      assertEquals("test(X) :- write(3)", write(next.getOriginal()));

      assertFalse(iterator.hasNext());
   }
}
