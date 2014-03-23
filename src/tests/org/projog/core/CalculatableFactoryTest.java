/*
 * Copyright 2013 S Webber
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.ADD_CALCULATABLE_KEY;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.doubleNumber;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

public class CalculatableFactoryTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testGetNumericIntegerNumber() {
      CalculatableFactory cf = getCalculatableFactory();
      IntegerNumber i = integerNumber(1);
      assertSame(i, cf.getNumeric(i));
   }

   @Test
   public void testGetNumericDoubleNumber() {
      CalculatableFactory cf = getCalculatableFactory();
      DoubleNumber d = doubleNumber(17.6);
      assertSame(d, cf.getNumeric(d));
   }

   @Test
   public void testGetNumericException() {
      CalculatableFactory cf = getCalculatableFactory();
      try {
         cf.getNumeric(variable("X"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Can't get Numeric for term: X of type: NAMED_VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testGetNumericPredicate() {
      CalculatableFactory cf = getCalculatableFactory();
      String dummyCalculatableName = "dummy_calculatable";
      String dummyCalculatableClassName = new DummyCalculatable().getClass().getName();
      int input = 7;
      Structure p = structure(dummyCalculatableName, integerNumber(input));
      Term evaluateArg1 = atom(dummyCalculatableName);
      Term evaluateArg2 = atom(dummyCalculatableClassName);

      // try to use calculatable by a name that there is no match for (expect exception)
      try {
         cf.getNumeric(p);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find calculatable: dummy_calculatable", e.getMessage());
      }

      // add new calculatable
      assertTrue(cf.evaluate(evaluateArg1, evaluateArg2));

      // assert that the factory is now using the newly added calculatable
      Numeric n = cf.getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(input + 1, n.getInt());

      // attempt to add calculatable again 
      // (should fail now a calculatable with the same name already exists in the factoty)
      try {
         cf.evaluate(evaluateArg1, evaluateArg2);
         fail("could re-add calculatable named: " + evaluateArg1);
      } catch (ProjogException e) {
         // expected;
      }
   }

   private CalculatableFactory getCalculatableFactory() {
      PredicateFactory ef = kb.getPredicateFactory(ADD_CALCULATABLE_KEY);
      assertSame(CalculatableFactory.class, ef.getClass());
      assertTrue(ef instanceof AbstractSingletonPredicate);
      return (CalculatableFactory) ef;
   }

   /**
    * Calculatable used to test that new calculatables can be added to the factory.
    */
   public static class DummyCalculatable implements Calculatable {
      /**
       * @return an IntegerNumber with a value of the first input argument + 1
       */
      @Override
      public Numeric calculate(KnowledgeBase kb, Term[] args) {
         int input = TermUtils.castToNumeric(args[0]).getInt();
         int output = input + 1;
         return new IntegerNumber(output);
      }
   }
}