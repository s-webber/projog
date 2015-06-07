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

import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;

/* TEST
 validate_in_range(X) :- Y is random(X), Y>=0, Y<X.
 
 %TRUE validate_in_range(3), validate_in_range(7), validate_in_range(100)

 %QUERY X is random(1)
 %ANSWER X=0
 */
/**
 * <code>random(X)</code> Evaluate to a random integer i for which 0 =< i < X.
 */
public final class Random extends AbstractCalculatable {
   @Override
   public Numeric calculate(Numeric n) {
      long max = n.getLong();
      return new IntegerNumber((long) (Math.random() * max));
   }
}
