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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.decimalFraction;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.structure;
import static org.projog.core.math.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.math.ArithmeticOperators;
import org.projog.core.math.Numeric;
import org.projog.core.math.NumericTermComparator;

public class NumericTermComparatorTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();
   private final ArithmeticOperators operators = kb.getArithmeticOperators();

   @Test
   public void testCompareDecimalValues() {
      compareDecimals(decimalFraction(2.1), decimalFraction(2.1));
      compareDecimals(decimalFraction(2.1), decimalFraction(2.11));
      compareDecimals(decimalFraction(2.1), decimalFraction(-2.1));
   }

   @Test
   public void testCompareIntegerValues() {
      long[] values = {0, 1, 2, 7, -1, -2, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE, Long.MAX_VALUE - 1};
      for (int i1 = 0; i1 < values.length; i1++) {
         for (int i2 = i1; i2 < values.length; i2++) {
            comparePrimitives(values[i1], values[i2]);
         }
      }
   }

   @Test
   public void testMixedTypes() {
      compareMixedTypes(decimalFraction(2.0), integerNumber(2));
      compareMixedTypes(integerNumber(2), decimalFraction(2.0));

      compareMixedTypes(decimalFraction(1.9), integerNumber(2));
      compareMixedTypes(decimalFraction(2.1), integerNumber(2));

      compareMixedTypes(integerNumber(2), decimalFraction(2.0));
      compareMixedTypes(integerNumber(2), decimalFraction(2.0));
   }

   /** Demonstrate unexpected results that can occur due to loss of precision when comparing decimal fractions. */
   @Test
   public void testRoundingErrors() {
      long a = Long.MAX_VALUE;
      long b = a - 1; // "b" is less than "a" but they are considered equal when compared as decimal fractions

      assertEquals(1, NUMERIC_TERM_COMPARATOR.compare(integerNumber(a), integerNumber(b)));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(decimalFraction(a), integerNumber(b)));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(integerNumber(a), decimalFraction(b)));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(decimalFraction(a), decimalFraction(b)));

      assertEquals(-1, NUMERIC_TERM_COMPARATOR.compare(integerNumber(b), integerNumber(a)));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(decimalFraction(b), integerNumber(a)));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(integerNumber(b), decimalFraction(a)));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(decimalFraction(b), decimalFraction(a)));
   }

   /**
    * NumericTermComparator provides an overloaded version of {@link NumericTermComparator#compare(Term, Term)} that
    * also accepts a {@code KnowledgeBase} argument - this method tests that overloaded version.
    *
    * @see NumericTermComparator#compare(Term, Term, KnowledgeBase)
    * @see #testStructuresRepresentingArithmeticOperators
    */
   @Test
   public void testOverloadedCompareMethod() {
      compare("1+1", "5-3", kb, 0);
      compare("1.5", "3/2.0", kb, 0);
      compare("7*5", "4*9", kb, -1); //35v36
      compare("72", "8*9", kb, 0);
      compare("72", "60+13", kb, -1);
      compare("72", "74-3", kb, 1);
   }

   /**
    * Test that {@link NumericTermComparator#compare(Term, Term, KnowledgeBase)} works with {@code Structure}s that
    * represent arithmetic expressions.
    */
   @Test
   public void testStructuresRepresentingArithmeticOperators() {
      Structure addition = structure("+", integerNumber(1), integerNumber(3));
      Structure subtraction = structure("-", integerNumber(5), integerNumber(2));

      // test compare(Term, Term) evaluates structures representing arithmetic expressions
      assertEquals(1, NUMERIC_TERM_COMPARATOR.compare(addition, subtraction, operators));
      assertEquals(-1, NUMERIC_TERM_COMPARATOR.compare(subtraction, addition, operators));
      assertEquals(0, NUMERIC_TERM_COMPARATOR.compare(addition, addition, operators));

      // test compare(Term, Term, KnowledgeBase) throws a ProjogException if
      // a structure cannot be evaluated as an arithmetic expression
      try {
         NUMERIC_TERM_COMPARATOR.compare(addition, structure("-", integerNumber(5), atom()), operators);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find arithmetic operator: test/0", e.getMessage());
      }
      try {
         NUMERIC_TERM_COMPARATOR.compare(structure("~", integerNumber(5), integerNumber(2)), subtraction, operators);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find arithmetic operator: ~/2", e.getMessage());
      }
   }

   private void comparePrimitives(long i1, long i2) {
      compareIntegers(integerNumber(i1), integerNumber(i2));
      compareIntegers(integerNumber(i2), integerNumber(i1));

      compareDecimals(decimalFraction(i1), decimalFraction(i2));
      compareDecimals(decimalFraction(i2), decimalFraction(i1));

      compareMixedTypes(decimalFraction(i1), integerNumber(i2));
      compareMixedTypes(integerNumber(i1), decimalFraction(i2));
   }

   private void compareIntegers(IntegerNumber t1, IntegerNumber t2) {
      Long i1 = t1.getLong();
      Long i2 = t2.getLong();
      assertEquals(i1.compareTo(i2), NUMERIC_TERM_COMPARATOR.compare(t1, t2));
      assertEquals(i2.compareTo(i1), NUMERIC_TERM_COMPARATOR.compare(t2, t1));
   }

   private void compareDecimals(DecimalFraction t1, DecimalFraction t2) {
      Double d1 = t1.getDouble();
      Double d2 = t2.getDouble();
      assertEquals(d1.compareTo(d2), NUMERIC_TERM_COMPARATOR.compare(t1, t2));
      assertEquals(d2.compareTo(d1), NUMERIC_TERM_COMPARATOR.compare(t2, t1));
   }

   private void compareMixedTypes(Numeric t1, Numeric t2) {
      Double d1 = t1.getDouble();
      Double d2 = t2.getDouble();
      assertEquals(d1.compareTo(d2), NUMERIC_TERM_COMPARATOR.compare(t1, t2));
      assertEquals(d2.compareTo(d1), NUMERIC_TERM_COMPARATOR.compare(t2, t1));
   }

   private void compare(String s1, String s2, KnowledgeBase kb, int expected) {
      Term t1 = TestUtils.parseSentence(s1 + ".");
      Term t2 = TestUtils.parseSentence(s2 + ".");
      assertEquals(expected, NUMERIC_TERM_COMPARATOR.compare(t1, t2, operators));
      assertEquals(0 - expected, NUMERIC_TERM_COMPARATOR.compare(t2, t1, operators));
   }
}
