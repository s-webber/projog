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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.decimalFraction;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.list;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;
import static org.projog.TestUtils.assertStrictEquality;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.projog.TestUtils;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

/**
 * @see TermTest
 */
@RunWith(DataProviderRunner.class)
public class ListTest {
   private static final int LONG_LIST_SIZE = 1000;

   @Test
   public void testGetName() {
      List testList = new List(new Atom("a"), new Atom("b"));
      assertEquals(".", testList.getName());
   }

   @Test
   public void testToString() {
      List testList = new List(new Atom("a"), new Atom("b"));
      assertEquals(".(a, b)", testList.toString());
   }

   @Test
   public void testMutableListGetTerm() {
      List testList = new List(new Atom("a"), new Atom("b"));
      List l = testList.getTerm();
      assertSame(testList, l);
   }

   @Test
   public void testImmutableListGetTerm() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Atom c = new Atom("c");
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      Variable z = new Variable("Z");
      List sublist = ListFactory.createList(y, z);
      List originalList = ListFactory.createList(x, sublist);

      assertSame(originalList, originalList.getTerm());

      x.unify(a);

      assertSame(x, originalList.firstArgument());
      assertSame(sublist, originalList.secondArgument());

      List newList = originalList.getTerm();
      assertNotSame(originalList, newList);
      assertSame(a, newList.firstArgument());
      assertSame(sublist, newList.secondArgument());

      z.unify(c);

      newList = originalList.getTerm();
      assertNotSame(originalList, newList);
      assertSame(a, newList.firstArgument());
      assertNotSame(sublist, newList.secondArgument());
      assertSame(y, newList.secondArgument().firstArgument());
      assertSame(c, newList.secondArgument().secondArgument());

      x.backtrack();
      z.backtrack();
      y.unify(b);

