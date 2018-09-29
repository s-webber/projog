/*
 * Copyright 2013-2014 S. Webber
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.parseTerm;

import org.junit.Test;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.List;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

public class TermParserTest {
   @Test
   public void testAtoms() {
      assertNonVariableTerm(new Atom("x"), "x");
      assertNonVariableTerm(new Atom("xyz"), "xyz");
      assertNonVariableTerm(new Atom("xYz"), "xYz");
      assertNonVariableTerm(new Atom("xYZ"), "xYZ");
   }

   @Test
   public void testAtomsWithUnderscores() {
      assertNonVariableTerm(new Atom("x_1"), "x_1");
      assertNonVariableTerm(new Atom("xttRytf_uiu"), "xttRytf_uiu");
   }

   @Test
   public void testAtomsEnclosedInSingleQuotes() {
      assertNonVariableTerm(new Atom("Abc"), "'Abc'");
      assertNonVariableTerm(new Atom("a B 1.2,3;4 !:$% c"), "'a B 1.2,3;4 !:$% c'");
   }

   @Test
   public void testAtomsContainingSingleQuotes() {
      assertNonVariableTerm(new Atom("'"), "''''");
      assertNonVariableTerm(new Atom("Ab'c"), "'Ab''c'");
      assertNonVariableTerm(new Atom("A'b''c"), "'A''b''''c'");
   }

   @Test
   public void testAtomsWithSingleNonAlphanumericCharacter() {
      assertNonVariableTerm(new Atom("~"), "~");
      assertNonVariableTerm(new Atom("!"), "!");
   }

   @Test
   public void testAtomsWithEscapedCharacters() {
      assertNonVariableTerm(new Atom("\t"), "'\\t'");
      assertNonVariableTerm(new Atom("\b"), "'\\b'");
      assertNonVariableTerm(new Atom("\n"), "'\\n'");
      assertNonVariableTerm(new Atom("\r"), "'\\r'");
      assertNonVariableTerm(new Atom("\f"), "'\\f'");
      assertNonVariableTerm(new Atom("\'"), "'\\''");
      assertNonVariableTerm(new Atom("\""), "'\\\"'");
      assertNonVariableTerm(new Atom("\\"), "'\\\\'");
      assertNonVariableTerm(new Atom("abc\t\t\tdef\n"), "'abc\\t\\t\\tdef\\n'");
   }

   @Test
   public void testAtomsWithUnicodeCharacters() {
      assertNonVariableTerm(new Atom("Hello"), "'\u0048\u0065\u006C\u006c\u006F'");
      assertNonVariableTerm(new Atom("Hello"), "'\u0048ello'");
      assertNonVariableTerm(new Atom("Hello"), "'H\u0065l\u006co'");
      assertNonVariableTerm(new Atom("Hello"), "'Hell\u006F'");
   }

   @Test
   public void testIntegerNumbers() {
      for (int i = 0; i < 10; i++) {
         assertNonVariableTerm(new IntegerNumber(i), Integer.toString(i));
      }
      assertNonVariableTerm(new IntegerNumber(Long.MAX_VALUE), Long.toString(Long.MAX_VALUE));
      assertNonVariableTerm(new IntegerNumber(Long.MIN_VALUE), Long.toString(Long.MIN_VALUE));
   }

   @Test
   public void testDecimalFractions() {
      double[] testData = {0, 1, 2, 10, 3.14, 1.0000001, 0.2};
      for (double d : testData) {
         assertNonVariableTerm(new DecimalFraction(d), Double.toString(d));
         assertNonVariableTerm(new DecimalFraction(-d), Double.toString(-d));
      }
      assertNonVariableTerm(new DecimalFraction(3.4028235E38), "3.4028235E38");
      assertNonVariableTerm(new DecimalFraction(3.4028235E38), "3.4028235e38");
      assertNonVariableTerm(new DecimalFraction(340.28235), "3.4028235E2");
   }

   @Test
   public void testInvalidDecimalFractions() {
      // must have extra digits after the 'e' or 'E'
      parseInvalid("3.403e");
      parseInvalid("3.403E");
   }

   @Test
   public void testPredicates() {
      testPredicate("p(a,b,c)");
      testPredicate("p( a, b, c )");
      testPredicate("p(1, a, [a,b,c|d])");
      testPredicate("p(1, 'a b c?', [a,b,c|d])");
      testPredicate("p(_Test1, _Test2, _Test3)");
      testPredicate("~(a,b,c)");
      testPredicate(">(a,b,c)");
   }

   private void testPredicate(String syntax) {
      Term t = parseTerm(syntax);
      assertNotNull(t);
      assertSame(Structure.class, t.getClass());
   }

   @Test
   public void testInvalidPredicateSyntax() {
      parseInvalid("p(");
      parseInvalid("p(a ,b, c");
      parseInvalid("p(a b, c)");
      parseInvalid("p(a, b c)");
   }

   @Test
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
         expected = ListFactory.createList(expectedArgs);
      } else {
         expected = ListFactory.createList(expectedArgs, expectedTail);
      }
      Term actual = parseTerm(input);
      assertSame(TermType.LIST, actual.getType());
      assertTrue(actual instanceof List);
      assertTrue(expected.strictEquality(actual));
   }

   @Test
   public void testInvalidListSyntax() {
      parseInvalid("[a, b c]");
      parseInvalid("[a, b; c]");
      parseInvalid("[a, b, c");
      parseInvalid("[a, b, c | d ");
      parseInvalid("[a, b, c | d | e]");
      parseInvalid("[a, b, c | d , e]");
   }

   @Test
   public void testEmptyList() {
      assertSame(EmptyList.EMPTY_LIST, parseTerm("[]"));
   }

   @Test
   public void testNoArgumentStructure() {
      parseInvalid("p()");
   }

   @Test
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

   @Test
   public void testVariables() {
      assertVariableTerm(new Variable("X"), "X");
      assertVariableTerm(new Variable("XYZ"), "XYZ");
      assertVariableTerm(new Variable("Xyz"), "Xyz");
      assertVariableTerm(new Variable("XyZ"), "XyZ");
      assertVariableTerm(new Variable("X_1"), "X_1");
   }

   @Test
   public void testAnonymousVariable() {
      assertVariableTerm(new Variable("_"), "_");
      assertVariableTerm(new Variable("_123"), "_123");
      assertVariableTerm(new Variable("_Test"), "_Test");
   }

   private void assertNonVariableTerm(Term expected, String input) {
      Term actual = parseTerm(input);
      assertNotNull(actual);
      assertEquals(expected.getClass(), actual.getClass());
      assertEquals(expected.getType(), actual.getType());
      assertEquals(expected.getName(), actual.getName());
      assertEquals(expected.toString(), actual.toString());
      assertTrue(expected.strictEquality(actual));
   }

   private void assertVariableTerm(Term expected, String input) {
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
