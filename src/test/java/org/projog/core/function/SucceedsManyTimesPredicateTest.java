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
package org.projog.core.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class SucceedsManyTimesPredicateTest {
   @Test
   public void testCount() {
      assertEquals(2, new SucceedsManyTimesPredicate(2).getCount());
      assertEquals(3, new SucceedsManyTimesPredicate(3).getCount());
      assertEquals(Integer.MAX_VALUE, new SucceedsManyTimesPredicate(Integer.MAX_VALUE).getCount());
   }

   @Test
   public void testEvaluatesTwice() {
      SucceedsManyTimesPredicate succeedsTwice = new SucceedsManyTimesPredicate(2);
      assertTrue(succeedsTwice.couldReevaluationSucceed());
      assertTrue(succeedsTwice.evaluate());
      assertTrue(succeedsTwice.couldReevaluationSucceed());
      assertTrue(succeedsTwice.evaluate());
      assertFalse(succeedsTwice.couldReevaluationSucceed());
      assertFalse(succeedsTwice.evaluate());
   }

   @Test
   public void testEvaluatesSeven() {
      SucceedsManyTimesPredicate succeedsSeven = new SucceedsManyTimesPredicate(7);
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertTrue(succeedsSeven.couldReevaluationSucceed());
      assertTrue(succeedsSeven.evaluate());
      assertFalse(succeedsSeven.couldReevaluationSucceed());
      assertFalse(succeedsSeven.evaluate());
   }

   @Ignore
   @Test
   public void testEvaluatesMaximum() {
      SucceedsManyTimesPredicate succeedsMax = new SucceedsManyTimesPredicate(Integer.MAX_VALUE);
      for (int i = 0; i < Integer.MAX_VALUE; i++) {
         assertTrue(succeedsMax.couldReevaluationSucceed());
         assertTrue(succeedsMax.evaluate());
      }
      assertFalse(succeedsMax.couldReevaluationSucceed());
      assertFalse(succeedsMax.evaluate());
   }

   @Test
   public void testGetFree() {
      SucceedsManyTimesPredicate original = new SucceedsManyTimesPredicate(7);
      SucceedsFixedAmountPredicate copy = original.getFree();
      assertEquals(7, copy.getCount());
      assertNotSame(original, copy);
   }

   @Test
   public void testIncrement() {
      SucceedsManyTimesPredicate original = new SucceedsManyTimesPredicate(7);
      SucceedsManyTimesPredicate increment = original.increment();
      assertNotSame(increment, original.getFree());
      assertEquals(7, original.getCount());
      assertEquals(8, increment.getCount());
      assertEquals(9, increment.increment().getCount());
   }
}
