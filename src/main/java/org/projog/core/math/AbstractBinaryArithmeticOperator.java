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

import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.TermType;

/**
 * A template for {@code ArithmeticOperator}s that accept two arguments.
 */
public abstract class AbstractBinaryArithmeticOperator extends AbstractArithmeticOperator {
   @Override
   public final Numeric calculate(Numeric n1, Numeric n2) {
      if (containsFraction(n1, n2)) {
         double answer = calculateDouble(n1.getDouble(), n2.getDouble());
         return new DecimalFraction(answer);
      } else {
         long answer = calculateLong(n1.getLong(), n2.getLong());
         return IntegerNumberCache.valueOf(answer);
      }
   }

   private static boolean containsFraction(Numeric n1, Numeric n2) {
      return n1.getType() == TermType.FRACTION || n2.getType() == TermType.FRACTION;
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract double calculateDouble(double n1, double n2);

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract long calculateLong(long n1, long n2);
}
