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
import static org.projog.core.term.EmptyList.EMPTY_LIST;

import org.junit.Test;

/**
 * @see TermTest
 */
public class EmptyListTest {
   @Test
   public void testGetName() {
      assertEquals(".", EMPTY_LIST.getName());
   }

   @Test
   public void testToString() {
      assertEquals("[]", EMPTY_LIST.toString());
   }

   @Test
   public void testGetTerm() {
      EmptyList e = EMPTY_LIST.getTerm();
      assertSame(EMPTY_LIST, e);
   }

   @Test
   public void testGetType() {
      assertSame(TermType.EMPTY_LIST, EMPTY_LIST.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      assertEquals(0, EMPTY_LIST.getNumberOfArguments());
   }

   @Test
   public void testGetArgument() {
      try {
         EMPTY_LIST.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testGetArgs() {
      try {
         EMPTY_LIST.getArgs();
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }
}
