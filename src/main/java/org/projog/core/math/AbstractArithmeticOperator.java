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

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseConsumer;
import org.projog.core.term.Term;

public abstract class AbstractArithmeticOperator implements PreprocessableArithmeticOperator, KnowledgeBaseConsumer {
   private ArithmeticOperators operators;

   /**
    * Provides a reference to a {@code KnowledgeBase}.
    * <p>
    * Meaning this object will always have access to a {@code KnowledgeBase} by the time its {@code calculate} method is
    * invoked.
    */
   @Override
   public final void setKnowledgeBase(KnowledgeBase kb) {
      operators = kb.getArithmeticOperators();
   }

   @Override
   public final Numeric calculate(Term[] args) {
      switch (args.length) {
         case 1:
            Numeric n = operators.getNumeric(args[0]);
            return calculate(n);
         case 2:
            Numeric n1 = operators.getNumeric(args[0]);
            Numeric n2 = operators.getNumeric(args[1]);
            return calculate(n1, n2);
         default:
            throw createWrongNumberOfArgumentsException(args.length);
      }
   }

   protected Numeric calculate(Numeric n) {
      throw createWrongNumberOfArgumentsException(1);
   }

   protected Numeric calculate(Numeric n1, Numeric n2) {
      throw createWrongNumberOfArgumentsException(2);
   }

   private IllegalArgumentException createWrongNumberOfArgumentsException(int numberOfArguments) {
      throw new IllegalArgumentException("The ArithmeticOperator: " + getClass() + " does next accept the number of arguments: " + numberOfArguments);
   }

   @Override
   public final ArithmeticOperator preprocess(final Term expression) {
      if (!isPure()) {
         return this;
      }

      Term[] arguments = expression.getArgs();
      if (arguments.length == 1) {
         return preprocessUnaryOperator(arguments[0]);
      } else if (arguments.length == 2) {
         return preprocessBinaryOperator(arguments[0], arguments[1]);
      } else {
         throw createWrongNumberOfArgumentsException(arguments.length);
      }
   }

   /**
    * Indicates if this operator is pure and so can be preprocessed.
    * <p>
    * An operator is pure if multiple calls with identical arguments always produce the same result.
    *
    * @return true if pure and so can be preprocessed, else false
    */
   protected boolean isPure() {
      return true;
   }

   private ArithmeticOperator preprocessUnaryOperator(final Term argument) {
      final ArithmeticOperator o = operators.getPreprocessedArithmeticOperator(argument);
      if (o instanceof Numeric) {
         return calculate((Numeric) o);
      } else if (o != null) {
         return new PreprocessedUnaryOperator(o);
      } else {
         return this;
      }
   }

   private ArithmeticOperator preprocessBinaryOperator(final Term argument1, Term argument2) {
      final ArithmeticOperator o1 = operators.getPreprocessedArithmeticOperator(argument1);
      final ArithmeticOperator o2 = operators.getPreprocessedArithmeticOperator(argument2);
      if (o1 instanceof Numeric && o2 instanceof Numeric) {
         return calculate((Numeric) o1, (Numeric) o2);
      } else if (o1 != null || o2 != null) {
         return new PreprocessedBinaryOperator(o1, o2);
      } else {
         return this;
      }
   }

   private final class PreprocessedUnaryOperator implements ArithmeticOperator {
      final ArithmeticOperator o;

      PreprocessedUnaryOperator(ArithmeticOperator o) {
         this.o = o;
      }

      @Override
      public Numeric calculate(Term[] args) {
         Numeric n = o.calculate(args[0].getArgs());
         return AbstractArithmeticOperator.this.calculate(n);
      }
   }

   private final class PreprocessedBinaryOperator implements ArithmeticOperator {
      final ArithmeticOperator o1;
      final ArithmeticOperator o2;

      PreprocessedBinaryOperator(ArithmeticOperator o1, ArithmeticOperator o2) {
         this.o1 = o1;
         this.o2 = o2;
      }

      @Override
      public Numeric calculate(Term[] args) {
         Numeric n1 = o1 == null ? operators.getNumeric(args[0]) : o1.calculate(args[0].getArgs());
         Numeric n2 = o2 == null ? operators.getNumeric(args[1]) : o2.calculate(args[1].getArgs());
         return AbstractArithmeticOperator.this.calculate(n1, n2);
      }
   }
}
