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
package org.projog.core.function.math;

/* TEST
 %QUERY X is 3 xor 3
 %ANSWER X=0

 %QUERY X is 3 xor 7
 %ANSWER X=4

 %QUERY X is 3 xor 6
 %ANSWER X=5

 %QUERY X is 3 xor 8
 %ANSWER X=11

 %QUERY X is 43 xor 27
 %ANSWER X=48

 %QUERY X is 27 xor 43
 %ANSWER X=48

 %QUERY X is 43 xor 0
 %ANSWER X=43

 %QUERY X is 0 xor 0
 %ANSWER X=0
 */
/**
 * <code>xor</code> - bitwise 'exclusive or'.
 */
public final class BitwiseXor extends AbstractBinaryIntegerArithmeticOperator {
   @Override
   protected long calculateLong(long n1, long n2) {
      return n1 ^ n2;
   }
}
