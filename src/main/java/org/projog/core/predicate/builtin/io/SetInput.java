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
 * <code>set_input(X)</code> - sets the current input.
 * <p>
 * <code>set_input(X)</code> sets the current input to the stream represented by <code>X</code>.
 * </p>
 * <p>
 * <code>X</code> will be a term returned as the third argument of <code>open</code>, or the atom
 * <code>user_input</code>, which specifies that input to come from the keyboard.
 * </p>
 */
public final class SetInput extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      getFileHandles().setInput(arg);
      return true;
   }
}
