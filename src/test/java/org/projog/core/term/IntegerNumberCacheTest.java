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
package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class IntegerNumberCacheTest {
   private static final int MIN_CACHED_VALUE = -128;
   private static final int MAX_CACHED_VALUE = 127;

   @Test
   public void testZero() {
      assertEquals(new IntegerNumber(0), IntegerNumberCache.ZERO);
      assertSame(IntegerNumberCache.valueOf(0), IntegerNumberCache.ZERO);
   }

   @Test
   public void testCached() {
      for (int i = MIN_CACHED_VALUE; i <= MAX_CACHED_VALUE; i++) {
         assertSame(IntegerNumberCache.valueOf(i), IntegerNumberCache.valueOf(i));
         assertEquals(new IntegerNumber(i), IntegerNumberCache.valueOf(i));
      }
   }

   @Test
   public void testOutsideCache_too_low() {
      long value = MIN_CACHED_VALUE - 1;
      assertNotSame(IntegerNumberCache.valueOf(value), IntegerNumberCache.valueOf(value));
      assertEquals(new IntegerNumber(value), IntegerNumberCache.valueOf(value));
   }

   @Test
   public void testOutsideCache_too_high() {
      long value = MAX_CACHED_VALUE + 1;
      assertNotSame(IntegerNumberCache.valueOf(value), IntegerNumberCache.valueOf(value));
      assertEquals(new IntegerNumber(value), IntegerNumberCache.valueOf(value));
   }

   @Test
   public void testMinimumLongValue() {
      assertNotSame(IntegerNumberCache.valueOf(Long.MIN_VALUE), IntegerNumberCache.valueOf(Long.MIN_VALUE));
      assertEquals(new IntegerNumber(Long.MIN_VALUE), IntegerNumberCache.valueOf(Long.MIN_VALUE));
   }

   @Test
   public void testMaximumLongValue() {
      assertNotSame(IntegerNumberCache.valueOf(Long.MAX_VALUE), IntegerNumberCache.valueOf(Long.MAX_VALUE));
      assertEquals(new IntegerNumber(Long.MAX_VALUE), IntegerNumberCache.valueOf(Long.MAX_VALUE));
   }
}
