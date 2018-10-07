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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.assertStrictEquality;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.list;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.projog.TestUtils;

/**
 * @see TermTest
 */
public class ListTest {
   private static final Term head = new Atom("a");
   private static final Term tail = new Atom("b");
   private static final List testList = new List(head, tail);

   @Test
   public void testGetName() {
      assertEquals(".", testList.getName());
   }

   @Test
   public void testToString() {
      assertEquals(".(a, b)", testList.toString());
   }

   @Test
   public void testGetTerm() {
      List l = testList.getTerm();
      assertSame(testList, l);
   }

   @Test
   public void testGetType() {
      assertSame(TermType.LIST, testList.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      assertEquals(2, testList.getNumberOfArguments());
   }

   @Test
   public void testGetArgument() {
      assertSame(head, testList.getArgument(0));
      assertSame(tail, testList.getArgument(1));
   }

   @Test
   public void testGetArgs() {
      try {
         testList.getArgs();
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testCopyNoVariableElements() {
      assertSame(testList, testList.copy(null));
   }

   @Test
   public void testCopyVariableElements() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Variable X = new Variable("X");
      Variable Y = new Variable("Y");
      Structure head = structure("p", X);

      List original = new List(head, Y); // [p(X), Y]

      assertSame(original, original.getTerm());

      Map<Variable, Variable> sharedVariables = new HashMap<Variable, Variable>();
      List copy1 = original.copy(sharedVariables);
      assertNotSame(original, copy1);
      assertStrictEquality(original, copy1, false);
      assertEquals(2, sharedVariables.size());
      assertTrue(sharedVariables.containsKey(X));
      assertTrue(sharedVariables.containsKey(Y));
      assertEquals(original.toString(), copy1.toString());

      assertTrue(X.unify(a));
      assertTrue(Y.unify(b));

      List copy2 = original.copy(null);
      assertNotSame(original, copy2);
      assertStrictEquality(original, copy2, true);
      assertEquals(original.toString(), copy2.toString());
      assertSame(copy2, copy2.copy(null));
      assertSame(copy2, copy2.getTerm());

      X.backtrack();
      Y.backtrack();

      assertStrictEquality(original, copy2, false);

      assertEquals(".(p(X), Y)", original.toString());
      assertEquals(".(p(a), b)", copy2.toString());
   }

   @Test
   public void testGetValueNoVariableElements() {
      assertSame(testList, testList.getTerm());
   }

   @Test
   public void testListWithVariableArguments() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Variable X = new Variable("X");
      Variable Y = new Variable("Y");
      List l1 = new List(a, Y);
      List l2 = new List(X, b);

      assertStrictEqualityUnifyAndBacktrack(l1, l2);
      assertStrictEqualityUnifyAndBacktrack(l2, l1);
   }

   @Test
   public void testUnifyWhenBothListsHaveVariableArguments_1() {
      // [x, Y]
      List l1 = new List(new Atom("x"), new Variable("Y"));
      // [X, y]
      List l2 = new List(new Variable("X"), new Atom("y"));
      assertTrue(l1.unify(l2));
      assertEquals(".(x, y)", l1.toString());
      assertEquals(l1.toString(), l2.toString());
   }

   @Test
   public void testUnifyWhenBothListsHaveVariableArguments_2() {
      // [x, z]
      List l1 = new List(new Atom("x"), new Atom("z"));
      // [X, y]
      List l2 = new List(new Variable("X"), new Atom("y"));
      assertFalse(l1.unify(l2));
      assertEquals(".(x, z)", l1.toString());
      // Note: following is expected quirk - list doesn't automatically backtrack on failure
      assertEquals(".(x, y)", l2.toString());

      l2.backtrack();
      assertEquals(".(X, y)", l2.toString());
   }

   @Test
   public void testUnifyWhenBothListsHaveVariableArguments_3() {
      // [X, z]
      List l1 = new List(new Variable("X"), new Atom("z"));
      // [x, y]
      List l2 = new List(new Atom("x"), new Atom("y"));
      assertFalse(l1.unify(l2));
      // Note: following is expected quirk - list doesn't automatically backtrack on failure
      assertEquals(".(x, z)", l1.toString());
      assertEquals(".(x, y)", l2.toString());

      l1.backtrack();
      assertEquals(".(X, z)", l1.toString());
   }

   @Test
   public void testLongList() {
      StringBuilder bigListSyntaxBuilder1 = new StringBuilder("[");
      StringBuilder bigListSyntaxBuilder2 = new StringBuilder("[");
      for (int i = 0; i < 10000; i++) {
         if (i != 0) {
            bigListSyntaxBuilder1.append(",");
            bigListSyntaxBuilder2.append(",");
         }
         bigListSyntaxBuilder1.append(i);
         // make one element in second list different than first
         if (i == 789) {
            bigListSyntaxBuilder2.append(i - 1);
         } else {
            bigListSyntaxBuilder2.append(i);
         }
      }
      bigListSyntaxBuilder1.append("]");
      bigListSyntaxBuilder2.append("]");
      String bigListSyntax1 = bigListSyntaxBuilder1.toString();
      String bigListSyntax2 = bigListSyntaxBuilder2.toString();
      List t1 = (List) TestUtils.parseSentence(bigListSyntax1 + ".");
      List t2 = (List) TestUtils.parseSentence(bigListSyntax1 + ".");
      List t3 = (List) TestUtils.parseSentence(bigListSyntax2 + ".");
      assertNotSame(t1, t2);
      // NOTE important to test write method doesn't throw stackoverflow
      assertEquals(bigListSyntax1, TestUtils.write(t1));
      assertEquals(bigListSyntax2, TestUtils.write(t3));
      assertMatch(t1, t1, true);
      assertMatch(t1, t2, true);
      assertMatch(t1, t3, false);
   }

   @Test
   public void testIsImmutable() {
      Atom atom = atom("a");
      IntegerNumber number = integerNumber(42);
      Variable variable1 = variable("X");
      Variable variable2 = variable("Y");
      Structure immutableStructure = structure("p", atom("c"));
      Structure mutableStructure = structure("p", variable("Z"));

      // assert when both terms are mutable
      assertTrue(new List(atom, number).isImmutable());
      assertTrue(new List(atom, atom).isImmutable());
      assertTrue(new List(immutableStructure, number).isImmutable());
      assertTrue(new List(atom, immutableStructure).isImmutable());
      assertTrue(new List(immutableStructure, immutableStructure).isImmutable());

      // assert when one at least one term is a variable
      assertFalse(new List(variable1, variable2).isImmutable());
      assertFalse(new List(variable1, variable1).isImmutable());
      assertFalse(new List(atom, variable2).isImmutable());
      assertFalse(new List(variable1, number).isImmutable());
      assertFalse(new List(immutableStructure, variable2).isImmutable());
      assertFalse(new List(variable1, immutableStructure).isImmutable());

      // assert when one term is a mutable structure
      assertFalse(new List(atom, mutableStructure).isImmutable());
      assertFalse(new List(mutableStructure, number).isImmutable());
      assertFalse(new List(mutableStructure, immutableStructure).isImmutable());
      assertFalse(new List(immutableStructure, mutableStructure).isImmutable());
      assertFalse(new List(mutableStructure, number).isImmutable());
      assertFalse(new List(mutableStructure, mutableStructure).isImmutable());
   }

   @Test
   public void testIsImmutableAfterCopy() {
      Variable v = variable("X");
      Atom a = atom("test");
      List l1 = list(atom(), structure("p", atom(), v, integerNumber()), list(integerNumber(), decimalFraction()));
      assertFalse(l1.isImmutable());
      v.unify(a);
      List l2 = l1.copy(null);
      assertFalse(l1.isImmutable());
      assertTrue(l2.toString(), l2.isImmutable());
      assertSame(v, l1.getArgument(1).getArgument(0).getArgument(1));
      assertSame(a, l2.getArgument(1).getArgument(0).getArgument(1));
   }

   private void assertMatch(List l1, List l2, boolean expectMatch) {
      // NOTE important to test toString, strictEquality and unify
      // methods doesn't throw stackoverflow
      assertEquals(expectMatch, l1.strictEquality(l2));
      assertEquals(expectMatch, l1.unify(l2));
      assertEquals(expectMatch, l1.toString().equals(l2.toString()));
   }

   private void assertStrictEqualityUnifyAndBacktrack(List l1, List l2) {
      assertStrictEquality(l1, l2, false);
      assertSame(l1, l1.getTerm());
      assertSame(l2, l2.getTerm());

      l1.unify(l2);

      assertStrictEquality(l1, l2, true);
      assertNotSame(l1, l1.getTerm());
      assertNotSame(l2, l2.getTerm());

      l1.backtrack();
      l2.backtrack();

      assertStrictEquality(l1, l2, false);
      assertSame(l1, l1.getTerm());
      assertSame(l2, l2.getTerm());
   }
}
