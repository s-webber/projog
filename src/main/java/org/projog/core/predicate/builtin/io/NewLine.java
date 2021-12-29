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

/* TEST
%?- write('a'), write('b'), nl, write('c')
%OUTPUT
%ab
%c
%OUTPUT
%YES
*/
/**
 * <code>nl</code> - outputs a new line character.
 * <p>
 * Causes a line break to be output to the current stream.
 * </p>
 * <p>
 * This goal succeeds only once.
 * </p>
 */
public final class NewLine extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate() {
      getFileHandles().getCurrentOutputStream().println();
      return true;
   }
}
