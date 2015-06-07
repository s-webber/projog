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
 %TRUE float(1.0)
 %TRUE float(-1.0)
 %TRUE float(0.0)
 %FALSE float(1)
 %FALSE float(-1)
 %FALSE float(0)
 %FALSE float('1')
 %FALSE float('1.0')
 %FALSE float(a)
 %FALSE float(p(1.0,2.0,3.0))
 %FALSE float([1.0,2.0,3.0])
 %FALSE float([])
 %FALSE float(X)
 %FALSE float(_)
*/
/**
 * <code>float(X)</code> - checks that a term is a floating point number.
 * <p>
 * <code>float(X)</code> succeeds if <code>X</code> currently stands for a floating point number.
 * </p>
 */
public final class IsFloat extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg) {
      return arg.getType() == TermType.FRACTION;
   }
}
