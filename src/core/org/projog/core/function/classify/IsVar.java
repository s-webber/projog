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
package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %FALSE var(abc)
 %FALSE var(1)
 %FALSE var(a(b,c))
 %FALSE var([a,b,c])
 %FALSE X=1, var(X)
 %QUERY var(X)
 %ANSWER X=UNINSTANTIATED VARIABLE 
 %QUERY X=Y, var(X)
 %ANSWER 
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %TRUE var(_)
 */
/**
 * <code>var(X)</code> - checks that a term is an uninstantiated variable.
 * <p>
 * <code>var(X)</code> succeeds if <code>X</code> is an <i>uninstantiated</i> variable.
 * </p>
 */
public final class IsVar extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg) {
      return arg.getType().isVariable();
   }
}
