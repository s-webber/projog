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
import org.projog.core.term.TermType;

/* TEST
 %TRUE integer(1)
 %TRUE integer(-1)
 %TRUE integer(0)
 %FALSE integer(1.0)
 %FALSE integer(-1.0)
 %FALSE integer(0.0)
 %FALSE float('1')
 %FALSE float('1.0')
 %FALSE integer(a)
 %FALSE integer(p(1,2,3))
 %FALSE integer([1,2,3])
 %FALSE integer([])
 %FALSE integer(X)
 %FALSE integer(_)
*/
/**
 * <code>integer(X)</code> - checks that a term is an integer.
 * <p>
 * <code>integer(X)</code> succeeds if <code>X</code> currently stands for an integer.
 * </p>
 */
public final class IsInteger extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg) {
      return arg.getType() == TermType.INTEGER;
   }
}
