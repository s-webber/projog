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

import static org.projog.TestUtils.integerNumber;
import junit.framework.TestCase;

/**
 * @see TermTest
 */
public class IntegerNumberTest extends TestCase {
   public void testGetName() {
      assertEquals("0", new IntegerNumber(0).getName());
      assertEquals(Integer.toString(Integer.MAX_VALUE), new IntegerNumber(Integer.MAX_VALUE).getName());
      assertEquals("-7", new IntegerNumber(-7).getName());
   }

   public void testToString() {
      assertEquals("0", new IntegerNumber(0).toString());
      assertEquals(Integer.toString(Integer.MAX_VALUE), new IntegerNumber(Integer.MAX_VALUE).toString());
      assertEquals("-7", new IntegerNumber(-7).toString());
   }

   public void testGetTerm() {
      IntegerNumber i1 = new IntegerNumber(0);
      IntegerNumber i2 = i1.getTerm();
      assertSame(i1, i2);
   }

   public void testGetInt() {
      assertEquals(0, new IntegerNumber(0).getInt());
      assertEquals(Integer.MAX_VALUE, new IntegerNumber(Integer.MAX_VALUE).getInt());
      assertEquals(-7, new IntegerNumber(-7).getInt());
   }

   public void testGetDouble() {
      assertEquals(0.0, new IntegerNumber(0).getDouble());
      assertEquals((double) Integer.MAX_VALUE, new IntegerNumber(Integer.MAX_VALUE).getDouble());
      assertEquals(-7.0, new IntegerNumber(-7).getDouble());
   }

   public void testGetType() {
      IntegerNumber i = integerNumber();
      assertSame(TermType.INTEGER, i.getType());
   }

   public void testGetNumberOfArguments() {
      IntegerNumber i = integerNumber();
      assertEquals(0, i.getNumberOfArguments());
   }

   public void testGetArgument() {
      try {
         IntegerNumber i = integerNumber();
         i.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public void testGetArgs() {
      IntegerNumber i = integerNumber();
      assertSame(TermUtils.EMPTY_ARRAY, i.getArgs());
   }
}