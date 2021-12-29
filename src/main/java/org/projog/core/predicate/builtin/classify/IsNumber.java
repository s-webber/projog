/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.builtin.classify;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%TRUE number(1)
%TRUE number(-1)
%TRUE number(0)
%TRUE number(0.0)
%TRUE number(1.0)
%TRUE number(-1.0)
%FAIL number('1')
%FAIL number('1.0')
%FAIL number(1+1)
%FAIL number(a)
%FAIL number(p(1,2,3))
%FAIL number([1,2,3])
%FAIL number([a,b,c])
%FAIL number([])
%FAIL number(X)
%FAIL number(_)
*/
/**
 * <code>number(X)</code> - checks that a term is numeric.
 * <p>
 * <code>number(X)</code> succeeds if <code>X</code> currently stands for a number.
 * </p>
 */
public final class IsNumber extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      return arg.getType().isNumeric();
   }
}
