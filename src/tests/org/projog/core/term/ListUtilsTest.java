package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.doubleNumber;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.parseTerm;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;
import static org.projog.TestUtils.write;
import static org.projog.core.term.AnonymousVariable.ANONYMOUS_VARIABLE;
import static org.projog.core.term.EmptyList.EMPTY_LIST;

import org.junit.Test;
import org.projog.TestUtils;

public class ListUtilsTest {
   @Test
   public void testIsMember_True() {
      List list = TestUtils.list(atom("x"), atom("y"), atom("z"));
      assertTrue(ListUtils.isMember(atom("x"), list));
      assertTrue(ListUtils.isMember(atom("y"), list));
      assertTrue(ListUtils.isMember(atom("z"), list));
   }

   @Test
   public void testIsMember_Failure() {
      List list = TestUtils.list(atom("x"), atom("y"), atom("z"));
      assertFalse(ListUtils.isMember(atom("w"), list));
   }

   @Test
   public void testIsMember_EmptyList() {
      assertFalse(ListUtils.isMember(atom(), EMPTY_LIST));
   }

   @Test
   public void testIsMember_Variable() {
      Atom x = atom("x");
      List list = TestUtils.list(x, atom("y"), atom("z"));
      Variable v = variable();
      assertTrue(ListUtils.isMember(v, list));
      assertSame(x, v.getTerm());
   }

   @Test
   public void testIsMember_VariablesAsArgumentsOfStructures() {
      Term list = parseTerm("[p(a, B, 2),p(q, b, C),p(A, b, 5)]");
      Term element = parseTerm("p(X,b,5)");
      assertTrue(ListUtils.isMember(element, list));
      assertEquals("[p(a, B, 2),p(q, b, 5),p(A, b, 5)]", write(list));
      assertEquals("p(q, b, 5)", write(element));
   }

   @Test
   public void testIsMember_InvalidArgumentList() {
      try {
         ListUtils.isMember(atom(), atom("a"));
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Expected list but got: a", e.getMessage());
      }
   }

   @Test
   public void testToJavaUtilList() {
      final Term[] arguments = createArguments();
      final List projogList = (List) ListFactory.createList(arguments);
      final java.util.List<Term> javaUtilList = ListUtils.toJavaUtilList(projogList);
      assertEquals(arguments.length, javaUtilList.size());
      for (int i = 0; i < arguments.length; i++) {
         assertSame(arguments[i], javaUtilList.get(i));
      }
   }

   @Test
   public void testToJavaUtilList_PartialList() {
      final List projogList = (List) ListFactory.createList(createArguments(), atom("tail"));
      assertNull(ListUtils.toJavaUtilList(projogList));
   }

   @Test
   public void testToJavaUtilList_EmptyList() {
      final java.util.List<Term> javaUtilList = ListUtils.toJavaUtilList(EMPTY_LIST);
      assertTrue(javaUtilList.isEmpty());
   }

   @Test
   public void testToJavaUtilList_NonListArguments() {
      assertNull(ListUtils.toJavaUtilList(variable()));
      assertNull(ListUtils.toJavaUtilList(atom()));
      assertNull(ListUtils.toJavaUtilList(structure()));
      assertNull(ListUtils.toJavaUtilList(integerNumber()));
      assertNull(ListUtils.toJavaUtilList(doubleNumber()));
      assertNull(ListUtils.toJavaUtilList(ANONYMOUS_VARIABLE));
   }

   @Test
   public void testToSortedJavaUtilList() {
      Atom z = atom("z");
      Atom a = atom("a");
      Atom h = atom("h");
      Atom q = atom("q");
      // include multiple 'a's to test duplicates are not removed
      final List projogList = (List) ListFactory.createList(new Term[] {z, a, a, h, a, q});
      final java.util.List<Term> javaUtilList = ListUtils.toSortedJavaUtilList(projogList);
      assertEquals(6, javaUtilList.size());
      assertSame(a, javaUtilList.get(0));
      assertSame(a, javaUtilList.get(1));
      assertSame(a, javaUtilList.get(2));
      assertSame(h, javaUtilList.get(3));
      assertSame(q, javaUtilList.get(4));
      assertSame(z, javaUtilList.get(5));
   }

   @Test
   public void testToSortedJavaUtilList_EmptyList() {
      final java.util.List<Term> javaUtilList = ListUtils.toSortedJavaUtilList(EMPTY_LIST);
      assertTrue(javaUtilList.isEmpty());
   }

   @Test
   public void testToSortedJavaUtilList_EmptyList_NonListArguments() {
      assertNull(ListUtils.toSortedJavaUtilList(variable()));
      assertNull(ListUtils.toSortedJavaUtilList(atom()));
      assertNull(ListUtils.toSortedJavaUtilList(structure()));
      assertNull(ListUtils.toSortedJavaUtilList(integerNumber()));
      assertNull(ListUtils.toSortedJavaUtilList(doubleNumber()));
      assertNull(ListUtils.toSortedJavaUtilList(ANONYMOUS_VARIABLE));
   }

   private Term[] createArguments() {
      return new Term[] {atom(), structure(), integerNumber(), doubleNumber(), variable()};
   }
}
