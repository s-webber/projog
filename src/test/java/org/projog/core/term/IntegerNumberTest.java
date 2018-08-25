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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.integerNumber;

import org.junit.Test;

/**
 * @see TermTest
 */
public class IntegerNumberTest {
   private static final double DELTA = 0;

   @Test
   public void testGetName() {
      assertEquals("0", new IntegerNumber(0).getName());
      assertEquals(Long.toString(Long.MAX_VALUE), new IntegerNumber(Long.MAX_VALUE).getName());
      assertEquals("-7", new IntegerNumber(-7).getName());
   }

   @Test
   public void testToString() {
      assertEquals("0", new IntegerNumber(0).toString());
      assertEquals(Long.toString(Long.MAX_VALUE), new IntegerNumber(Long.MAX_VALUE).toString());
      assertEquals("-7", new IntegerNumber(-7).toString());
   }

   @Test
   public void testGetTerm() {
      IntegerNumber i1 = new IntegerNumber(0);
      IntegerNumber i2 = i1.getTerm();
      assertSame(i1, i2);
   }

   @Test
   public void testGetLong() {
      assertEquals(0, new IntegerNumber(0).getLong());
      assertEquals(Long.MAX_VALUE, new IntegerNumber(Long.MAX_VALUE).getLong());
      assertEquals(-7, new IntegerNumber(-7).getLong());
   }

   @Test
   public void testGetDouble() {
      assertEquals(0.0, new IntegerNumber(0).getDouble(), DELTA);
      assertEquals(Integer.MAX_VALUE, new IntegerNumber(Integer.MAX_VALUE).getDouble(), DELTA);
      assertEquals(-7.0, new IntegerNumber(-7).getDouble(), DELTA);
   }

   @Test
   public void testGetType() {
      IntegerNumber i = integerNumber();
      assertSame(TermType.INTEGER, i.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      IntegerNumber i = integerNumber();
      assertEquals(0, i.getNumberOfArguments());
   }

   @Test
   public void testGetArgument() {
      try {
         IntegerNumber i = integerNumber();
         i.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testGetArgs() {
      IntegerNumber i = integerNumber();
      assertSame(TermUtils.EMPTY_ARRAY, i.getArgs());
   }
}
