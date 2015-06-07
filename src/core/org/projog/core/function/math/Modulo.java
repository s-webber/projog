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
package org.projog.core.function.math;


/* TEST
 %LINK prolog-arithmetic
 */
/**
 * <code>mod</code> - finds the remainder of division of one number by another.
 * <p>
 * The result has the same sign as the divisor (i.e. second argument).
 */
public final class Modulo extends AbstractTwoIntegerArgumentsCalculatable {
   @Override
   protected long calculateLong(long numerator, long divider) {
      final long modulo = numerator % divider;
      if (modulo == 0 || numerator * divider > 0) {
         return modulo;
      } else {
         return modulo + divider;
      }
   }
}
