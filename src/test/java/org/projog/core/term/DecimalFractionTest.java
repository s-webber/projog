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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.decimalFraction;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

/**
 * @see TermTest
 */
@RunWith(DataProviderRunner.class)
public class DecimalFractionTest {
   private static final double DELTA = 0;

   @Test
   public void testGetName() {
      assertEquals("0.0", new DecimalFraction(0).getName());
      assertEquals(Double.toString(Double.MAX_VALUE), new DecimalFraction(Double.MAX_VALUE).getName());
      assertEquals("-7.0", new DecimalFraction(-7).getName());
   }

   @Test
   public void testToString() {
      assertEquals("0.0", new DecimalFraction(0).toString());
      assertEquals(Double.toString(Double.MAX_VALUE), new DecimalFraction(Double.MAX_VALUE).toString());
      assertEquals("-7.0", new DecimalFraction(-7).toString());
   }

   @Test
   public void testGetTerm() {
      DecimalFraction d1 = new DecimalFraction(0);
      DecimalFraction d2 = d1.getTerm();
      assertSame(d1, d2);
   }

   @Test
   public void testGetBound() {
      DecimalFraction d1 = new DecimalFraction(0);
      Term d2 = d1.getBound();
      assertSame(d1, d2);
   }

   @Test
   public void testGetLong() {
      assertEquals(0, new DecimalFraction(0).getLong());
      assertEquals(Long.MAX_VALUE, new DecimalFraction(Long.MAX_VALUE).getLong());
      assertEquals(-7, new DecimalFraction(-7.01).getLong());
      assertEquals(-1, new DecimalFraction(-1.01).getLong());
   }

   @Test
   public void testCalculate() {
      DecimalFraction d1 = new DecimalFraction(0);
      DecimalFraction d2 = d1.calculate(null);
      assertSame(d1, d2);
   }

   @Test
   public void testGetDouble() {
      assertEquals(0.0, new DecimalFraction(0).getDouble(), DELTA);
      assertEquals(Double.MAX_VALUE, new DecimalFraction(Double.MAX_VALUE).getDouble(), DELTA);
      assertEquals(-7.01, new DecimalFraction(-7.01).getDouble(), DELTA);
   }

   @Test
   public void testGetType() {
      DecimalFraction d = decimalFraction();
      assertSame(TermType.FRACTION, d.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      DecimalFraction d = decimalFraction();
      assertEquals(0, d.getNumberOfArguments());
   }

   @Test
   @DataProvider({"-1", "0", "1"})
   public void testGetArgument(int index) {
      try {
         DecimalFraction d = decimalFraction();
         d.getArgument(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: " + index, e.getMessage());
      }
   }

   @Test
   public void testGetArgs() {
      DecimalFraction d = decimalFraction();
      assertSame(TermUtils.EMPTY_ARRAY, d.getArgs());
   }

   @Test
   public void testHashCode() {
      DecimalFraction n = new DecimalFraction(7);

      assertEquals(n.hashCode(), new DecimalFraction(7).hashCode());
      assertNotEquals(n.hashCode(), new DecimalFraction(7.1).hashCode());
   }

   /** see {@link TermTest} */
   @Test
   public void testEquals() {
      DecimalFraction n = new DecimalFraction(7);

      assertTrue(n.equals(n));
      assertTrue(n.equals(new DecimalFraction(7)));

      assertNotEquals(n, new DecimalFraction(6));
      assertNotEquals(n, new DecimalFraction(8));
      assertNotEquals(n, new DecimalFraction(6.9));
      assertNotEquals(n, new DecimalFraction(7.1));
      assertNotEquals(n, new DecimalFraction(0));
      assertNotEquals(n, new DecimalFraction(-7));
      assertNotEquals(n, new IntegerNumber(7));
      assertNotEquals(n, new Atom("7"));
      assertNotEquals(n, Structure.createStructure("7", new Term[] {n}));
      assertNotEquals(n, ListFactory.createList(n, n));
   }
}
