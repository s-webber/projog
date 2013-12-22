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
package org.projog.core.term;

import static org.projog.TestUtils.doubleNumber;
import junit.framework.TestCase;

/**
 * @see TermTest
 */
public class DoubleNumberTest extends TestCase {
   public void testGetName() {
      assertEquals("0.0", new DoubleNumber(0).getName());
      assertEquals(Double.toString(Double.MAX_VALUE), new DoubleNumber(Double.MAX_VALUE).getName());
      assertEquals("-7.0", new DoubleNumber(-7).getName());
   }

   public void testToString() {
      assertEquals("0.0", new DoubleNumber(0).toString());
      assertEquals(Double.toString(Double.MAX_VALUE), new DoubleNumber(Double.MAX_VALUE).toString());
      assertEquals("-7.0", new DoubleNumber(-7).toString());
   }

   public void testGetTerm() {
      DoubleNumber d1 = new DoubleNumber(0);
      DoubleNumber d2 = d1.getTerm();
      assertSame(d1, d2);
   }

   public void testGetInt() {
      assertEquals(0, new DoubleNumber(0).getInt());
      assertEquals(Integer.MAX_VALUE, new DoubleNumber(Integer.MAX_VALUE).getInt());
      assertEquals(-7, new DoubleNumber(-7.01).getInt());
      assertEquals(-1, new DoubleNumber(-1.01).getInt());
   }

   public void testGetDouble() {
      assertEquals(0.0, new DoubleNumber(0).getDouble());
      assertEquals(Double.MAX_VALUE, new DoubleNumber(Double.MAX_VALUE).getDouble());
      assertEquals(-7.01, new DoubleNumber(-7.01).getDouble());
   }

   public void testGetType() {
      DoubleNumber d = doubleNumber();
      assertSame(TermType.DOUBLE, d.getType());
   }

   public void testGetNumberOfArguments() {
      DoubleNumber d = doubleNumber();
      assertEquals(0, d.getNumberOfArguments());
   }

   public void testGetArgument() {
      try {
         DoubleNumber d = doubleNumber();
         d.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public void testGetArgs() {
      DoubleNumber d = doubleNumber();
      assertSame(TermUtils.EMPTY_ARRAY, d.getArgs());
   }
}