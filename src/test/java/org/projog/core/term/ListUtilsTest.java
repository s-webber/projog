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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.decimalFraction;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;
import static org.projog.TestUtils.parseTerm;
import static org.projog.TestUtils.write;
import static org.projog.core.term.EmptyList.EMPTY_LIST;

import org.junit.Test;
import org.projog.TermFactory;
import org.projog.core.ProjogException;

public class ListUtilsTest {
   @Test
   public void testIsMember_True() {
      List list = TermFactory.list(atom("x"), atom("y"), atom("z"));
      assertTrue(ListUtils.isMember(atom("x"), list));
      assertTrue(ListUtils.isMember(atom("y"), list));
      assertTrue(ListUtils.isMember(atom("z"), list));
   }

   @Test
   public void testIsMember_Failure() {
      List list = TermFactory.list(atom("x"), atom("y"), atom("z"));
      assertFalse(ListUtils.isMember(atom("w"), list));
   }

   @Test
   public void testIsMember_EmptyList() {
      assertFalse(ListUtils.isMember(atom(), EMPTY_LIST));
   }

   @Test
   public void testIsMember_Variable() {
      Atom x = atom("x");
      List list = TermFactory.list(x, atom("y"), atom("z"));
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
   public void testIsMember_variable_tail() {
      Variable tail = variable();
      Term list = ListFactory.createList(new Term[] {atom("x"), atom("y"), atom("z")}, tail);

      assertTrue(ListUtils.isMember(atom("x"), list));
      assertEquals(".(x, .(y, .(z, X)))", list.toString());
      assertSame(tail, tail.getTerm());

      assertTrue(ListUtils.isMember(atom("y"), list));
      assertEquals(".(x, .(y, .(z, X)))", list.toString());
      assertSame(tail, tail.getTerm());

      assertTrue(ListUtils.isMember(atom("z"), list));
      assertEquals(".(x, .(y, .(z, X)))", list.toString());
      assertSame(tail, tail.getTerm());

      Atom q = atom("q");
      assertTrue(ListUtils.isMember(q, list));
      assertEquals(".(x, .(y, .(z, .(q, _))))", list.toString());
      assertNotSame(tail, tail.getTerm());
      assertSame(TermType.LIST, tail.getType());
      assertSame(q, tail.firstArgument());
      assertSame(TermType.VARIABLE, tail.secondArgument().getType());

      Term newList = list.getTerm();
      Term newTail = tail.secondArgument();
      Atom w = atom("w");
      assertTrue(ListUtils.isMember(w, newList));
      assertEquals(".(x, .(y, .(z, .(q, .(w, _)))))", newList.toString());
      assertNotSame(newTail, newTail.getTerm());
      assertSame(TermType.LIST, newTail.getType());
      assertSame(w, newTail.firstArgument());
      assertSame(TermType.VARIABLE, newTail.secondArgument().getType());
   }

   @Test
   public void testIsMember_atom_tail() {
      Atom tail = atom("test");
      Term list = ListFactory.createList(new Term[] {atom("x"), atom("y"), atom("z")}, tail);

      assertTrue(ListUtils.isMember(atom("x"), list));
      assertEquals(".(x, .(y, .(z, test)))", list.toString());
      assertSame(tail, tail.getTerm());

      assertTrue(ListUtils.isMember(atom("y"), list));
      assertEquals(".(x, .(y, .(z, test)))", list.toString());
      assertSame(tail, tail.getTerm());

      assertTrue(ListUtils.isMember(atom("z"), list));
      assertEquals(".(x, .(y, .(z, test)))", list.toString());
      assertSame(tail, tail.getTerm());

      try {
         assertTrue(ListUtils.isMember(atom("q"), list));
         fail();
      } catch (ProjogException e) {
         assertEquals("Expected empty list or variable but got: ATOM with value: test", e.getMessage());
      }
   }

   @Test
   public void testIsMember_InvalidArgumentList() {
      try {
         ListUtils.isMember(atom(), atom("a"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Expected list or empty list but got: ATOM with value: a", e.getMessage());
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
      assertNull(ListUtils.toJavaUtilList(decimalFraction()));
      assertNull(ListUtils.toJavaUtilList(new Variable()));
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
      assertNull(ListUtils.toSortedJavaUtilList(decimalFraction()));
      assertNull(ListUtils.toSortedJavaUtilList(new Variable()));
   }

   private Term[] createArguments() {
      return new Term[] {atom(), structure(), integerNumber(), decimalFraction(), variable()};
   }
}
