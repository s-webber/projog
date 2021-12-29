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
package org.projog.core.predicate.builtin.io;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%LINK prolog-io
*/
/**
 * <code>current_input(X)</code> - match a term to the current input stream.
 * <p>
 * <code>current_input(X)</code> succeeds if the name of the current input stream matches with <code>X</code>, else
 * fails.
 * </p>
 */
public final class CurrentInput extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term argument) {
      return argument.unify(getFileHandles().getCurrentInputHandle());
   }
}
