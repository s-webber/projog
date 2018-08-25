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
package org.projog.core.function.math;

import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.TermType;

/**
 * A template for {@code Calculatable}s that accept exactly one argument.
 */
public abstract class AbstractOneArgumentCalculatable extends AbstractCalculatable {
   @Override
   public final Numeric calculate(Numeric n) {
      if (n.getType() == TermType.FRACTION) {
         double answer = calculateDouble(n.getDouble());
         return new DecimalFraction(answer);
      } else {
         long answer = calculateLong(n.getLong());
         return new IntegerNumber(answer);
      }
   }

   /** Returns the result of evaluating an arithmetic expression using the specified argument */
   protected abstract double calculateDouble(double n);

   /** Returns the result of evaluating an arithmetic expression using the specified argument */
   protected abstract long calculateLong(long n);
}
