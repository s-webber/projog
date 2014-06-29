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

public class CalculatablesTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testGetNumericIntegerNumber() {
      Calculatables c = createCalculatables();
      IntegerNumber i = integerNumber(1);
      assertSame(i, c.getNumeric(i));
   }

   @Test
   public void testGetNumericDoubleNumber() {
      Calculatables c = createCalculatables();
      DoubleNumber d = doubleNumber(17.6);
      assertSame(d, c.getNumeric(d));
   }

   @Test
   public void testGetNumericException() {
      Calculatables c = createCalculatables();
      try {
         c.getNumeric(variable("X"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot get Numeric for term: X of type: NAMED_VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testGetNumericPredicate() {
      Calculatables c = createCalculatables();
      String dummyCalculatableName = "dummy_calculatable";
      PredicateKey key = new PredicateKey(dummyCalculatableName, 1);
      int input = 7;
      Structure p = structure(dummyCalculatableName, integerNumber(input));

      // try to use calculatable by a name that there is no match for (expect exception)
      try {
         c.getNumeric(p);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find calculatable: dummy_calculatable/1", e.getMessage());
      }

      // add new calculatable
      c.addCalculatable(key, new DummyCalculatable());

      // assert that the factory is now using the newly added calculatable
      Numeric n = c.getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(input + 1, n.getLong());

      // attempt to add calculatable again 
      // (should fail now a calculatable with the same name already exists in the factoty)
      try {
         c.addCalculatable(key, new DummyCalculatable());
         fail("could re-add calculatable named: " + dummyCalculatableName);
      } catch (ProjogException e) {
         // expected;
      }
   }

   private Calculatables createCalculatables() {
      return new Calculatables(kb);
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
         long input = TermUtils.castToNumeric(args[0]).getLong();
         long output = input + 1;
         return new IntegerNumber(output);
      }
   }
}