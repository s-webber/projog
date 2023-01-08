/*
 * Copyright 2023 S. Webber
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
package org.projog.core.predicate.builtin.clp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.math.Absolute;
import org.projog.clp.math.Add;
import org.projog.clp.math.Divide;
import org.projog.clp.math.Maximum;
import org.projog.clp.math.Minimum;
import org.projog.clp.math.Minus;
import org.projog.clp.math.Multiply;
import org.projog.clp.math.Subtract;

public class CommonExpressionTest {
   @Test
   public void testMinus() {
      ExpressionFactory f = CommonExpression.minus();
      Expression e = f.createExpression(new Expression[] {new FixedValue(7)});

      assertSame(Minus.class, e.getClass());
      assertEquals(-7L, e.getMax(null));
      assertEquals(-7L, e.getMin(null));
   }

   @Test
   public void testAbsolute() {
      ExpressionFactory f = CommonExpression.absolute();
      Expression e = f.createExpression(new Expression[] {new FixedValue(-7)});

      assertSame(Absolute.class, e.getClass());
      assertEquals(7L, e.getMax(null));
      assertEquals(7L, e.getMin(null));
   }

   @Test
   public void testAdd() {
      ExpressionFactory f = CommonExpression.add();
      Expression e = f.createExpression(new Expression[] {new FixedValue(7), new FixedValue(3)});

      assertSame(Add.class, e.getClass());
      assertEquals(10L, e.getMax(null));
      assertEquals(10L, e.getMin(null));
   }

   @Test
   public void testSubtract() {
      ExpressionFactory f = CommonExpression.subtract();
      Expression e = f.createExpression(new Expression[] {new FixedValue(7), new FixedValue(3)});

      assertSame(Subtract.class, e.getClass());
      assertEquals(4L, e.getMax(null));
      assertEquals(4L, e.getMin(null));
   }

   @Test
   public void testMultiply() {
      ExpressionFactory f = CommonExpression.multiply();
      Expression e = f.createExpression(new Expression[] {new FixedValue(7), new FixedValue(3)});

      assertSame(Multiply.class, e.getClass());
      assertEquals(21L, e.getMax(null));
      assertEquals(21L, e.getMin(null));
   }

   @Test
   public void testDivide() {
      ExpressionFactory f = CommonExpression.divide();
      Expression e = f.createExpression(new Expression[] {new FixedValue(27), new FixedValue(3)});

      assertSame(Divide.class, e.getClass());
      assertSame(9L, e.getMax(null));
      assertSame(9L, e.getMin(null));
   }

   @Test
   public void testMaximum() {
      ExpressionFactory f = CommonExpression.maximum();
      Expression e = f.createExpression(new Expression[] {new FixedValue(123), new FixedValue(456)});

      assertSame(Maximum.class, e.getClass());
      assertEquals(456L, e.getMax(null));
      assertEquals(456L, e.getMin(null));
   }

   @Test
   public void testMinimum() {
      ExpressionFactory f = CommonExpression.minimum();
      Expression e = f.createExpression(new Expression[] {new FixedValue(123), new FixedValue(456)});

      assertSame(Minimum.class, e.getClass());
      assertEquals(123L, e.getMax(null));
      assertEquals(123L, e.getMin(null));
   }
}
