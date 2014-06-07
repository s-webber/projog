package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.doubleNumber;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;
import static org.projog.core.term.AnonymousVariable.ANONYMOUS_VARIABLE;
import static org.projog.core.term.EmptyList.EMPTY_LIST;

import org.junit.Test;

public class ListUtilsTest {
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

   private Term[] createArguments() {
      return new Term[] {atom(), structure(), integerNumber(), doubleNumber(), variable()};
   }
}
