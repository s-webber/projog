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

public final class IntegerNumberCache {
   public static final IntegerNumber ZERO = new IntegerNumber(0);
   private static final int MIN_CACHED_VALUE = -128;
   private static final int MAX_CACHED_VALUE = 127;
   private static final int OFFSET = -MIN_CACHED_VALUE;

   static final IntegerNumber CACHE[] = new IntegerNumber[OFFSET + MAX_CACHED_VALUE + 1];

   static {
      for (int i = 0; i < CACHE.length; i++) {
         int n = i - OFFSET;
         CACHE[i] = n == 0 ? ZERO : new IntegerNumber(n);
      }
   }

   private IntegerNumberCache() {
   }

   public static IntegerNumber valueOf(long l) {
      if (l >= MIN_CACHED_VALUE && l <= MAX_CACHED_VALUE) {
         return CACHE[(int) l + OFFSET];
      } else {
         return new IntegerNumber(l);
      }
   }
}
