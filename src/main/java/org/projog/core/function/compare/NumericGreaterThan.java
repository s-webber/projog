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
package org.projog.core.function.compare;

import org.projog.core.term.Term;

/* TEST
 %TRUE 2>1
 %FALSE 2>2
 %FALSE 2>3
 %TRUE 3-1>1
 %FALSE 1+1>4-2
 %FALSE 8/4>9/3
 %FALSE 1.5>3.0/2.0

 %FALSE 7>7.0
 %FALSE 7.0>7
 %FALSE 7.0>7.0
 %FALSE 7.5>7.5

 %FALSE 7.4999>7.5
 %TRUE 7.5001>7.5

 %FALSE 7>7.5
 %FALSE 7>7.0001
 %FALSE 6.9999>7

 %TRUE 8>7.5
 %TRUE 8>7.9999
 %TRUE 8.0001>8

 %FALSE 9223372036854775806 > 9223372036854775807
 %TRUE 9223372036854775807 > 9223372036854775806
 %FALSE 9223372036854775807 > 9223372036854775807
 %FALSE 9223372036854775806 > 9223372036854775806

 %FALSE -9223372036854775808 > -9223372036854775807
 %TRUE -9223372036854775807 > -9223372036854775808
 %FALSE -9223372036854775808 > -9223372036854775808
 %FALSE -9223372036854775807 > -9223372036854775807
 */
/**
 * <code>X&gt;Y</code> - numeric "greater than" test.
 * <p>
 * Succeeds when the number argument <code>X</code> is greater than the number argument <code>Y</code>.
 * </p>
 */
public final class NumericGreaterThan extends AbstractNumericComparisonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return compare(arg1, arg2) > 0;
   }
}
