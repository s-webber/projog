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
import static org.projog.TermFactory.atom;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

/**
 * @see TermTest
 */
@RunWith(DataProviderRunner.class)
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
   public void testGetBound() {
      Atom a = atom();
      Term b = a.getBound();
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
   @DataProvider({"-1", "0", "1"})
   public void testGetArgument(int index) {
      try {
         Atom a = atom();
         a.getArgument(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: " + index, e.getMessage());
      }
   }

   @Test
   public void testFirstArgument() {
      try {
         Atom a = atom();
         a.firstArgument();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: 0", e.getMessage());
      }
   }

   @Test
   public void testSecondArgument() {
      try {
         Atom a = atom();
         a.secondArgument();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: 1", e.getMessage());
      }
   }

   @Test
   public void testThirdArgument() {
      try {
         Atom a = atom();
         a.thirdArgument();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: 2", e.getMessage());
      }
   }

   @Test
   public void testFourthArgument() {
      try {
         Atom a = atom();
         a.fourthArgument();
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         assertEquals("Array index out of range: 3", e.getMessage());
      }
   }

   @Test
   public void testGetArgs() {
      Atom a = atom();
      assertSame(TermUtils.EMPTY_ARRAY, a.getArgs());
   }

   @Test
   public void testHashCode() {
      Atom a = new Atom("test");

      assertEquals(a.hashCode(), new Atom("test").hashCode());
      assertNotEquals(a.hashCode(), new Atom("abcd").hashCode());
   }

   /** see {@link TermTest} */
   @Test
   public void testEquals() {
      Atom a = new Atom("test");

      assertTrue(a.equals(a));
      assertTrue(a.equals(new Atom(a.getName())));

      assertNotEquals(new Atom("7"), new IntegerNumber(7));
      assertNotEquals(new Atom("7"), new DecimalFraction(7));
      assertNotEquals(a, new Atom(a.getName() + " "));
      assertNotEquals(a, new Atom(a.getName() + "x"));
      assertNotEquals(a, new Atom(" " + a.getName()));
      assertNotEquals(a, new Atom("x" + a.getName()));
      assertNotEquals(a, new Atom("TEST"));
      assertNotEquals(a, new Atom("tes"));
      assertNotEquals(a, Structure.createStructure(a.getName(), new Term[] {a}));
      assertNotEquals(a, ListFactory.createList(a, a));
   }
}
