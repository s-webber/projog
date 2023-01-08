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

import org.projog.clp.Expression;
import org.projog.clp.math.Absolute;
import org.projog.clp.math.Add;
import org.projog.clp.math.Divide;
import org.projog.clp.math.Maximum;
import org.projog.clp.math.Minimum;
import org.projog.clp.math.Minus;
import org.projog.clp.math.Multiply;
import org.projog.clp.math.Subtract;

public final class CommonExpression implements ExpressionFactory {
   private final Function<Expression[], Expression> function;

   public static ExpressionFactory add() {
      return new CommonExpression(args -> new Add(args[0], args[1]));
   }

   public static ExpressionFactory subtract() {
      return new CommonExpression(args -> new Subtract(args[0], args[1]));
   }

   public static ExpressionFactory multiply() {
      return new CommonExpression(args -> new Multiply(args[0], args[1]));
   }

   public static ExpressionFactory divide() {
      return new CommonExpression(args -> new Divide(args[0], args[1]));
   }

   public static ExpressionFactory minimum() {
      return new CommonExpression(args -> new Minimum(args[0], args[1]));
   }

   public static ExpressionFactory maximum() {
      return new CommonExpression(args -> new Maximum(args[0], args[1]));
   }

   public static ExpressionFactory absolute() {
      return new CommonExpression(args -> new Absolute(args[0]));
   }

   public static ExpressionFactory minus() {
      return new CommonExpression(args -> new Minus(args[0]));
   }

   private CommonExpression(Function<Expression[], Expression> function) {
      this.function = function;
   }

   @Override
   public Expression createExpression(Expression[] args) {
      return function.apply(args);
   }
}
