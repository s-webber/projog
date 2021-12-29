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
package org.projog.core.predicate.builtin.bool;

import org.projog.core.predicate.AbstractSingleResultPredicate;

/* TEST
%FAIL fail

a(1).
a(2).
a(3).

test :- a(X), a(Y), write(Y), write(' '), write(X), nl, fail.

%?- test
%OUTPUT
%1 1
%2 1
%3 1
%1 2
%2 2
%3 2
%1 3
%2 3
%3 3
%
%OUTPUT
*/
/**
 * <code>fail</code> - always fails.
 * <p>
 * The goal <code>fail</code> always fails.
 * </p>
 */
public final class Fail extends AbstractSingleResultPredicate {
   @Override
   public boolean evaluate() {
      return false;
   }
}
