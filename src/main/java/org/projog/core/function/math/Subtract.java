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
 * <code>-</code> - performs subtraction.
 */
public final class Subtract extends AbstractTwoArgumentsCalculatable {
   /** Returns the difference of the two arguments */
   @Override
   protected double calculateDouble(double n1, double n2) {
      return n1 - n2;
   }

   /** Returns the difference of the two arguments */
   @Override
   protected long calculateLong(long n1, long n2) {
      return n1 - n2;
   }
}
