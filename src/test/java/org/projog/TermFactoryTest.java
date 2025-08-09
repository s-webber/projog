/*
 * Copyright 2021 S. Webber
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
package org.projog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Random;

import org.junit.Test;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.List;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class TermFactoryTest {
   @Test
   public void testAtom() {
      Atom a = TermFactory.atom();
      assertEquals("test", a.getName());
   }

   @Test
   public void testAtomByName() {
      String name = "testAtom" + System.currentTimeMillis();
      Atom a = TermFactory.atom(name);
      assertEquals(name, a.getName());
   }

   @Test
   public void testStructure() {
      Structure s = TermFactory.structure();
      assertEquals("test", s.getName());
      assertEquals(1, s.getNumberOfArguments());
      assertEquals(new Atom("test"), s.firstArgument());
   }

   @Test
   public void testStructureByNameAndArgs() {
      String name = "testStructure" + System.currentTimeMillis();
      Term arg1 = new Atom("first argument");
      Term arg2 = new Atom("second argument");
      Term arg3 = new Atom("third argument");
      Structure s = TermFactory.structure(name, arg1, arg2, arg3);
      assertEquals(name, s.getName());
      assertEquals(3, s.getNumberOfArguments());
      assertSame(arg1, s.firstArgument());
      assertSame(arg2, s.secondArgument());
      assertSame(arg3, s.thirdArgument());
   }

   @Test
   public void testList() {
      Term arg1 = new Atom("first argument");
      Term arg2 = new Atom("second argument");
      Term arg3 = new Atom("third argument");
      List list = TermFactory.list(arg1, arg2, arg3);
      assertSame(arg1, list.firstArgument());
      list = (List) list.secondArgument();
      assertSame(arg2, list.firstArgument());
      list = (List) list.secondArgument();
      assertSame(arg3, list.firstArgument());
      assertSame(EmptyList.EMPTY_LIST, list.secondArgument());
   }

   @Test
   public void testIntegerNumber() {
      IntegerNumber i = TermFactory.integerNumber();
      assertEquals(1, i.getLong());
   }

   @Test
   public void testIntegerNumberByValue() {
      long value = new Random().nextLong();
      IntegerNumber i = TermFactory.integerNumber(value);
      assertEquals(value, i.getLong());
   }

   @Test
   public void testDecimalFraction() {
      DecimalFraction d = TermFactory.decimalFraction();
      assertEquals(1.0, d.getDouble(), 0);
   }

   @Test
   public void testDecimalFractionByValue() {
      double value = new Random().nextDouble();
      DecimalFraction d = TermFactory.decimalFraction(value);
      assertEquals(value, d.getDouble(), 0);
   }

   @Test
   public void testVariable() {
      Variable v = TermFactory.variable();
      assertEquals("X", v.getId());
   }

   @Test
   public void testVariableById() {
      String id = "testVariable" + System.currentTimeMillis();
      Variable v = TermFactory.variable(id);
      assertEquals(id, v.getId());
   }
}
