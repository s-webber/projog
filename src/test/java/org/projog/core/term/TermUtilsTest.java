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
package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.list;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;
import static org.projog.core.KnowledgeBaseUtils.getCalculatables;

import java.util.Set;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.Calculatables;
import org.projog.core.KnowledgeBase;
import org.projog.core.ProjogException;

public class TermUtilsTest {
   @Test
   public void testEmptyArray() {
      assertEquals(0, TermUtils.EMPTY_ARRAY.length);
   }

   @Test
   public void testCopy() {
      // setup input terms
      Atom a = atom("a");
      Variable x = variable("X");
      Variable y = variable("Y");
      Variable z = variable("Z");
      assertTrue(x.unify(a));
      Structure p = structure("p", x, y);
      Term[] input = {a, p, x, y, z};

      // perform copy
      Term[] output = TermUtils.copy(input);

      // check result
      assertEquals(input.length, output.length);

      assertSame(a, output[0]);

      Term t = output[1];
      assertSame(TermType.STRUCTURE, t.getType());
      assertSame(p.getName(), t.getName());
      assertEquals(2, t.getNumberOfArguments());
      assertSame(a, t.getArgument(0));
      Term copyOfY = t.getArgument(1);
      assertVariable(copyOfY, "Y");

      assertSame(a, output[2]);

      assertSame(copyOfY, output[3]);

      assertVariable(output[4], "Z");
   }

   private void assertVariable(Term t, String id) {
      assertSame(TermType.NAMED_VARIABLE, t.getType());
      assertSame(t, t.getTerm());
      assertEquals(id, ((Variable) t).getId());
   }

   @Test
   public void testBacktrack() {
      // setup input terms
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Variable x = variable("X");
      Variable y = variable("Y");
      Variable z = variable("Z");
      assertTrue(x.unify(a));
      assertTrue(y.unify(b));
      assertTrue(z.unify(c));
      Term original[] = {x, a, b, y, c, z};
      Term input[] = {x, a, b, y, c, z};

      // perform the backtrack
      TermUtils.backtrack(input);

      // assert variables have backtracked
      assertSame(x, x.getTerm());
      assertSame(y, y.getTerm());
      assertSame(z, z.getTerm());

      // assert array was not manipulated
      for (int i = 0; i < input.length; i++) {
         assertSame(original[i], input[i]);
      }
   }

   @Test
   public void testUnifySuccess() {
      // setup input terms
      Variable x = variable("X");
      Variable y = variable("Y");
      Variable z = variable("Z");
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Term[] input1 = {x, b, z};
      Term[] input2 = {a, y, c};

      // attempt unification
      assertTrue(TermUtils.unify(input1, input2));

      // assert all variables unified to atoms
      assertSame(a, x.getTerm());
      assertSame(b, y.getTerm());
      assertSame(c, z.getTerm());
   }

   @Test
   public void testUnifyFailure() {
      // setup input terms
      Variable x = variable("X");
      Variable y = variable("Y");
      Variable z = variable("Z");
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Term[] input1 = {x, b, z, b};
      Term[] input2 = {a, y, c, a};

      // attempt unification
      assertFalse(TermUtils.unify(input1, input2));

      // assert all variables in input1 were backed tracked
      assertSame(x, x.getTerm());
      assertSame(z, z.getTerm());

      // as javadocs states, terms passed in second argument to unify may not be backtracked 
      assertSame(b, y.getTerm());
   }

   @Test
   public void testGetAllVariablesInTerm() {
      Variable q = variable("Q");
      Variable r = variable("R");
      Variable s = variable("S");
      Variable t = variable("T");
      Variable v = variable("V");
      Variable w = variable("W");
      Variable x = variable("X");
      Variable y = variable("Y");
      Variable z = variable("Z");
      Variable anon = TermUtils.createAnonymousVariable();
      Variable[] variables = {q, r, s, t, v, w, x, y, z, anon};
      Structure input = structure("p1", x, v, anon, EmptyList.EMPTY_LIST, y, q, integerNumber(1), structure("p2", y, decimalFraction(1.5), w), list(s, y, integerNumber(7), r, t),
                  z);
      Set<Variable> result = TermUtils.getAllVariablesInTerm(input);
      assertEquals(variables.length, result.size());
      for (Variable variable : variables) {
         assertTrue(result.contains(variable));
      }
   }

   @Test
   public void testIntegerNumberCastToNumeric() {
      IntegerNumber i = integerNumber();
      assertSame(i, TermUtils.castToNumeric(i));
   }

   @Test
   public void testDecimalFractionCastToNumeric() {
      DecimalFraction d = decimalFraction();
      assertSame(d, TermUtils.castToNumeric(d));
   }

