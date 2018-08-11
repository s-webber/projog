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

import static org.projog.core.term.TermType.INTEGER;

import org.projog.core.ProjogException;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.TermType;

/**
 * A template for {@code Calculatable}s that accept two arguments of type {@link TermType#INTEGER}.
 */
public abstract class AbstractTwoIntegerArgumentsCalculatable extends AbstractCalculatable {
   @Override
   public final Numeric calculate(Numeric n1, Numeric n2) {
      final long i1 = toLong(n1);
      final long i2 = toLong(n2);
      return new IntegerNumber(calculateLong(i1, i2));
   }

   private long toLong(Numeric n) {
      if (n.getType() == INTEGER) {
         return n.getLong();
      } else {
         throw new ProjogException("Expected integer but got: " + n.getType() + " with value: " + n);
      }
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract long calculateLong(long n1, long n2);
}
