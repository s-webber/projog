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
import org.projog.core.term.TermType;

/* TEST
%TRUE atom(abc)
%FAIL atom(1)
%FAIL atom(X)
%FAIL atom(_)
%FAIL atom(a(b,c))
%FAIL atom([a,b,c])
*/
/**
 * <code>atom(X)</code> - checks that a term is an atom.
 * <p>
 * <code>atom(X)</code> succeeds if <code>X</code> currently stands for an atom.
 * </p>
 */
public final class IsAtom extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      return arg.getType() == TermType.ATOM;
   }
}
