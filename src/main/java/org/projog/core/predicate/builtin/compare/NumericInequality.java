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

import org.projog.core.term.Term;

/* TEST
%FAIL 1=\=1
%FAIL 1.5=\=3.0/2.0
%FAIL 6*6=\=9*4
%TRUE 1=\=2
%TRUE 1+1=\=1-1
%FAIL X=1, Y=1, X=\=Y
%?- X=1, Y=2, X=\=Y
% X=1
% Y=2

%FAIL 7.0=\=7.0
%FAIL 7=\=7.0
%FAIL 7.0=\=7
%FAIL 7=\=7
%FAIL 7.1=\=7.1
%TRUE 7.0=\=7.1
%TRUE 7.1=\=7.2
%TRUE 7.2=\=7.1
%TRUE 7.1=\=7
%TRUE 7=\=7.1
%FAIL -7=\=-7
%TRUE 7=\=-7
%TRUE -7=\=7

%TRUE 9223372036854775806 =\= 9223372036854775807
%TRUE 9223372036854775807 =\= 9223372036854775806
%FAIL 9223372036854775807 =\= 9223372036854775807
%FAIL 9223372036854775806 =\= 9223372036854775806

%TRUE -9223372036854775808 =\= -9223372036854775807
%TRUE -9223372036854775807 =\= -9223372036854775808
%FAIL -9223372036854775808 =\= -9223372036854775808
%FAIL -9223372036854775807 =\= -9223372036854775807

% Note that due to loss of precision when comparing decimal fractions the following queries evaluate to false:
%FAIL 9223372036854775806 =\= 9223372036854775807.0
%FAIL 9223372036854775806.0 =\= 9223372036854775807
%FAIL 9223372036854775806.0 =\= 9223372036854775807.0
*/
/**
 * <code>X=\=Y</code> - numeric inequality test.
 * <p>
 * Succeeds when the number argument <code>X</code> is <i>not</i> equal to the number argument <code>Y</code>.
 * </p>
 */
public final class NumericInequality extends AbstractNumericComparisonPredicate {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      return compare(arg1, arg2) != 0;
   }
}
