package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.core.term.AnonymousVariable.ANONYMOUS_VARIABLE;

import org.junit.Test;

/**
 * @see TermTest
 */
public class AnonymousVariableTest {
   @Test
   public void testGetName() {
      try {
         ANONYMOUS_VARIABLE.getName();
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testToString() {
      assertEquals("_", ANONYMOUS_VARIABLE.toString());
   }

   @Test
   public void testGetType() {
      assertSame(TermType.ANONYMOUS_VARIABLE, ANONYMOUS_VARIABLE.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      assertEquals(0, ANONYMOUS_VARIABLE.getNumberOfArguments());
   }

   @Test
   public void testGetArgument() {
      try {
         ANONYMOUS_VARIABLE.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testGetArgs() {
      try {
         ANONYMOUS_VARIABLE.getArgs();
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testCopy() {
      assertSame(ANONYMOUS_VARIABLE, ANONYMOUS_VARIABLE.copy(null));
   }

   @Test
   public void testGetValue() {
      AnonymousVariable a = ANONYMOUS_VARIABLE.getTerm();
      assertSame(ANONYMOUS_VARIABLE, a);
   }

   @Test
   public void testStrictEqualityWithSelf() {
      assertFalse(ANONYMOUS_VARIABLE.strictEquality(ANONYMOUS_VARIABLE));
   }

   @Test
   public void testUnifyWithSelf() {
      assertTrue(ANONYMOUS_VARIABLE.unify(ANONYMOUS_VARIABLE));
   }

   @Test
   public void testIsImmutable() {
      assertTrue(ANONYMOUS_VARIABLE.isImmutable());
   }

   @Test
   public void testBacktrack() {
      // just check no exceptions thrown
      ANONYMOUS_VARIABLE.backtrack();
   }
}