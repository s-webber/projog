/*
 * Copyright 2018 S. Webber
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
import org.projog.core.term.TermUtils;

/* TEST
%?- X \== Y
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE

%FAIL X \== X

%FAIL X=Y, X \== Y, Y=1

%?- X \== Y, Y = 1, X = Y
% X=1
% Y=1

%?- append([A|B],C) \== append(X,Y)
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE

%FAIL append([A|B],C) \== append([A|B],C)
*/
/**
 * <code>X\==Y</code> - a strict equality test.
 * <p>
 * If <code>X</code> cannot be matched with <code>Y</code> the goal succeeds else the goal fails. A <code>X\==Y</code>
 * goal will only consider an uninstantiated variable to be equal to another uninstantiated variable that is already
 * sharing with it.
 * </p>
 */
public final class NotStrictEquality extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      return !TermUtils.termsEqual(arg1, arg2);
   }
}
