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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

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
   public void testCreateListOfLengthZero() {
      assertSame(EmptyList.EMPTY_LIST, ListFactory.createListOfLength(0));
   }

   @Test
   public void testCreateListOfLengthOne() {
      Term t = ListFactory.createListOfLength(1);
      assertSame(List.class, t.getClass());
      assertTrue(t.getArgument(0).getType().isVariable());
      assertSame(EmptyList.EMPTY_LIST, t.getArgument(1));
      assertEquals(".(E0, [])", t.toString());
   }

   @Test
   public void testCreateListOfLengthThree() {
      Term t = ListFactory.createListOfLength(3);
      assertSame(List.class, t.getClass());
      assertTrue(t.getArgument(0).getType().isVariable());
      assertSame(List.class, t.getArgument(1).getClass());
      assertEquals(".(E0, .(E1, .(E2, [])))", t.toString());
   }

   @Test
   public void testCreateListFromTwoAtoms() {
      Atom head = atom("a");
      Atom tail = atom("b");
      List t = ListFactory.createList(head, tail);
      assertSame(List.class, t.getClass());
      assertSame(head, t.getArgument(0));
      assertSame(tail, t.getArgument(1));
      assertTrue(t.isImmutable());
   }

   @Test
   public void testCreateListWithVariableHead() {
      Variable head = variable("X");
      Atom tail = atom("b");
      Term t = ListFactory.createList(head, tail);
      assertSame(List.class, t.getClass());
      assertSame(head, t.getArgument(0));
      assertSame(tail, t.getArgument(1));
      assertFalse(t.isImmutable());
   }

   @Test
   public void testCreateListWithVariableTail() {
      Atom head = atom("a");
      Variable tail = variable("Y");
      Term t = ListFactory.createList(head, tail);
      assertSame(List.class, t.getClass());
      assertSame(head, t.getArgument(0));
      assertSame(tail, t.getArgument(1));
      assertFalse(t.isImmutable());
   }

   @Test
   public void testCreateListWithVariableHeadAndTail() {
      Variable head = variable("X");
      Variable tail = variable("Y");
      Term t = ListFactory.createList(head, tail);
      assertSame(List.class, t.getClass());
      assertSame(head, t.getArgument(0));
      assertSame(tail, t.getArgument(1));
      assertFalse(t.isImmutable());
   }

   private Term[] createArguments() {
      return new Term[] {atom(), structure(), integerNumber(), decimalFraction(), variable()};
   }

   private void testIsList(Term l) {
      assertEquals(".", l.getName());
      assertEquals(TermType.LIST, l.getType());
      assertEquals(2, l.getNumberOfArguments());
   }
}
