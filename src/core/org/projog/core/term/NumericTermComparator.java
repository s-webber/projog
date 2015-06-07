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
package org.projog.core.term;

import java.util.Comparator;

import org.projog.core.Calculatables;
import org.projog.core.ProjogException;

/**
 * An implementation of {@code Comparator} for comparing instances of {@link Numeric}.
 * 
 * @see #compare(Term, Term, Calculatables)
 * @see TermComparator
 */
public final class NumericTermComparator implements Comparator<Term> {
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
    * Unlike {@link #compare(Term, Term)} this method will work for arguments that represent arithmetic expressions
    * (e.g. a {@link Structure} of the form {@code +(1,2)}) as well as {@link Numeric} terms.
    * 
    * @param t1 the first term to be compared
    * @param t2 the second term to be compared
    * @return a negative integer, zero, or a positive integer as the first term is less than, equal to, or greater than
    * the second
    * @throws ProjogException if either argument does not represent an arithmetic expression
    * @see #compare(Term, Term)
    * @see Calculatables#getNumeric(Term)
    */
   public int compare(Term t1, Term t2, Calculatables calculatables) {
      Numeric n1 = calculatables.getNumeric(t1);
      Numeric n2 = calculatables.getNumeric(t2);
      return compare(n1, n2);
   }

   /**
    * Compares two arguments, representing {@link Numeric} terms, for order.
    * <p>
    * Returns a negative integer, zero, or a positive integer as the numeric value represented by the first argument is
    * less than, equal to, or greater than the second.
    * <p>
    * Unlike {@link #compare(Term, Term, Calculatables)} this method only works for arguments that represent a
    * {@link Numeric} (e.g. a {@link Structure} of the form {@code +(1,2)} would cause a {@code ProjogException}).
    * 
    * @param t1 the first term to be compared
    * @param t2 the second term to be compared
    * @return a negative integer, zero, or a positive integer as the first term is less than, equal to, or greater than
    * the second
    * @throws ProjogException if either argument does not represent a {@link Numeric} term
    * @see #compare(Term, Term, Calculatables)
    */
   @Override
   public int compare(Term t1, Term t2) {
      Numeric n1 = TermUtils.castToNumeric(t1);
      Numeric n2 = TermUtils.castToNumeric(t2);
      return Double.compare(n1.getDouble(), n2.getDouble());
   }
}
