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
package org.projog.core.predicate.builtin.compare;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;

/* TEST
%TRUE b@>a
%FAIL b@>b
%FAIL b@>c
%TRUE b@>1
%FAIL b@>b(a)
*/
/**
 * <code>X@&gt;Y</code> - term "greater than" test.
 * <p>
 * Succeeds when the term argument <code>X</code> is greater than the term argument <code>Y</code>.
 * </p>
 */
public final class TermGreaterThan extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      return TermComparator.TERM_COMPARATOR.compare(arg1, arg2) > 0;
   }
}
