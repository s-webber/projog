package org.projog.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.parseTerm;

import org.junit.Test;
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

public class TermParserTest {
   @Test
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
      testNonVariableTerm(new Atom("~"), "~");
   }

   @Test
   public void testIntegerNumbers() {
      // Note: parser doesn't handle negative numbers
      for (int i = 0; i < 10; i++) {
         testNonVariableTerm(new IntegerNumber(i), Integer.toString(i));
      }
      testNonVariableTerm(new IntegerNumber(Integer.MAX_VALUE), Integer.toString(Integer.MAX_VALUE));
   }

   @Test
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

   @Test
   public void testInvalidDoubleNumbers() {
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
   }

   private void testPredicate(String syntax) {
      Term t = parseTerm(syntax);
      assertNotNull(t);
      assertTrue("actual " + t.getClass(), t instanceof Structure);
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
         expected = ListFactory.create(expectedArgs);
      } else {
         expected = ListFactory.create(expectedArgs, expectedTail);
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
      assertSame(EmptyList.EMPTY_LIST, parseTerm(".()"));
      assertSame(EmptyList.EMPTY_LIST, parseTerm("'.'()"));
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
      testVariableTerm(new Variable("X"), "X");
      testVariableTerm(new Variable("XYZ"), "XYZ");
      testVariableTerm(new Variable("Xyz"), "Xyz");
      testVariableTerm(new Variable("XyZ"), "XyZ");
      testVariableTerm(new Variable("X_1"), "X_1");
   }

   @Test
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