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

import static org.projog.core.term.AnonymousVariable.ANONYMOUS_VARIABLE;
import junit.framework.TestCase;

/**
 * @see TermTest
 */
public class AnonymousVariableTest extends TestCase {
   public void testGetName() {
      try {
         ANONYMOUS_VARIABLE.getName();
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public void testToString() {
      assertEquals("_", ANONYMOUS_VARIABLE.toString());
   }

   public void testGetType() {
      assertSame(TermType.ANONYMOUS_VARIABLE, ANONYMOUS_VARIABLE.getType());
   }

   public void testGetNumberOfArguments() {
      assertEquals(0, ANONYMOUS_VARIABLE.getNumberOfArguments());
   }

   public void testGetArgument() {
      try {
         ANONYMOUS_VARIABLE.getArgument(0);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public void testGetArgs() {
      try {
         ANONYMOUS_VARIABLE.getArgs();
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public void testCopy() {
      assertSame(ANONYMOUS_VARIABLE, ANONYMOUS_VARIABLE.copy(null));
   }

   public void testGetValue() {
      AnonymousVariable a = ANONYMOUS_VARIABLE.getTerm();
      assertSame(ANONYMOUS_VARIABLE, a);
   }

   public void testStrictEqualityWithSelf() {
      assertFalse(ANONYMOUS_VARIABLE.strictEquality(ANONYMOUS_VARIABLE));
   }

   public void testUnifyWithSelf() {
      assertTrue(ANONYMOUS_VARIABLE.unify(ANONYMOUS_VARIABLE));
   }

   public void testIsImmutable() {
      assertTrue(ANONYMOUS_VARIABLE.isImmutable());
   }

   public void testBacktrack() {
      // just check no exceptions thrown
      ANONYMOUS_VARIABLE.backtrack();
   }
}