   @Test
   public void testAtomCastToNumeric() {
      try {
         Atom a = atom("1");
         TermUtils.castToNumeric(a);
         fail();
      } catch (ProjogException e) {
         assertEquals("Expected Numeric but got: ATOM with value: 1", e.getMessage());
      }
   }

   @Test
   public void testVariableCastToNumeric() {
      Variable v = variable();
      try {
         TermUtils.castToNumeric(v);
         fail();
      } catch (ProjogException e) {
         assertEquals("Expected Numeric but got: NAMED_VARIABLE with value: X", e.getMessage());
      }
      IntegerNumber i = integerNumber();
      v.unify(i);
      assertSame(i, TermUtils.castToNumeric(v));
   }

   @Test
   public void testStructureCastToNumeric() {
      // test that, even if it represents an arithmetic expression,
      // a structure causes an exception when passed to castToNumeric
      Structure arithmeticExpression = structure("*", integerNumber(3), integerNumber(7));
      try {
         TermUtils.castToNumeric(arithmeticExpression);
         fail();
      } catch (ProjogException e) {
         assertEquals("Expected Numeric but got: STRUCTURE with value: *(3, 7)", e.getMessage());
      }
   }

   @Test
   public void testIntegerNumberToLong() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      Calculatables calculatables = getCalculatables(kb);
      assertEquals(Integer.MAX_VALUE, TermUtils.toLong(calculatables, integerNumber(Integer.MAX_VALUE)));
      assertEquals(1, TermUtils.toLong(calculatables, integerNumber(1)));
      assertEquals(0, TermUtils.toLong(calculatables, integerNumber(0)));
      assertEquals(Integer.MIN_VALUE, TermUtils.toLong(calculatables, integerNumber(Integer.MIN_VALUE)));
   }

   @Test
   public void testArithmeticFunctionToLong() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      Calculatables calculatables = getCalculatables(kb);
      Structure arithmeticExpression = structure("*", integerNumber(3), integerNumber(7));
      assertEquals(21, TermUtils.toLong(calculatables, arithmeticExpression));
   }

   @Test
   public void testToLongExceptions() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      assertTestToLongException(kb, atom("test"), "Cannot find calculatable: test");
      assertTestToLongException(kb, structure("p", integerNumber(1), integerNumber(1)), "Cannot find calculatable: p/2");
      assertTestToLongException(kb, decimalFraction(0), "Expected integer but got: FRACTION with value: 0.0");
      assertTestToLongException(kb, structure("+", decimalFraction(1.0), decimalFraction(1.0)), "Expected integer but got: FRACTION with value: 2.0");
   }

   private void assertTestToLongException(KnowledgeBase kb, Term t, String expectedExceptionMessage) {
      Calculatables calculatables = getCalculatables(kb);
      try {
         TermUtils.toLong(calculatables, t);
         fail();
      } catch (ProjogException e) {
         assertEquals(expectedExceptionMessage, e.getMessage());
      }
   }

   @Test
   public void testGetAtomName() {
      Atom a = atom("testAtomName");
      assertEquals("testAtomName", TermUtils.getAtomName(a));
   }

   @Test
   public void testGetAtomNameException() {
      Structure p = structure("testAtomName", atom());
      try {
         assertEquals("testAtomName", TermUtils.getAtomName(p));
      } catch (ProjogException e) {
         assertEquals("Expected an atom but got: STRUCTURE with value: testAtomName(test)", e.getMessage());
      }
   }

   @Test
   public void testCreateAnonymousVariable() {
      Term anon = TermUtils.createAnonymousVariable();
      assertSame(Variable.class, anon.getClass());
      assertSame(TermType.NAMED_VARIABLE, anon.getType());
      assertEquals("_", anon.toString());
      assertNotSame(anon, TermUtils.createAnonymousVariable());
   }

   @Test
   public void testToInt() {
      assertToInt(0);
      assertToInt(1);
      assertToInt(-1);
      assertToInt(Integer.MAX_VALUE);
      assertToInt(Integer.MIN_VALUE);
   }

   private void assertToInt(long n) {
      assertEquals(n, TermUtils.toInt(integerNumber(n)));
   }

   @Test
   public void testToIntException() {
      assertToIntException(Integer.MAX_VALUE + 1L);
      assertToIntException(Integer.MIN_VALUE - 1L);
      assertToIntException(Long.MAX_VALUE);
      assertToIntException(Long.MIN_VALUE);
   }

   private void assertToIntException(long n) {
      try {
         TermUtils.toInt(integerNumber(n));
         fail();
      } catch (ProjogException e) {
         assertEquals("Value cannot be cast to an int without losing precision: " + n, e.getMessage());
      }
   }
}
