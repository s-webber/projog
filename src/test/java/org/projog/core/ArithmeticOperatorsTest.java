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
package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

public class ArithmeticOperatorsTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();
   private final String dummyOperatorName = "dummy_arithmetic_operator";
   private final PredicateKey dummyOperatorKey = new PredicateKey(dummyOperatorName, 1);
   private final int dummyTermArgument = 7;
   private final Structure dummyTerm = structure(dummyOperatorName, integerNumber(dummyTermArgument));

   @Test
   public void testGetNumericIntegerNumber() {
      ArithmeticOperators c = createOperators();
      IntegerNumber i = integerNumber(1);
      assertSame(i, c.getNumeric(i));
   }

   @Test
   public void testGetNumericDecimalFraction() {
      ArithmeticOperators c = createOperators();
      DecimalFraction d = decimalFraction(17.6);
      assertSame(d, c.getNumeric(d));
   }

   @Test
   public void testGetNumericException() {
      ArithmeticOperators c = createOperators();
      try {
         c.getNumeric(variable("X"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot get Numeric for term: X of type: NAMED_VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testGetNumericPredicate() {
      ArithmeticOperators c = createOperators();

      // try to use arithmetic operator by a name that there is no match for (expect exception)
      try {
         c.getNumeric(dummyTerm);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find arithmetic operator: dummy_arithmetic_operator/1", e.getMessage());
      }

      // add new arithmetic operator
      c.addArithmeticOperator(dummyOperatorKey, DummyArithmeticOperatorDefaultConstructor.class.getName());

      // assert that the factory is now using the newly added arithmetic operator
      Numeric n = c.getNumeric(dummyTerm);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(dummyTermArgument + 1, n.getLong());
   }

   @Test
   public void testAddExistingOperatorName() {
      ArithmeticOperators c = createOperators();

      // add new arithmetic operator class name
      c.addArithmeticOperator(dummyOperatorKey, DummyArithmeticOperatorDefaultConstructor.class.getName());

      // attempt to add arithmetic operator again
      // (should fail now a arithmetic operator with the same name already exists in the factoty)
      try {
         c.addArithmeticOperator(dummyOperatorKey, DummyArithmeticOperatorDefaultConstructor.class.getName());
         fail("could re-add arithmetic operator named: " + dummyOperatorName);
      } catch (ProjogException e) {
         // expected;
      }
      try {
         c.addArithmeticOperator(dummyOperatorKey, new DummyArithmeticOperatorDefaultConstructor());
         fail("could re-add arithmetic operator named: " + dummyOperatorName);
      } catch (ProjogException e) {
         // expected;
      }
   }

   @Test
   public void testAddExistingOperatorInstance() {
      ArithmeticOperators c = createOperators();

      // add new arithmetic operator instance
      c.addArithmeticOperator(dummyOperatorKey, new DummyArithmeticOperatorDefaultConstructor());

      // attempt to add arithmetic operator again
      // (should fail now a arithmetic operator with the same name already exists in the factoty)
      try {
         c.addArithmeticOperator(dummyOperatorKey, DummyArithmeticOperatorDefaultConstructor.class.getName());
         fail("could re-add arithmetic operator named: " + dummyOperatorName);
      } catch (ProjogException e) {
         // expected;
      }
      try {
         c.addArithmeticOperator(dummyOperatorKey, new DummyArithmeticOperatorDefaultConstructor());
         fail("could re-add arithmetic operator named: " + dummyOperatorName);
      } catch (ProjogException e) {
         // expected;
      }
   }

   @Test
   public void testAddOperatorError() {
      ArithmeticOperators c = createOperators();

      // add new arithmetic operator with invalid name
      c.addArithmeticOperator(dummyOperatorKey, "an invalid class name");
      try {
         c.getNumeric(dummyTerm);
         fail();
      } catch (RuntimeException e) {
         // expected as specified class name is invalid
         assertEquals("Could not create new ArithmeticOperator using: an invalid class name", e.getMessage());
      }
   }

   /** Test using a static method to add a arithmetic operator that does not have a public no arg constructor. */
   @Test
   public void testAddOperatorUsingStaticMethod() {
      final ArithmeticOperators c = createOperators();
      final String className = DummyArithmeticOperatorPublicConstructor.class.getName();
      c.addArithmeticOperator(dummyOperatorKey, className + "/getInstance");
      Numeric n = c.getNumeric(dummyTerm);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(dummyTermArgument * 3, n.getLong());
   }

   private ArithmeticOperators createOperators() {
      return new ArithmeticOperators(kb);
   }

   /** ArithmeticOperator used to test that new arithmetic operators can be added to the factory. */
   public static class DummyArithmeticOperatorDefaultConstructor implements ArithmeticOperator {
      KnowledgeBase kb;

      /**
       * @return an IntegerNumber with a value of the first input argument + 1
       */
      @Override
      public Numeric calculate(Term... args) {
         if (kb == null) {
            // setKnowledgeBase should be called by ArithmeticOperators when it creates an instance of this class
            throw new RuntimeException("KnowledgeBase not set on " + this);
         }
         long input = TermUtils.castToNumeric(args[0]).getLong();
         long output = input + 1;
         return new IntegerNumber(output);
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase kb) {
         this.kb = kb;
      }
   }

   /** ArithmeticOperator used to test that new arithmetic operators can be created using a static method. */
   public static class DummyArithmeticOperatorPublicConstructor implements ArithmeticOperator {
      KnowledgeBase kb;

      public static DummyArithmeticOperatorPublicConstructor getInstance() {
         return new DummyArithmeticOperatorPublicConstructor();
      }

      private DummyArithmeticOperatorPublicConstructor() {
         // private as want to test creation using getInstance static method
      }

      /**
       * @return an IntegerNumber with a value of the first input argument + 1
       */
      @Override
      public Numeric calculate(Term... args) {
         if (kb == null) {
            // setKnowledgeBase should be called by ArithmeticOperators when it creates an instance of this class
            throw new RuntimeException("KnowledgeBase not set on " + this);
         }
         long input = TermUtils.castToNumeric(args[0]).getLong();
         long output = input * 3;
         return new IntegerNumber(output);
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase kb) {
         this.kb = kb;
      }
   }
}
