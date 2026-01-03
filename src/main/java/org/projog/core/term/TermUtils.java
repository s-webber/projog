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
package org.projog.core.term;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.ProjogException;
import org.projog.core.math.ArithmeticOperators;
import org.projog.core.math.Numeric;

/**
 * Helper methods for performing common tasks on {@link Term} instances.
 */
public final class TermUtils {
   /**
    * A {@link Term} array of length 0.
    * <p>
    * Should be used wherever a zero-length {@link Term} array is required in order to minimise object creation.
    */
   public static final Term[] EMPTY_ARRAY = new Term[0];

   /**
    * Private constructor as all methods are static.
    */
   private TermUtils() {
      // do nothing
   }

   /**
    * Returns copies of the specified {link Term}s
    *
    * @param input {@link Term}s to copy
    * @return copies of the specified {link Term}s
    */
   public static Term[] copy(final Term... input) {
      final int numTerms = input.length;
      final Term[] output = new Term[numTerms];
      final Map<Variable, Term> vars = new HashMap<>();
      for (int i = 0; i < numTerms; i++) {
         output[i] = input[i].copy(vars);
      }
      return output;
   }

   /**
    * Backtracks all {@link Term}s in the specified array.
    *
    * @param terms {@link Term}s to backtrack
    * @see Term#backtrack()
    */
   public static void backtrack(final Term[] terms) {
      for (final Term t : terms) {
         t.backtrack();
      }
   }

   /**
    * Return the {@link Numeric} represented by the specified {@link Term}.
    *
    * @param t the term representing a {@link Numeric}
    * @return the {@link Numeric} represented by the specified {@link Term}
    * @throws ProjogException if the specified {@link Term} does not represent a {@link Numeric}
    */
   public static Numeric castToNumeric(final Term t) {
      if (t.getType().isNumeric()) {
         return (Numeric) t.getTerm();
      } else {
         throw new ProjogException("Expected Numeric but got: " + t.getType() + " with value: " + t);
      }
   }

   /**
    * Returns the integer value of the {@link Numeric} represented by the specified {@link Term}.
    *
    * @param t the term representing a {@link Numeric}
    * @return the {@code int} value represented by {@code t}
    * @throws ProjogException if the specified {@link Term} cannot be represented as an {@code int}.
    */
   public static int toInt(final Term t) {
      Numeric n = castToNumeric(t);
      long l = n.getLong();
      if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
         throw new ProjogException("Value cannot be cast to an int without losing precision: " + l);
      }
      return (int) l;
   }

   /**
    * Return the long value represented by the specified term.
    *
    * @param t the term representing a long value
    * @return the {@code long} value represented by {@code t}
    * @throws ProjogException if the specified {@link Term} does not represent a term of type {@link TermType#INTEGER}
    */
   public static long toLong(final ArithmeticOperators operators, final Term t) {
      final Numeric n = operators.getNumeric(t);
      assertType(n, TermType.INTEGER);
      return n.getLong();
   }

   /**
    * Return the name of the {@link Atom} represented by the specified {@link Atom}.
    *
    * @param t the term representing an {@link Atom}
    * @return the name of {@link Atom} represented by the specified {@link Term}
    * @throws ProjogException if the specified {@link Term} does not represent an {@link Atom}
    */
   public static String getAtomName(final Term t) {
      assertType(t, TermType.ATOM);
      return t.getName();
   }

   public static void assertType(final Term t, final TermType type) {
      if (t.getType() != type) {
         throw new ProjogException("Expected " + type + " but got: " + t.getType() + " with value: " + t);
      }
   }

   public static boolean termsEqual(Term a, Term b) {
      return a.getTerm().equals(b.getTerm());
   }
}
