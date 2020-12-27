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

import org.projog.core.ProjogException;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;
import org.projog.core.term.TermType;

/**
 * Provides methods for comparing instances of {@link Numeric}.
 *
 * @see #compare(Term, Term, ArithmeticOperators)
 * @see TermComparator
 */
public final class NumericTermComparator {
   /**
    * Singleton instance
    */
   public static final NumericTermComparator NUMERIC_TERM_COMPARATOR = new NumericTermComparator();

   /**
    * Private constructor to force use of {@link #NUMERIC_TERM_COMPARATOR}
    */
   private NumericTermComparator() {
      // do nothing
   }

   /**
    * Compares the two arguments, representing arithmetic expressions, for order.
    * <p>
    * Returns a negative integer, zero, or a positive integer as the numeric value represented by the first argument is
    * less than, equal to, or greater than the second.
    * <p>
    * Unlike {@link #compare(Numeric, Numeric)} this method will work for arguments that represent arithmetic
    * expressions (e.g. a {@link Structure} of the form {@code +(1,2)}) as well as {@link Numeric} terms.
    *
    * @param t1 the first term to be compared
    * @param t2 the second term to be compared
    * @return a negative integer, zero, or a positive integer as the first term is less than, equal to, or greater than
    * the second
    * @throws ProjogException if either argument does not represent an arithmetic expression
    * @see #compare(Numeric, Numeric)
    * @see ArithmeticOperators#getNumeric(Term)
    */
   public int compare(Term t1, Term t2, ArithmeticOperators operators) {
      Numeric n1 = operators.getNumeric(t1);
      Numeric n2 = operators.getNumeric(t2);
      return compare(n1, n2);
   }

   /**
    * Compares two arguments, representing {@link Numeric} terms, for order.
    * <p>
    * Returns a negative integer, zero, or a positive integer as the numeric value represented by the first argument is
    * less than, equal to, or greater than the second.
    *
    * @param n1 the first term to be compared
    * @param n2 the second term to be compared
    * @return a negative integer, zero, or a positive integer as the first term is less than, equal to, or greater than
    * the second
    * @see #compare(Term, Term, ArithmeticOperators)
    */
   public int compare(Numeric n1, Numeric n2) {
      if (n1.getType() == TermType.INTEGER && n2.getType() == TermType.INTEGER) {
         return Long.compare(n1.getLong(), n2.getLong());
      } else {
         return Double.compare(n1.getDouble(), n2.getDouble());
      }
   }
}
