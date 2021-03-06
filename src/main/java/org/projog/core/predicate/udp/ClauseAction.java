/*
 * Copyright 2020 S. Webber
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
package org.projog.core.predicate.udp;

import org.projog.core.predicate.Predicate;
import org.projog.core.term.Term;

public interface ClauseAction {
   Predicate getPredicate(Term[] input);

   // TODO is this method needed? should it return model.copy()
   ClauseModel getModel();

   boolean isRetryable();

   boolean isAlwaysCutOnBacktrack();
}
