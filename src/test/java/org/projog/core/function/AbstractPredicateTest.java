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
package org.projog.core.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.createArgs;

import org.junit.Test;
import org.projog.core.term.Term;

public class AbstractPredicateTest {
   // a non-abstract implementation of AbstractPredicate (so we can create and test it) 
   static class DummyPredicate extends AbstractPredicate {
      @Override
      public boolean isRetryable() {
         return false;
      }

      @Override
      public boolean couldReEvaluationSucceed() {
         return false;
      }
   }

   @Test
   public void testWrongNumberOfArgumentsException() {
      for (int i = 0; i < 10; i++) {
         assertWrongNumberOfArgumentsException(i);
      }
   }

   private void assertWrongNumberOfArgumentsException(int numberOfArguments) {
      try {
         new DummyPredicate().evaluate(createArgs(numberOfArguments));
         fail();
      } catch (IllegalArgumentException e) {
         String expectedMessage = "The predicate: class org.projog.core.function.AbstractPredicateTest$DummyPredicate does next accept the number of arguments: " + numberOfArguments;
         assertEquals(expectedMessage, e.getMessage());
      }
   }

   @Test
   public void testNoArgs() {
      AbstractPredicate p = new DummyPredicate() {
         @Override
         public boolean evaluate() {
            return true;
         }
      };
      assertTrue(p.evaluate(createArgs(0)));
   }

   @Test
   public void testOneArg() {
      AbstractPredicate p = new DummyPredicate() {
         @Override
         public boolean evaluate(Term t1) {
            return true;
         }
      };
      assertTrue(p.evaluate(createArgs(1)));
   }

   @Test
   public void testTwoArgs() {
      AbstractPredicate p = new DummyPredicate() {
         @Override
         public boolean evaluate(Term t1, Term t2) {
            return true;
         }
      };
      assertTrue(p.evaluate(createArgs(2)));
   }

   @Test
   public void testThreeArgs() {
      AbstractPredicate p = new DummyPredicate() {
         @Override
         public boolean evaluate(Term t1, Term t2, Term t3) {
            return true;
         }
      };
      assertTrue(p.evaluate(createArgs(3)));
   }
}
