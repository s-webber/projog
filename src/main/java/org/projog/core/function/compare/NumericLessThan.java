/*
 * Copyright 2013-2014 S. Webber
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
 %FALSE 2<1
 %FALSE 2<2
 %TRUE 2<3
 %FALSE 3-1<1
 %FALSE 1+1<4-2
 %TRUE 8/4<9/3
 %FALSE 1.5<3.0/2.0
 */
/**
 * <code>X&lt;Y</code> - numeric "less than" test.
 * <p>
 * Succeeds when the number argument <code>X</code> is less than the number argument <code>Y</code>.
 * </p>
 */
public final class NumericLessThan extends AbstractNumericComparisonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return compare(arg1, arg2) < 0;
   }
}
