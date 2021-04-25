/*
 * Copyright 2021 S. Webber
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
package org.projog.core.predicate.builtin.flow;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.core.predicate.CutException;
import org.projog.core.predicate.Predicate;

public class CutTest {
   @Test
   public void testIsAlwaysCutOnBacktrack() {
      Cut c = new Cut();
      assertTrue(c.isAlwaysCutOnBacktrack());
   }

   @Test
   public void testIsRetryable() {
      Cut c = new Cut();
      assertTrue(c.isRetryable());
   }

   @Test
   public void testGetPredicate() {
      Cut c = new Cut();

      Predicate p1 = c.getPredicate();
      assertTrue(p1.evaluate());
      assertThrows(CutException.class, () -> p1.evaluate());

      Predicate p2 = c.getPredicate();
      assertTrue(p2.evaluate());
      assertThrows(CutException.class, () -> p2.evaluate());
   }
}
