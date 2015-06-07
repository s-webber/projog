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
package org.projog.core.function.time;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/* TEST
 validate_get_time :- get_time(X), X>1000000000000, get_time(Y), Y>=X.

 %TRUE validate_get_time
 */
/**
 * <code>get_time(X)</code> - gets the current system time.
 * <p>
 * Attempts to unify <code>X</code> with an integer term representing the value returned from <code>java.lang.System.currentTimeMillis()</code>. 
 * </p>
 */
public final class GetTime extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term t) {
      IntegerNumber currentTime = new IntegerNumber(System.currentTimeMillis());
      return t.unify(currentTime);
   }
}
