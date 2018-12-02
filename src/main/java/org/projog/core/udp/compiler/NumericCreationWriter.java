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
package org.projog.core.udp.compiler;

import org.projog.core.term.Term;

/** Generates Java source code to construct a {@code Numeric}. */
final class NumericCreationWriter {
   String outputCreateNumericStatement(Term t, CompiledPredicateState state) {
      if (t.getType().isNumeric()) {
         return state.outputCreateTermStatement(t, false);
      } else if (t.getType().isVariable()) {
         return CompiledPredicateConstants.ARITHMETIC_OPERATORS_VARIABLE_NAME + ".getNumeric(" + state.outputCreateTermStatement(t, false) + ")";
      } else {
         StringBuilder args = new StringBuilder();
         for (int i = 0; i < t.getNumberOfArguments(); i++) {
            if (i != 0) {
               args.append(',');
            }
            args.append(outputCreateNumericStatement(t.getArgument(i), state));
         }
         return state.getArithmeticOperator(t).getVariableName() + ".calculate(" + args + ")";
      }
   }
}
