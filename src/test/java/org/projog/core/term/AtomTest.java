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
import static org.projog.TestUtils.atom;

import org.junit.Test;

/**
 * @see TermTest
 */
public class AtomTest {
   @Test
   public void testGetName() {
      Atom a = new Atom("test");
      assertEquals("test", a.getName());
   }

   @Test
   public void testToString() {
      Atom a = new Atom("test");
      assertEquals("test", a.toString());
   }

   @Test
   public void testGetTerm() {
      Atom a = atom();
      Atom b = a.getTerm();
      assertSame(a, b);
   }

   @Test
   public void testGetType() {
      Atom a = atom();
      assertSame(TermType.ATOM, a.getType());
   }

   @Test
   public void testGetNumberOfArguments() {
      Atom a = atom();
      assertEquals(0, a.getNumberOfArguments());
   }

   @Test
   public void testGetArgument() {
      try {
         Atom a = atom();
         a.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testGetArgs() {
      Atom a = atom();
      assertSame(TermUtils.EMPTY_ARRAY, a.getArgs());
   }
}
