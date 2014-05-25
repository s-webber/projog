package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.doubleNumber;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import org.junit.Test;
import org.projog.TestUtils;
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
      CalculatableFactory cf = createCalculatableFactory();
      IntegerNumber i = integerNumber(1);
      assertSame(i, cf.getNumeric(i));
   }

   @Test
   public void testGetNumericDoubleNumber() {
      CalculatableFactory cf = createCalculatableFactory();
      DoubleNumber d = doubleNumber(17.6);
      assertSame(d, cf.getNumeric(d));
   }

   @Test
   public void testGetNumericException() {
      CalculatableFactory cf = createCalculatableFactory();
      try {
         cf.getNumeric(variable("X"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Can't get Numeric for term: X of type: NAMED_VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testGetNumericPredicate() {
      CalculatableFactory cf = createCalculatableFactory();
      String dummyCalculatableName = "dummy_calculatable";
      int input = 7;
      Structure p = structure(dummyCalculatableName, integerNumber(input));

      // try to use calculatable by a name that there is no match for (expect exception)
      try {
         cf.getNumeric(p);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find calculatable: dummy_calculatable", e.getMessage());
      }

      // add new calculatable
      cf.addCalculatable(dummyCalculatableName, new DummyCalculatable());

      // assert that the factory is now using the newly added calculatable
      Numeric n = cf.getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(input + 1, n.getInt());

      // attempt to add calculatable again 
      // (should fail now a calculatable with the same name already exists in the factoty)
      try {
         cf.addCalculatable(dummyCalculatableName, new DummyCalculatable());
         fail("could re-add calculatable named: " + dummyCalculatableName);
      } catch (ProjogException e) {
         // expected;
      }
   }

   private CalculatableFactory createCalculatableFactory() {
      return new CalculatableFactory(kb);
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