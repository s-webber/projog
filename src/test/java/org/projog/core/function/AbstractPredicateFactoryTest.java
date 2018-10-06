/*
 * Copyright 2018 S. Webber
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
import static org.junit.Assert.fail;
import static org.projog.TestUtils.atom;

import org.junit.Test;
import org.projog.core.Predicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class AbstractPredicateFactoryTest {
   @Test
   public void testIllegalArgumentException() {
      AbstractPredicateFactory pf = new AbstractPredicateFactory() {
      };
      for (int i = 0; i < 100; i++) {
         assertIllegalArgumentException(pf, i);
      }
   }

   @Test
   public void testOverridenMethods() {
      final Atom arg1 = atom("a");
      final Atom arg2 = atom("b");
      final Atom arg3 = atom("c");

      final Predicate noArgPredicate = createPredicate();
      final Predicate oneArgPredicate = createPredicate();
      final Predicate twoArgsPredicate = createPredicate();
      final Predicate threeArgsPredicate = createPredicate();

      final AbstractPredicateFactory pf = new AbstractPredicateFactory() {
         @Override
         protected Predicate getPredicate() {
            return noArgPredicate;
         }

         @Override
         protected Predicate getPredicate(Term t) {
            assertSame(arg1, t);
            return oneArgPredicate;
         }

         @Override
         protected Predicate getPredicate(Term t1, Term t2) {
            assertSame(arg1, t1);
            assertSame(arg2, t2);
            return twoArgsPredicate;
         }

         @Override
         protected Predicate getPredicate(Term t1, Term t2, Term t3) {
            assertSame(arg1, t1);
            assertSame(arg2, t2);
            assertSame(arg3, t3);
            return threeArgsPredicate;
         }
      };

      assertSame(noArgPredicate, pf.getPredicate(new Term[0]));
      assertSame(oneArgPredicate, pf.getPredicate(new Term[] {arg1}));
      assertSame(twoArgsPredicate, pf.getPredicate(new Term[] {arg1, arg2}));
      assertSame(threeArgsPredicate, pf.getPredicate(new Term[] {arg1, arg2, arg3}));

      assertIllegalArgumentException(pf, 4);
   }

   private Predicate createPredicate() {
      return new Predicate() {
         @Override
         public boolean evaluate() {
            return false;
         }

         @Override
         public boolean couldReevaluationSucceed() {
            return false;
         }
      };
   }

   private void assertIllegalArgumentException(AbstractPredicateFactory pf, int numberOfArguments) {
      try {
         pf.getPredicate(new Term[numberOfArguments]);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("The predicate factory: " + pf.getClass().getName() + " does next accept the number of arguments: " + numberOfArguments, e.getMessage());
      }
   }
}
