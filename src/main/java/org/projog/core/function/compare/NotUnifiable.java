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
package org.projog.core.function.compare;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE abc \= def

 %FALSE X \= Y

 %FALSE p(X,b) \= p(a,Y)

 %QUERY p(X,b,c) \= p(a,Y,z)
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE 
 %ANSWER
 */
/**
 * <code>X \= Y</code> - checks whether two terms cannot be unified.
 * <p>
 * If <code>X</code> can be NOT unified with <code>Y</code> the goal succeeds
 * else the goal fails.
 * </p>
 */
public final class NotUnifiable extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      final boolean unifiable = arg1.unify(arg2);
      arg1.backtrack();
      arg2.backtrack();
      return !unifiable;
   }
}
