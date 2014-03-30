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
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.doubleNumber;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class ListFactoryTest {
   @Test
   public void testCreationWithoutTail() {
      final Term[] args = createArguments();
      Term l = ListFactory.create(args);

      for (Term arg : args) {
         testIsList(l);
         assertEquals(arg, l.getArgument(0));
         l = l.getArgument(1);
      }

      assertSame(TermType.EMPTY_LIST, l.getType());
      assertSame(EmptyList.EMPTY_LIST, l);
   }

   @Test
   public void testCreationWithTail() {
      final Term[] args = createArguments();
      final Term tail = new Atom("tail");
      Term l = ListFactory.create(args, tail);

      for (Term arg : args) {
         testIsList(l);
         assertEquals(arg, l.getArgument(0));
         l = l.getArgument(1);
      }

      assertSame(tail, l);
   }

   /** Check {@link ListFactory#create(Collection)} works the same as {@link ListFactory#create(Term[])} */
   @Test
   public void testCreationWithJavaCollection() {
      final Term[] args = createArguments();
      final Collection<Term> c = Arrays.asList(args);
      final Term listFromArray = ListFactory.create(args);
      final Term listFromCollection = ListFactory.create(c);
      assertTrue(listFromCollection.strictEquality(listFromArray));
   }

   private Term[] createArguments() {
      return new Term[] {atom(), structure(), integerNumber(), doubleNumber(), variable()};
   }

   private void testIsList(Term l) {
      assertEquals(".", l.getName());
      assertEquals(TermType.LIST, l.getType());
      assertEquals(2, l.getNumberOfArguments());
   }
}