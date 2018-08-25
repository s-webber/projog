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

import org.projog.core.ProjogException;
import org.projog.core.term.IntegerNumber;

/* TEST
 %QUERY number_chars(7348,X)
 %ANSWER X = [7,3,4,8]
 
 %QUERY number_chars(X,[7,3,4,8])
 %ANSWER X = 7348

 %QUERY number_chars(X,['6','8','3'])
 %ANSWER X = 683
 
 %QUERY number_chars(X,['-',8,3,5])
 %ANSWER X = -835

 %QUERY number_chars(X,['o','n','e'])
 %ERROR Could not convert characters to an integer: 'one'
 
 %QUERY number_chars(X,1257)
 %ERROR As the first argument: X is a variable the second argument needs to be a list but was: 1257 of type: INTEGER
 
 %QUERY number_chars( X , ['1','.','2'])
 %ERROR Could not convert characters to an integer: '1.2'
 
 %QUERY number_chars(-12 ,Y)
 %ERROR Could not convert characters to an integer: '-' 
 */
/**
 * <code>number_chars(A,L)</code> - compares a number to a list of characters.
 * <p>
 * <code>number_chars(A,L)</code> compares the number <code>A</code> with the list of characters <code>L</code>.
 * </p>
 */
public final class NumberChars extends AbstractTermSplitFunction {
   @Override
   protected IntegerNumber toTerm(String s) {
      try {
         return new IntegerNumber(Integer.parseInt(s));
      } catch (NumberFormatException e) {
         throw new ProjogException("Could not convert characters to an integer: '" + s + "'");
      }
   }
}
