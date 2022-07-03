/*
 * Copyright 2022 S. Webber
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
package org.projog.core.predicate.builtin.clp;

import java.util.function.Function;

import org.projog.clp.Add;
import org.projog.clp.Expression;
import org.projog.clp.Multiply;
import org.projog.clp.Subtract;

public class ClpExpression implements ClpExpressionFactory {
   private final Function<Expression[], Expression> function;

   public static ClpExpressionFactory add() {
      return new ClpExpression(args -> new Add(args[0], args[1]));
   }

   public static ClpExpressionFactory subtract() {
      return new ClpExpression(args -> new Subtract(args[0], args[1]));
   }

   public static ClpExpressionFactory multiply() {
      return new ClpExpression(args -> new Multiply(args[0], args[1]));
   }

   private ClpExpression(Function<Expression[], Expression> function) {
      this.function = function;
   }

   @Override
   public Expression createExpression(Expression[] args) {
      return function.apply(args);
   }
}
