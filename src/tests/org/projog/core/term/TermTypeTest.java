package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TermTypeTest {
   @Test
   public void testIsNumeric() {
      assertTrue(TermType.DOUBLE.isNumeric());
      assertTrue(TermType.INTEGER.isNumeric());

      assertFalse(TermType.ATOM.isNumeric());
      assertFalse(TermType.EMPTY_LIST.isNumeric());
      assertFalse(TermType.LIST.isNumeric());
      assertFalse(TermType.STRUCTURE.isNumeric());
      assertFalse(TermType.ANONYMOUS_VARIABLE.isNumeric());
      assertFalse(TermType.NAMED_VARIABLE.isNumeric());
   }

   @Test
   public void testIsStructure() {
      assertTrue(TermType.EMPTY_LIST.isStructure());
      assertTrue(TermType.LIST.isStructure());
      assertTrue(TermType.STRUCTURE.isStructure());

      assertFalse(TermType.DOUBLE.isStructure());
      assertFalse(TermType.INTEGER.isStructure());
      assertFalse(TermType.ATOM.isStructure());
      assertFalse(TermType.ANONYMOUS_VARIABLE.isStructure());
      assertFalse(TermType.NAMED_VARIABLE.isStructure());
   }

   @Test
   public void testIsVariable() {
      assertTrue(TermType.ANONYMOUS_VARIABLE.isVariable());
      assertTrue(TermType.NAMED_VARIABLE.isVariable());

      assertFalse(TermType.DOUBLE.isVariable());
      assertFalse(TermType.INTEGER.isVariable());
      assertFalse(TermType.ATOM.isVariable());
      assertFalse(TermType.EMPTY_LIST.isVariable());
      assertFalse(TermType.LIST.isVariable());
      assertFalse(TermType.STRUCTURE.isVariable());
   }

   @Test
   public void testGetPrecedence() {
      assertEquals(1, TermType.ANONYMOUS_VARIABLE.getPrecedence());
      assertEquals(2, TermType.NAMED_VARIABLE.getPrecedence());
      assertEquals(3, TermType.DOUBLE.getPrecedence());
      assertEquals(4, TermType.INTEGER.getPrecedence());
      assertEquals(5, TermType.ATOM.getPrecedence());
      // all compound structures share the same precedence
      assertEquals(6, TermType.STRUCTURE.getPrecedence());
      assertEquals(6, TermType.EMPTY_LIST.getPrecedence());
      assertEquals(6, TermType.LIST.getPrecedence());
   }
}