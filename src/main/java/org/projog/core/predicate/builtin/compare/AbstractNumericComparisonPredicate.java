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

import static org.projog.core.math.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

abstract class AbstractNumericComparisonPredicate extends AbstractSingleResultPredicate {
   protected int compare(Term arg1, Term arg2) {
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, getArithmeticOperators());
   }
}
