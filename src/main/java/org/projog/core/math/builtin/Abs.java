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

import org.projog.core.math.AbstractUnaryArithmeticOperator;

/* TEST
%?- X is abs(-1)
% X=1

%?- X is abs(1)
% X=1

%?- X is abs(0)
% X=0

%?- X is abs(43.138)
% X=43.138

%?- X is abs(-832.24)
% X=832.24
 
%?- X is abs(9223372036854775807)
% X=9223372036854775807

%?- X is abs(-9223372036854775807)
% X=9223372036854775807

% Note: As this functionality is implemented using java.lang.Math.abs(), when called with an integer argument that is equal to the value of java.lang.Long.MIN_VALUE 
% (i.e. the most negative representable long value) the result is that same value, which is negative.
%?- X is abs(-9223372036854775808)
% X=-9223372036854775808
*/
/**
 * <code>abs</code> - returns the absolute value of a numeric argument.
 */
public final class Abs extends AbstractUnaryArithmeticOperator {
   @Override
   protected double calculateDouble(double n) {
      return Math.abs(n);
   }

   @Override
   protected long calculateLong(long n) {
      return Math.abs(n);
   }
}
