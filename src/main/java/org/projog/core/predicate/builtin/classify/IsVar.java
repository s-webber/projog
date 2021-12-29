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
%FAIL var(abc)
%FAIL var(1)
%FAIL var(a(b,c))
%FAIL var([a,b,c])
%FAIL X=1, var(X)
%?- var(X)
% X=UNINSTANTIATED VARIABLE
%?- X=Y, var(X)
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
%TRUE var(_)
*/
/**
 * <code>var(X)</code> - checks that a term is an uninstantiated variable.
 * <p>
 * <code>var(X)</code> succeeds if <code>X</code> is an <i>uninstantiated</i> variable.
 * </p>
 */
public final class IsVar extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      return arg.getType().isVariable();
   }
}
