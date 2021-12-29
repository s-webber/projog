/*
 * Copyright 2013 S. Webber
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

import org.projog.core.math.AbstractBinaryIntegerArithmeticOperator;

/* TEST
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

% Note bit shifting using a negative value gives different results than in some other Prolog implementations.
%?- X is 13 >> -1
% X=0
*/
/**
 * <code>&gt;&gt;</code> - right shift bits.
 */
public final class ShiftRight extends AbstractBinaryIntegerArithmeticOperator {
   @Override
   protected long calculateLong(long n1, long n2) {
      return n1 >> n2;
   }
}
