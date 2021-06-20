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
package org.projog.core.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.atom;
import static org.projog.TestUtils.createArgs;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TermFactory.decimalFraction;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;

import org.junit.Test;
import org.projog.core.ProjogException;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class AbstractArithmeticOperatorTest {
   // a non-abstract implementation of ArithmeticOperator (so we can create and test it)
   static class DummyArithmeticOperator extends AbstractArithmeticOperator {
   }

   @Test
   public void testWrongNumberOfArgumentsException() {
      for (int i = 0; i < 10; i++) {
         assertWrongNumberOfArgumentsException(i);
      }
   }

   private void assertWrongNumberOfArgumentsException(int numberOfArguments) {
      try {
         DummyArithmeticOperator c = new DummyArithmeticOperator();
         c.setKnowledgeBase(createKnowledgeBase());
         c.calculate(createArgs(numberOfArguments, integerNumber()));
         fail();
      } catch (IllegalArgumentException e) {
         String expectedMessage = "The ArithmeticOperator: class org.projog.core.math.AbstractArithmeticOperatorTest$DummyArithmeticOperator does next accept the number of arguments: "
                                  + numberOfArguments;
         assertEquals(expectedMessage, e.getMessage());
      }
   }

   @Test
   public void testOneArg() {
      final Numeric expected = integerNumber(14);
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
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
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
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
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
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
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
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

   private void assertUnexpectedAtom(AbstractArithmeticOperator c, Term... args) {
      try {
         c.calculate(args);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find arithmetic operator: test/0", e.getMessage());
      }
   }

   private void assertUnexpectedVariable(AbstractArithmeticOperator c, Term... args) {
      try {
         c.calculate(args);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot get Numeric for term: X of type: VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testArithmeticFunctionArgument() {
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
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
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
         @Override
         public Numeric calculate(Numeric n1, Numeric n2) {
            return new IntegerNumber(n1.getLong() - n2.getLong());
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      Structure f1 = structure("*", integerNumber(3), integerNumber(7));
      Structure f2 = structure("/", integerNumber(12), integerNumber(2));
      Numeric result = c.calculate(new Term[] {f1, f2});
      assertEquals(15, result.getLong()); // 15 = (3*7)-(12/2)
   }

   @Test
   public void testPreprocess_one_argument() {
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
         @Override
         public Numeric calculate(Numeric n1) {
            return new IntegerNumber(-(n1.getLong() * 2));
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      assertEquals(integerNumber(-84), c.preprocess(structure("dummy", integerNumber(42))));
      assertSame(c, c.preprocess(structure("dummy", variable())));
      // TODO test PreprocessedUnaryOperator
      assertEquals("org.projog.core.math.AbstractArithmeticOperator$PreprocessedUnaryOperator",
                  c.preprocess(structure("dummy", structure("+", integerNumber(), variable()))).getClass().getName());
   }

   @Test
   public void testPreprocess_two_arguments() {
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
         @Override
         public Numeric calculate(Numeric n1, Numeric n2) {
            return new IntegerNumber(n1.getLong() - n2.getLong() + 42);
         }
      };
      c.setKnowledgeBase(createKnowledgeBase());
      assertEquals(integerNumber(47), c.preprocess(structure("dummy", integerNumber(8), integerNumber(3))));
      assertSame(c, c.preprocess(structure("dummy", variable(), variable())));
      // TODO test PreprocessedBinaryOperator
      assertEquals("org.projog.core.math.AbstractArithmeticOperator$PreprocessedBinaryOperator",
                  c.preprocess(structure("dummy", variable(), structure("+", integerNumber(), variable()))).getClass().getName());
      assertEquals("org.projog.core.math.AbstractArithmeticOperator$PreprocessedBinaryOperator",
                  c.preprocess(structure("dummy", structure("+", integerNumber(), variable()), variable())).getClass().getName());
      assertEquals("org.projog.core.math.AbstractArithmeticOperator$PreprocessedBinaryOperator",
                  c.preprocess(structure("dummy", structure("+", integerNumber(), variable()), structure("+", integerNumber(), variable()))).getClass().getName());
   }

   @Test
   public void testPreprocess_not_pure() {
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
         @Override
         protected boolean isPure() {
            return false;
         }
      };
      assertSame(c, c.preprocess(structure("dummy", integerNumber(42))));
      assertSame(c, c.preprocess(structure("dummy", variable())));
   }

   @Test
   public void testIsPure() {
      final AbstractArithmeticOperator c = new AbstractArithmeticOperator() {
      };
      assertTrue(c.isPure());
   }
}
