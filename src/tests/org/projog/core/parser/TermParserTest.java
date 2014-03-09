/*
 * Copyright 2013 S Webber
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
package org.projog.core.parser;

import static org.projog.TestUtils.parseTerm;
import junit.framework.TestCase;

import org.projog.core.term.AnonymousVariable;
import org.projog.core.term.Atom;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.List;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

public class TermParserTest extends TestCase {
   public void testAtoms() {
      testNonVariableTerm(new Atom("x"), "x");
      testNonVariableTerm(new Atom("xyz"), "xyz");
      testNonVariableTerm(new Atom("xYz"), "xYz");
      testNonVariableTerm(new Atom("xYZ"), "xYZ");
      testNonVariableTerm(new Atom("x_1"), "x_1");
      testNonVariableTerm(new Atom("xttRytf_uiu"), "xttRytf_uiu");
      testNonVariableTerm(new Atom("Abc"), "'Abc'");
      testNonVariableTerm(new Atom("a B 1.2,3;4 !:$% c"), "'a B 1.2,3;4 !:$% c'");
      testNonVariableTerm(new Atom("'"), "''''");
      testNonVariableTerm(new Atom("Ab'c"), "'Ab''c'");
      testNonVariableTerm(new Atom("A'b''c"), "'A''b''''c'");
   }

   public void testIntegerNumbers() {
      // Note: parser doesn't handle negative numbers
      for (int i = 0; i < 10; i++) {
         testNonVariableTerm(new IntegerNumber(i), Integer.toString(i));
      }
      testNonVariableTerm(new IntegerNumber(Integer.MAX_VALUE), Integer.toString(Integer.MAX_VALUE));
   }

   public void testDoubleNumbers() {
      // Note: parser doesn't handle negative numbers
      for (int i = 0; i < 10; i++) {
         testNonVariableTerm(new DoubleNumber(i), Double.toString(i));
      }
      double[] testData = {3.14, 1.0000001, 0.2};
      for (double d : testData) {
         testNonVariableTerm(new DoubleNumber(d), Double.toString(d));
      }
      testNonVariableTerm(new DoubleNumber(3.4028235E38), "3.4028235E38");
      testNonVariableTerm(new DoubleNumber(3.4028235E38), "3.4028235e38");
      testNonVariableTerm(new DoubleNumber(340.28235), "3.4028235E2");
   }

   public void testInvalidDoubleNumbers() {
      // must have extra digits after the 'e' or 'E'
      parseInvalid("3.403e");
      parseInvalid("3.403E");
   }

   public void testPredicates() {
      testPredicate("p(a,b,c)");
      testPredicate("p( a, b, c )");
      testPredicate("p(1, a, [a,b,c|d])");
      testPredicate("p(1, 'a b c?', [a,b,c|d])");
      testPredicate("p(_Test1, _Test2, _Test3)");
   }

   private void testPredicate(String syntax) {
      Term t = parseTerm(syntax);
      assertNotNull(t);
      assertTrue("actual " + t.getClass(), t instanceof Structure);
   }

   public void testInvalidPredicateSyntax() {
      parseInvalid("p(");
      parseInvalid("p(a ,b, c");
      parseInvalid("p(a b, c)");
      parseInvalid("p(a, b c)");
   }

   public void testLists() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Atom c = new Atom("c");
      Atom d = new Atom("d");
      Atom e = new Atom("e");
      Atom f = new Atom("f");
      testList("[a,b,c]", new Term[] {a, b, c}, null);
      testList("[a,b,c|d]", new Term[] {a, b, c}, d);
      testList("[ a, b, c | d ]", new Term[] {a, b, c}, d);
      testList("[a,b,c|[d,e,f]]", new Term[] {a, b, c, d, e, f}, null);
      testList("[a,b,c|[d,e|f]]", new Term[] {a, b, c, d, e}, f);
      testList("[a,b,c|[]]]", new Term[] {a, b, c}, null);
   }

   private void testList(String input, Term[] expectedArgs, Term expectedTail) {
      Term expected;
      if (expectedTail == null) {
         expected = ListFactory.create(expectedArgs);
      } else {
         expected = ListFactory.create(expectedArgs, expectedTail);
      }
      Term actual = parseTerm(input);
      assertSame(TermType.LIST, actual.getType());
      assertTrue(actual instanceof List);
      assertTrue(expected.strictEquality(actual));
   }

   public void testInvalidListSyntax() {
      parseInvalid("[a, b c]");
      parseInvalid("[a, b; c]");
      parseInvalid("[a, b, c");
      parseInvalid("[a, b, c | d ");
      parseInvalid("[a, b, c | d | e]");
      parseInvalid("[a, b, c | d , e]");
   }

   public void testEmptyList() {
      assertSame(EmptyList.EMPTY_LIST, parseTerm("[]"));
      assertSame(EmptyList.EMPTY_LIST, parseTerm(".()"));
      assertSame(EmptyList.EMPTY_LIST, parseTerm("'.'()"));
   }

   public void testListsUsingPredicateSyntax() {
      testPredicate(".(1)");
      testPredicate(".(1, 2, 3)");

      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Atom c = new Atom("c");
      testList(".(a, b)", new Term[] {a}, b);
      testList(".(a, .(b, c))", new Term[] {a, b}, c);
      testList(".(a, .(b, .(c, [])))", new Term[] {a, b, c}, EmptyList.EMPTY_LIST);

      testList("'.'(a, '.'(b, '.'(c, [])))", new Term[] {a, b, c}, EmptyList.EMPTY_LIST);
   }

   public void testVariables() {
      testVariableTerm(new Variable("X"), "X");
      testVariableTerm(new Variable("XYZ"), "XYZ");
      testVariableTerm(new Variable("Xyz"), "Xyz");
      testVariableTerm(new Variable("XyZ"), "XyZ");
      testVariableTerm(new Variable("X_1"), "X_1");
   }

   public void testAnonymousVariable() {
      testVariableTerm(AnonymousVariable.ANONYMOUS_VARIABLE, "_");
      testVariableTerm(AnonymousVariable.ANONYMOUS_VARIABLE, "_123");
      testVariableTerm(AnonymousVariable.ANONYMOUS_VARIABLE, "_Test");
   }

   private void testNonVariableTerm(Term expected, String input) {
      Term actual = parseTerm(input);
      assertNotNull(actual);
      assertEquals(expected.getClass(), actual.getClass());
      assertEquals(expected.getType(), actual.getType());
      assertEquals(expected.getName(), actual.getName());
      assertEquals(expected.toString(), actual.toString());
      assertTrue(expected.strictEquality(actual));
   }

   private void testVariableTerm(Term expected, String input) {
      Term actual = parseTerm(input);
      assertNotNull(actual);
      assertEquals(expected.getClass(), actual.getClass());
      assertEquals(expected.getType(), actual.getType());
      assertEquals(expected.toString(), actual.toString());
      assertTrue(expected.unify(actual));
   }

   private void parseInvalid(String source) {
      try {
         parseTerm(source);
         fail("No exception thrown parsing: " + source);
      } catch (ParserException e) {
         // expected
      } catch (Exception e) {
         e.printStackTrace();
         fail("parsing: " + source + " caused: " + e);
      }
   }
}