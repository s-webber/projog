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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.doubleNumber;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import org.junit.Test;

public class ListFactoryTest {
   @Test
   public void testCreationWithoutTail() {
      testCreation(null);
   }

   @Test
   public void testCreationWithTail() {
      testCreation(new Atom("tail"));
   }

   private void testCreation(Term tail) {
      Term[] args = new Term[] {atom(), structure(), integerNumber(), doubleNumber(), variable()};

      Term l;
      if (tail == null) {
         l = ListFactory.create(args);
      } else {
         l = ListFactory.create(args, tail);
      }
      testIsList(l);
      assertEquals(args[0], l.getArgument(0));

      for (Term arg : args) {
         testIsList(l);
         assertEquals(arg, l.getArgument(0));
         l = l.getArgument(1);
      }

      if (tail == null) {
         assertSame(TermType.EMPTY_LIST, l.getType());
         assertSame(EmptyList.EMPTY_LIST, l);
      } else {
         assertEquals(tail, l);
      }
   }

   private void testIsList(Term l) {
      assertEquals(".", l.getName());
      assertEquals(TermType.LIST, l.getType());
      assertEquals(2, l.getNumberOfArguments());
   }
}