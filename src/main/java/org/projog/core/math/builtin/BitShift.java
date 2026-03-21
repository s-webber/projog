/*
 * Copyright 2018 S. Webber
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
package org.projog.core.math.builtin;

import java.math.BigInteger;

import org.projog.core.math.AbstractBinaryIntegerArithmeticOperator;

/* TEST
%?- X is 13 << 0
% X=13

%?- X is 13 << 1
% X=26

%?- X is 13 << 2
% X=52

%?- X is 13 << 3
% X=104

%?- X is 13 << 4
% X=208

%?- X is 13 >> 0
% X=13

%?- X is 13 >> 1
% X=6

%?- X is 13 >> 2
% X=3

%?- X is 13 >> 3
% X=1

%?- X is 13 >> 4
% X=0

%?- X is 13 << -1
% X=6

%?- X is 13 >> -1
% X=26
*/
/**
 * <code>&lt;&lt;</code> / <code>&gt;&gt;</code> - shift bits left or right.
 */
public final class BitShift extends AbstractBinaryIntegerArithmeticOperator {
   private final boolean shiftLeft;

   public static BitShift shiftLeft() {
      return new BitShift(true);
   }

   public static BitShift shiftRight() {
      return new BitShift(false);
   }

   private BitShift(boolean shiftLeft) {
      this.shiftLeft = shiftLeft;
   }

   @Override
   protected long calculateLong(long base, long exponent) {
      BigInteger baseBigInteger = BigInteger.valueOf(base);
      int exponentInt = Math.toIntExact(exponent);
      if (exponentInt >= 0 == shiftLeft) {
         return baseBigInteger.shiftLeft(exponentInt).longValueExact();
      } else {
         return baseBigInteger.shiftRight(exponentInt).longValueExact();
      }
   }
}