      newList = originalList.getTerm();
      assertNotSame(originalList, newList);
      assertSame(x, newList.firstArgument());
      assertNotSame(sublist, newList.secondArgument());
      assertSame(b, newList.secondArgument().firstArgument());
      assertSame(z, newList.secondArgument().secondArgument());
   }

   @Test
   public void testGetType() {
      List testList = new List(new Atom("a"), new Atom("b"));
      assertSame(TermType.LIST, testList.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      List testList = new List(new Atom("a"), new Atom("b"));
      assertEquals(2, testList.getNumberOfArguments());
   }

   @Test
   public void testGetArgument() {
      Term head = new Atom("a");
      Term tail = new Atom("b");
      List testList = new List(head, tail);
      assertSame(head, testList.firstArgument());
      assertSame(head, testList.getArgument(0));
      assertSame(tail, testList.secondArgument());
      assertSame(tail, testList.getArgument(1));
   }

   @Test
   public void testThirdArgument() {
      List testList = new List(new Atom("a"), new Atom("b"));
      try {
         testList.thirdArgument();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: 2", e.getMessage());
      }
   }

   @Test
   public void testFourthArgument() {
      List testList = new List(new Atom("a"), new Atom("b"));
      try {
         testList.fourthArgument();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: 3", e.getMessage());
      }
   }

   @Test
   @DataProvider({"-1", "2", "3"})
   public void testGetArgumentIndexOutOfBounds(int index) {
      List testList = new List(new Atom("a"), new Atom("b"));
      try {
         testList.getArgument(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: " + index, e.getMessage());
      }
   }

   @Test
   public void testCopyNoVariableElements() {
      List testList = new List(new Atom("a"), new Atom("b"));
      assertSame(testList, testList.copy(null));
      assertSame(testList, testList.copy());
   }

   @Test
   public void testCopyVariableElements() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Variable X = new Variable("X");
      Variable Y = new Variable("Y");
      Term head = structure("p", X);

      List original = new List(head, Y); // [p(X), Y]

      assertSame(original, original.getTerm());

      Map<Variable, Term> sharedVariables = new HashMap<>();
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
      assertSame(copy2, copy2.copy());
      assertSame(copy2, copy2.getTerm());

      X.backtrack();
      Y.backtrack();

      assertStrictEquality(original, copy2, false);

      assertEquals(".(p(X), Y)", original.toString());
      assertEquals(".(p(a), b)", copy2.toString());
   }

   @Test
   public void testImmutableListCopy() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Atom c = new Atom("c");
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      Variable z = new Variable("Z");
      List sublist = ListFactory.createList(y, z);
      List originalList = ListFactory.createList(x, sublist);

      Map<Variable, Term> variables = new HashMap<>();
      List newList = originalList.copy(variables);
      assertNotSame(originalList, newList);
      assertSame(variables.get(x), newList.firstArgument());
      assertSame(variables.get(y), newList.secondArgument().firstArgument());
      assertSame(variables.get(z), newList.secondArgument().secondArgument());

      x.unify(a);

      assertSame(x, originalList.firstArgument());
      assertSame(sublist, originalList.secondArgument());

      variables = new HashMap<>();
      newList = originalList.copy(variables);
      assertNotSame(originalList, newList);
      assertSame(a, newList.firstArgument());
      assertSame(variables.get(y), newList.secondArgument().firstArgument());
      assertSame(variables.get(z), newList.secondArgument().secondArgument());

      z.unify(c);

      variables = new HashMap<>();
      newList = originalList.copy(variables);
      assertNotSame(originalList, newList);
      assertSame(a, newList.firstArgument());
      assertNotSame(sublist, newList.secondArgument());
      assertSame(variables.get(y), newList.secondArgument().firstArgument());
      assertSame(c, newList.secondArgument().secondArgument());

      x.backtrack();
      z.backtrack();
      y.unify(b);

      variables = new HashMap<>();
      newList = originalList.copy(variables);
      assertNotSame(originalList, newList);
      assertSame(variables.get(x), newList.firstArgument());
      assertNotSame(sublist, newList.secondArgument());
      assertSame(b, newList.secondArgument().firstArgument());
      assertSame(variables.get(z), newList.secondArgument().secondArgument());
   }

   @Test
   public void testImmutableHeadMutableTailCopy() {
      Variable x = new Variable("X");
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      List sublist = ListFactory.createList(a, b);
      List originalList = ListFactory.createList(x, sublist);

      Map<Variable, Term> variables = new HashMap<>();
      List newList = originalList.copy(variables);
      assertSame(variables.get(x), newList.firstArgument());
      assertSame(sublist, newList.secondArgument());
   }

   @Test
   public void testGetValueNoVariableElements() {
      List testList = new List(new Atom("a"), new Atom("b"));
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

   /** A long chain of lists where the majority of heads are immutable and all tails are not variables. */
   @Test
   public void testLongList() {
      StringBuilder bigListSyntaxBuilder1 = new StringBuilder("[");
      StringBuilder bigListSyntaxBuilder2 = new StringBuilder("[");
      for (int i = 0; i < LONG_LIST_SIZE; i++) {
         if (i != 0) {
            bigListSyntaxBuilder1.append(",");
            bigListSyntaxBuilder2.append(",");
         }
         bigListSyntaxBuilder1.append(i);
         // make one element in second list different than first
         if (i == LONG_LIST_SIZE / 4) {
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
      // NOTE important to test toString, equals and unify methods don't throw stackoverflow
      assertMatch(t1, t1, true);
      assertMatch(t1, t2, true);
      assertMatch(t1, t3, false);
   }

   /** A long chain of lists where all the heads are variables and all tails are not variables. */
   @Test
   public void testLongListWithMutableElements() {
      StringBuilder bigListSyntaxBuilder1 = new StringBuilder("[");
      StringBuilder bigListSyntaxBuilder2 = new StringBuilder("[");
      for (int i = 0; i < LONG_LIST_SIZE; i++) {
         if (i != 0) {
            bigListSyntaxBuilder1.append(",");
            bigListSyntaxBuilder2.append(",");
         }
         if (i == LONG_LIST_SIZE - 1) {
            bigListSyntaxBuilder1.append("X");
         } else {
            bigListSyntaxBuilder1.append(i);
         }
         if (i == LONG_LIST_SIZE - 2) {
            bigListSyntaxBuilder2.append("Y");
         } else {
            bigListSyntaxBuilder2.append(i);
         }
      }
      bigListSyntaxBuilder1.append("]");
      bigListSyntaxBuilder2.append("]");
      String bigListSyntax1 = bigListSyntaxBuilder1.toString();
      String bigListSyntax2 = bigListSyntaxBuilder2.toString();
      List t1 = (List) TestUtils.parseSentence(bigListSyntax1 + ".");
      List t2 = (List) TestUtils.parseSentence(bigListSyntax2 + ".");
      assertSame(t1, t1.getTerm());
      assertSame(t2, t2.getTerm());
      assertEquals(bigListSyntax1, TestUtils.write(t1));
      assertEquals(bigListSyntax2, TestUtils.write(t2));
      assertStrictEquality(t1, t2, false);
      assertTrue(t1.unify(t2));
      assertStrictEquality(t1, t2, true);
      assertNotSame(t1, t1.getTerm());
      assertNotSame(t2, t2.getTerm());
      assertStrictEquality(t1.getTerm(), t2.getTerm(), true);
      t1.backtrack();
      t2.backtrack();
      assertStrictEquality(t1, t2, false);

      List t1Copy = t1.copy(new HashMap<>());
      assertEquals(bigListSyntax1, TestUtils.write(t1Copy));
      assertStrictEquality(t1, t1Copy, false);
      List t2Copy = t2.copy(new HashMap<>());
      assertEquals(bigListSyntax2, TestUtils.write(t2Copy));
      assertStrictEquality(t2, t2Copy, false);
   }

   /**
    * A long chain of lists where all heads and tails are variables.
    * <p>
    * Heads are unified to atoms and tails are unified with the next list in the chain.
    */
   @Test
   public void testLongListWithVariableTails() {
      Term input = EmptyList.EMPTY_LIST;
      Term[] atoms = new Term[LONG_LIST_SIZE];
      Term[] lists = new Term[LONG_LIST_SIZE];
      for (int i = 0; i < LONG_LIST_SIZE; i++) {
         Atom atom = new Atom("atom" + i);
         atoms[i] = atom;
         Variable head = new Variable("H" + i);
         head.unify(atom);
         Variable tail = new Variable("T" + i);
         tail.unify(input);
         input = new List(head, tail);
         lists[i] = input;
      }

      Term output = input.getTerm();
      assertNotSame(input, output);
      assertStrictEquality(input, output, true);
      for (int i = LONG_LIST_SIZE - 1; i > -1; i--) {
         assertSame(List.class, output.getClass());
         assertSame(atoms[i], output.firstArgument());
         output = output.secondArgument();
      }
      assertSame(EmptyList.EMPTY_LIST, output);

      Term tail = input.secondArgument().getBound();
      input.backtrack();
      assertSame(TermType.VARIABLE, input.firstArgument().getType());
      assertEquals("H" + (LONG_LIST_SIZE - 1), input.firstArgument().toString());
      assertSame(TermType.VARIABLE, input.secondArgument().getType());
      assertEquals("T" + (LONG_LIST_SIZE - 1), input.secondArgument().toString());

      boolean first = true;
      for (int i = LONG_LIST_SIZE - 2; i > -1; i--) {
         assertSame(first ? List.class : Variable.class, tail.getClass());
         first = false;
         assertSame(TermType.LIST, tail.getType());
         assertSame(atoms[i], tail.firstArgument().getBound());
         tail = tail.secondArgument();
      }
      assertSame(Variable.class, tail.getClass());
      assertSame(EmptyList.EMPTY_LIST, tail.getBound());
   }

   @Test
   public void testIsImmutable() {
      Atom atom = atom("a");
      IntegerNumber number = integerNumber(42);
      Variable variable1 = variable("X");
      Variable variable2 = variable("Y");
      Term immutableStructure = structure("p", atom("c"));
      Term mutableStructure = structure("p", variable("Z"));

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
      assertSame(v, l1.secondArgument().firstArgument().secondArgument());
      assertSame(a, l2.secondArgument().firstArgument().secondArgument());
   }

   @Test
   public void testHashCode() {
      Atom w = new Atom("w");
      Atom x = new Atom("x");
      Atom y = new Atom("y");
      Atom z = new Atom("z");
      List l1 = new List(x, new List(y, z));

      assertEquals(l1.hashCode(), new List(x, new List(y, z)).hashCode());
      assertEquals(l1.hashCode(), new List(new Atom("x"), new List(new Atom("y"), new Atom("z"))).hashCode());

      // assert lists of same length and elements do not have same hashcode if order is different
      assertNotEquals(l1.hashCode(), new List(x, new List(z, y)).hashCode());
      assertNotEquals(l1.hashCode(), new List(z, new List(y, x)).hashCode());
      assertNotEquals(l1.hashCode(), new List(z, new List(x, y)).hashCode());
      assertNotEquals(l1.hashCode(), new List(y, new List(y, z)).hashCode());
      assertNotEquals(l1.hashCode(), new List(y, new List(z, y)).hashCode());

      // assert lists of same length do not have same hashcode if elements are different
      assertNotEquals(l1.hashCode(), new List(w, new List(y, z)).hashCode());
      assertNotEquals(l1.hashCode(), new List(x, new List(w, z)).hashCode());
      assertNotEquals(l1.hashCode(), new List(x, new List(y, w)).hashCode());
      assertNotEquals(l1.hashCode(), new List(x, new List(y, new Variable("z"))).hashCode());

      // assert lists of different length do not have same hashcode
      assertNotEquals(l1.hashCode(), new List(x, y).hashCode());
   }

   private void assertMatch(List l1, List l2, boolean expectMatch) {
      // NOTE important to test that toString, equals, hashCode and unify methods don't throw stackoverflow
      assertStrictEquality(l1, l2, expectMatch);
      assertEquals(expectMatch, l1.equals(l2));
      assertEquals(expectMatch, l1.hashCode() == l2.hashCode());
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
