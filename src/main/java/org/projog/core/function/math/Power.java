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
 %QUERY X is 2 ** 1
 %ANSWER X = 2

 %QUERY X is 2 ** 2
 %ANSWER X = 4

 %QUERY X is 2 ** 5
 %ANSWER X = 32

 %QUERY X is 5 ** 3
 %ANSWER X = 125

 %QUERY X is 5.0 ** 3
 %ANSWER X = 125.0

 %QUERY X is 5 ** 3.0
 %ANSWER X = 125.0

 %QUERY X is 5.0 ** 3.0
 %ANSWER X = 125.0

 %QUERY X is 2 + 5 ** 3 - 1
 %ANSWER X = 126

 %QUERY X is -2 ** 2
 %ANSWER X = 4

 % Note: in some Prolog implementations the result would be 0.25
 %QUERY X is -2 ** -2
 %ANSWER X = 0

 % Note: in some Prolog implementations the result would be 0.25
 %QUERY X is 2 ** -2
 %ANSWER X = 0

 %QUERY X is 0.5 ** 2
 %ANSWER X = 0.25
 */
/**
 * <code>**</code> - calculates the result of the first argument raised to the power of the second argument.
 */
public final class Power extends AbstractTwoArgumentsCalculatable {
   @Override
   protected double calculateDouble(double n1, double n2) {
      return Math.pow(n1, n2);
   }

   @Override
   protected long calculateLong(long n1, long n2) {
      return (long) Math.pow(n1, n2);
   }
}
