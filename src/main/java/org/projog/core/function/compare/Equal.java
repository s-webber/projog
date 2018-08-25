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
 %TRUE a=a
 %FALSE a=b
 %QUERY a=X
 %ANSWER X=a
 %FALSE 2=1+1
 %FALSE p(b,c)=p(b,d)
 %FALSE p(b,c)=p(c,b)
 %QUERY p(b,c)=p(b,X)
 %ANSWER X=c
 %QUERY p(Y,c)=p(b,X)
 %ANSWER 
 % Y=b 
 % X=c
 %ANSWER
 %TRUE [a,b,c]=[a,b,c]
 %FALSE [a,b,c]=[a,b,d]
 */
/**
 * <code>X=Y</code> - an equality test.
 * <p>
 * If <code>X</code> can be matched with <code>Y</code> the goal succeeds else the goal fails. A <code>X=Y</code> goal
 * will consider an uninstantiated variable to be equal to anything. A <code>X=Y</code> goal will always succeed if
 * either argument is uninstantiated.
 * </p>
 */
public final class Equal extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return arg1.unify(arg2);
   }
}
