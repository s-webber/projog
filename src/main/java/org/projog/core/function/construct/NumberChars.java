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
package org.projog.core.function.construct;

/* TEST
 %QUERY number_chars(193457260,X)
 %ANSWER X =  [1,9,3,4,5,7,2,6,0]

 %QUERY number_chars(-193457260,X)
 %ANSWER X =  [-,1,9,3,4,5,7,2,6,0]

 %QUERY number_chars(X,['1','9','3','4','5','7','2','6','0'])
 %ANSWER X = 193457260

 %QUERY number_chars(X,['-', '1','9','3','4','5','7','2','6','0'])
 %ANSWER X = -193457260

 %QUERY number_chars(X,['o','n','e'])
 %ERROR Could not convert characters to an integer: 'one'

 %QUERY number_chars(X,1257)
 %ERROR As the first argument: X is a variable the second argument needs to be a list but was: 1257 of type: INTEGER

 %QUERY number_chars(X,['6','.','4'])
 %ANSWER X = 6.4

 %QUERY number_chars(X,['-','7','2','.','4','6','3'])
 %ANSWER X = -72.463

 %QUERY number_chars(X,['.','4','6','3'])
 %ANSWER X = 0.463

 %QUERY number_chars(X,['-','.','4','6','3'])
 %ANSWER X = -0.463

 %QUERY number_chars('193457260',X)
 %ERROR Unexpected type for first argument: ATOM
 */
/**
 * <code>number_chars(A,L)</code> - compares a number to a list of characters.
 * <p>
 * <code>number_chars(A,L)</code> compares the number <code>A</code> with the list of characters <code>L</code>.
 * </p>
 */
public final class NumberChars extends AbstractTermSplitFunction {
   public NumberChars() {
      super(true, false);
   }
}
