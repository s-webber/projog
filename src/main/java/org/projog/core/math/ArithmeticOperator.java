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
package org.projog.core.math;

import org.projog.core.term.Term;

/**
 * Represents a function that returns a single numerical value.
 * <p>
 * <img src="doc-files/ArithmeticOperator.png">
 * </p>
 *
 * @see ArithmeticOperators
 */
public interface ArithmeticOperator {
   /**
    * Returns the result of the calculation using the specified arguments.
    *
    * @param args the arguments to use in the calculation
    * @return the result of the calculation using the specified arguments
    */
   Numeric calculate(Term[] args);
}
