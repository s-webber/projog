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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.createArgs;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.term.Term;

public class AbstractRetryablePredicateTest {
   private static final AbstractRetryablePredicate INSTANCE = new AbstractRetryablePredicate() {
   };

   @Test
   public void testSimpleImplementation() {
      assertTrue(INSTANCE.isRetryable());
      assertTrue(INSTANCE.couldReEvaluationSucceed());

      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      INSTANCE.setKnowledgeBase(kb);
      assertSame(kb, INSTANCE.getKnowledgeBase());
   }

   @Test
   public void testWrongNumberOfArgumentsException() {
      for (int i = 0; i < 10; i++) {
         assertWrongNumberOfArgumentsException(i);
      }
   }

   private void assertWrongNumberOfArgumentsException(int numberOfArguments) {
      try {
         INSTANCE.getPredicate(createArgs(numberOfArguments));
         fail();
      } catch (IllegalArgumentException e) {
         String expectedMessage = "The predicate factory: class org.projog.core.function.AbstractRetryablePredicateTest$1 does next accept the number of arguments: " + numberOfArguments;
         assertEquals(expectedMessage, e.getMessage());
      }
   }

   @Test
   public void testNoArgs() {
      AbstractRetryablePredicate p = new AbstractRetryablePredicate() {
         @Override
         public Predicate getPredicate() {
            return this;
         }
      };
      assertSame(p, p.getPredicate(createArgs(0)));
   }

   @Test
   public void testOneArg() {
      AbstractRetryablePredicate p = new AbstractRetryablePredicate() {
         @Override
         public Predicate getPredicate(Term t1) {
            return this;
         }
      };
      assertSame(p, p.getPredicate(createArgs(1)));
   }

   @Test
   public void testTwoArgs() {
      AbstractRetryablePredicate p = new AbstractRetryablePredicate() {
         @Override
         public Predicate getPredicate(Term t1, Term t2) {
            return this;
         }
      };
      assertSame(p, p.getPredicate(createArgs(2)));
   }

   @Test
   public void testThreeArgs() {
      AbstractRetryablePredicate p = new AbstractRetryablePredicate() {
         @Override
         public Predicate getPredicate(Term t1, Term t2, Term t3) {
            return this;
         }
      };
      assertSame(p, p.getPredicate(createArgs(3)));
   }
}
