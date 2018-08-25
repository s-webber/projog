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
 %QUERY X is abs(-1)
 %ANSWER X=1
 
 %QUERY X is abs(1)
 %ANSWER X=1
 
 %QUERY X is abs(0)
 %ANSWER X=0

 %QUERY X is abs(43.138)
 %ANSWER X=43.138
 
 %QUERY X is abs(-832.24)
 %ANSWER X=832.24
  
 %QUERY X is abs(9223372036854775807)
 %ANSWER X=9223372036854775807

 %QUERY X is abs(-9223372036854775807)
 %ANSWER X=9223372036854775807
 
 % Note: As this functionality is implemented using java.lang.Math.abs(), when called with an integer argument that is equal to the value of java.lang.Long.MIN_VALUE 
 % (i.e. the most negative representable long value) the result is that same value, which is negative.
 %QUERY X is abs(-9223372036854775808)
 %ANSWER X=-9223372036854775808
 */
/**
 * <code>abs</code> - returns the absolute value of a numeric argument.
 */
public final class Abs extends AbstractOneArgumentCalculatable {
   @Override
   protected double calculateDouble(double n) {
      return Math.abs(n);
   }

   @Override
   protected long calculateLong(long n) {
      return Math.abs(n);
   }
}
