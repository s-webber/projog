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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class ListFactoryTest {
   @Test
   public void testCreationWithoutTail() {
      final Term[] args = createArguments();
      Term l = ListFactory.createList(args);

      for (Term arg : args) {
         testIsList(l);
         assertEquals(arg, l.getArgument(0));
         l = l.getArgument(1);
      }

      assertSame(TermType.EMPTY_LIST, l.getType());
      assertSame(EmptyList.EMPTY_LIST, l);
   }

   @Test
   public void testCreationWithTail() {
      final Term[] args = createArguments();
      final Term tail = new Atom("tail");
      Term l = ListFactory.createList(args, tail);

      for (Term arg : args) {
         testIsList(l);
         assertEquals(arg, l.getArgument(0));
         l = l.getArgument(1);
      }

      assertSame(tail, l);
   }

   /** Check {@link ListFactory#createList(Collection)} works the same as {@link ListFactory#createList(Term[])} */
   @Test
   public void testCreationWithJavaCollection() {
      final Term[] args = createArguments();
      final Collection<Term> c = Arrays.asList(args);
      final Term listFromArray = ListFactory.createList(args);
      final Term listFromCollection = ListFactory.createList(c);
      assertTrue(listFromCollection.strictEquality(listFromArray));
   }

   @Test
   public void testToJavaUtilList() {
      final Term[] arguments = createArguments();
      final List projogList = (List) ListFactory.createList(arguments);
      final java.util.List<Term> javaUtilList = ListFactory.toJavaUtilList(projogList);
      assertEquals(arguments.length, javaUtilList.size());
      for (int i = 0; i < arguments.length; i++) {
         assertSame(arguments[i], javaUtilList.get(i));
      }
   }

   @Test
   public void testToJavaUtilList_PartialList() {
      final List projogList = (List) ListFactory.createList(createArguments(), atom("tail"));
      assertNull(ListFactory.toJavaUtilList(projogList));
   }

   @Test
   public void testToJavaUtilList_EmptyList() {
      final java.util.List<Term> javaUtilList = ListFactory.toJavaUtilList(EMPTY_LIST);
      assertTrue(javaUtilList.isEmpty());
   }

   @Test
   public void testToJavaUtilList_NonListArguments() {
      assertNull(ListFactory.toJavaUtilList(variable()));
      assertNull(ListFactory.toJavaUtilList(atom()));
      assertNull(ListFactory.toJavaUtilList(structure()));
      assertNull(ListFactory.toJavaUtilList(integerNumber()));
      assertNull(ListFactory.toJavaUtilList(doubleNumber()));
      assertNull(ListFactory.toJavaUtilList(ANONYMOUS_VARIABLE));
   }

   private Term[] createArguments() {
      return new Term[] {atom(), structure(), integerNumber(), doubleNumber(), variable()};
   }

   private void testIsList(Term l) {
      assertEquals(".", l.getName());
      assertEquals(TermType.LIST, l.getType());
      assertEquals(2, l.getNumberOfArguments());
   }
}