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
package org.projog.core.math.builtin;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseConsumer;
import org.projog.core.math.ArithmeticOperator;
import org.projog.core.math.ArithmeticOperators;
import org.projog.core.math.Numeric;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- X is 2 ** 1
% X=2

%?- X is 2 ** 2
% X=4

%?- X is 2 ** 5
% X=32

%?- X is 5 ** 3
% X=125

%?- X is 5.0 ** 3
% X=125.0

%?- X is 5 ** 3.0
% X=125.0

%?- X is 5.0 ** 3.0
% X=125.0

%?- X is 2 + 5 ** 3 - 1
% X=126

%?- X is -2 ** 2
% X=4

%?- X is -2 ** -2
% X=0.25

%?- X is 2 ** -2
% X=0.25

%?- X is 0.5 ** 2
% X=0.25

%?- X is 2 ** 3
% X=8

%?- X is 2 ** -3
% X=0.125

%?- X is 1 ** -100
% X=1

%?- X is -1 ** -3
% X=-1

%?- X is 0 ** 2
% X=0

%?- X is 2 ** 0.25
% X=1.189207115002721

%?- X is 2 ** -0.25
% X=0.8408964152537145

%?- X is -2 ** 0.25
% X=NaN

%?- X is -2 ** -0.25
% X=NaN

%?- X is 0 ** -1
% X=Infinity

%?- X is 0.0 ** -1.0
% X=Infinity

%?- X is 0 ** 0
% X=1

%?- X is 0.0 ** 0.0
% X=1.0

%?- X is 1 ** -5
% X=1

%?- X is -1 ** -4
% X=1

%?- X is -1 ** -5
% X=-1

% Note: "^" is a synonym for "**".
%?- X is 3^7
% X=2187
*/
/**
 * <code>**</code> - calculates the result of the first argument raised to the power of the second argument.
 */
public final class Power implements ArithmeticOperator, KnowledgeBaseConsumer {
   private ArithmeticOperators operators;

   @Override
   public Numeric calculate(Term[] args) {
      Numeric base = operators.getNumeric(args[0]);
      Numeric exponent = operators.getNumeric(args[1]);
      if (base.getType() == TermType.INTEGER && exponent.getType() == TermType.INTEGER && (exponent.getLong() >= 0 || base.getLong() == 1 || base.getLong() == -1)) {
         long result = integerPow(base.getLong(), exponent.getLong());
         return new IntegerNumber(result);
      } else {
         double result = Math.pow(base.getDouble(), exponent.getDouble());
         return new DecimalFraction(result);
      }
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      this.operators = kb.getArithmeticOperators();
   }

   public static long integerPow(long base, long exponent) {
      if (exponent < 0) {
         if (base == 1) {
            return 1;
         } else if (base == -1) {
            return (exponent % 2 == 0) ? 1 : -1;
         } else {
            throw new IllegalArgumentException("Negative exponents not supported for integers.");
         }
      }

      int result = 1;
      while (exponent > 0) {
         if ((exponent & 1) == 1) {
            result *= base;
         }
         base *= base;
         exponent >>= 1;
      }
      return result;
   }
}
