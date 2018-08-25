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
package org.projog.core.function.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.createArgs;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import org.junit.Test;
import org.projog.core.ProjogException;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class AbstractCalculatableTest {
   // a non-abstract implementation of AbstractPredicate (so we can create and test it) 
   static class DummyCalculatable extends AbstractCalculatable {
   }

   @Test
   public void testWrongNumberOfArgumentsException() {
      for (int i = 0; i < 10; i++) {
         assertWrongNumberOfArgumentsException(i);
      }
   }

   private void assertWrongNumberOfArgumentsException(int numberOfArguments) {
      try {
         DummyCalculatable c = new DummyCalculatable();
         c.setKnowledgeBase(createKnowledgeBase());
         c.calculate(createArgs(numberOfArguments, integerNumber()));
         fail();
      } catch (IllegalArgumentException e) {
         String expectedMessage = "The Calculatable: class org.projog.core.function.math.AbstractCalculatableTest$DummyCalculatable does next accept the number of arguments: " + numberOfArguments;
         assertEquals(expectedMessage, e.getMessage());
      }
   }

   @Test
   public void testOneArg() {
      final Numeric expected = integerNumber(14);
      final AbstractCalculatable c = new AbstractCalculatable() {
         @Override
         public Numeric calculate(Numeric n1) {
            return expected;
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      assertSame(expected, c.calculate(new Term[] {integerNumber()}));
      assertSame(expected, c.calculate(new Term[] {decimalFraction()}));
   }

   @Test
   public void testTwoArgs() {
      final Numeric expected = integerNumber(14);
      final AbstractCalculatable c = new AbstractCalculatable() {
         @Override
         public Numeric calculate(Numeric n1, Numeric n2) {
            return expected;
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      assertSame(expected, c.calculate(new Term[] {integerNumber(), integerNumber()}));
      assertSame(expected, c.calculate(new Term[] {decimalFraction(), decimalFraction()}));
      assertSame(expected, c.calculate(new Term[] {integerNumber(), decimalFraction()}));
      assertSame(expected, c.calculate(new Term[] {decimalFraction(), integerNumber()}));
   }

   @Test
   public void testInvalidArgument() {
      final AbstractCalculatable c = new AbstractCalculatable() {
         @Override
         public Numeric calculate(Numeric n1) {
            return n1;
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());

      assertUnexpectedAtom(c, atom());
      assertUnexpectedVariable(c, variable());
   }

   @Test
   public void testInvalidArguments() {
      final AbstractCalculatable c = new AbstractCalculatable() {
         @Override
         public Numeric calculate(Numeric n1, Numeric n2) {
            return n1;
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());

      assertUnexpectedAtom(c, atom(), atom());
      assertUnexpectedAtom(c, integerNumber(), atom());
      assertUnexpectedAtom(c, atom(), integerNumber());
      assertUnexpectedVariable(c, variable(), variable());
      assertUnexpectedVariable(c, integerNumber(), variable());
      assertUnexpectedVariable(c, variable(), integerNumber());
   }

   private void assertUnexpectedAtom(AbstractCalculatable c, Term... args) {
      try {
         c.calculate(new Term[] {atom()});
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find calculatable: test", e.getMessage());
      }
   }

   private void assertUnexpectedVariable(AbstractCalculatable c, Term... args) {
      try {
         c.calculate(args);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot get Numeric for term: X of type: NAMED_VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testArithmeticFunctionArgument() {
      final AbstractCalculatable c = new AbstractCalculatable() {
         @Override
         public Numeric calculate(Numeric n1) {
            return new IntegerNumber(n1.getLong() + 5);
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      Structure arithmeticFunction = structure("*", integerNumber(3), integerNumber(7));
      Numeric result = c.calculate(new Term[] {arithmeticFunction});
      assertEquals(26, result.getLong()); // 26 = (3*7)+5
   }

   @Test
   public void testArithmeticFunctionArguments() {
      final AbstractCalculatable c = new AbstractCalculatable() {
         @Override
         public Numeric calculate(Numeric n1, Numeric n2) {
            return new IntegerNumber(n1.getLong() - n2.getLong());
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      Structure f1 = structure("*", integerNumber(3), integerNumber(7));
      Structure f2 = structure("/", integerNumber(12), integerNumber(2));
      Numeric result = c.calculate(new Term[] {f1, f2});
      assertEquals(15, result.getLong()); // 26 = (3*7)-(12/2)
   }
}
