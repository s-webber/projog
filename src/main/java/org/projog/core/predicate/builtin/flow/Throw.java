/*
 * Copyright 2023 S. Webber
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
package org.projog.core.predicate.builtin.flow;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%?- throw(error_message)
%ERROR Error: error_message

%?- throw(a(1,[x,y,z]))
%ERROR Error: a(1, [x,y,z])
 */
/**
 * <code>throw(X)</code> - throws an exception with the given message.
 */
public final class Throw extends AbstractSingleResultPredicate {
   @Override
   public boolean evaluate(Term error) {
      throw new ProjogException("Error: " + getTermFormatter().formatTerm(error));
   }
}
