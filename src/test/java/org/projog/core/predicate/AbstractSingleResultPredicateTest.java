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
package org.projog.core.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.atom;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class AbstractSingleResultPredicateTest {
   private static final Atom ARG1 = atom("a");
   private static final Atom ARG2 = atom("b");
   private static final Atom ARG3 = atom("c");
   private static final Atom ARG4 = atom("d");

   /**
    * Check {@code AbstractSingletonPredicate#setKnowledgeBase(KnowledgeBase)} invokes
    * {@code AbstractSingletonPredicate#init()} after setting the knowledge base.
    */
   @Test
   public void testInit() {
      class TestPredicate extends AbstractSingleResultPredicate {
         KnowledgeBase x;

         @Override
         protected void init() {
            x = getKnowledgeBase();
         }
      }
      TestPredicate pf = new TestPredicate();
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      pf.setKnowledgeBase(kb);
      assertSame(kb, pf.x);
      assertSame(kb, pf.getKnowledgeBase());
   }

   @Test
   public void testIllegalArgumentException() {
      AbstractSingleResultPredicate pf = new AbstractSingleResultPredicate() {
      };
      for (int i = 0; i < 100; i++) {
         assertIllegalArgumentException(pf, i);
      }
   }

   @Test
   public void testNoArguments() {
      final AtomicBoolean b = new AtomicBoolean();
      AbstractSingleResultPredicate pf = new AbstractSingleResultPredicate() {
         @Override
         protected boolean evaluate() {
            return b.get();
         }
      };

      b.set(true);
      assertSame(PredicateUtils.TRUE, pf.getPredicate(new Term[0]));

      b.set(false);
      assertSame(PredicateUtils.FALSE, pf.getPredicate(new Term[0]));
   }

   @Test
   public void testOneArgument() {
      AbstractSingleResultPredicate pf = new AbstractSingleResultPredicate() {
         @Override
         protected boolean evaluate(Term t) {
            return t == ARG1;
         }
      };

      assertSame(PredicateUtils.TRUE, pf.getPredicate(new Term[] {ARG1}));

      assertSame(PredicateUtils.FALSE, pf.getPredicate(new Term[] {ARG2}));
   }

   @Test
   public void testTwoArguments() {
      AbstractSingleResultPredicate pf = new AbstractSingleResultPredicate() {
         @Override
         protected boolean evaluate(Term t1, Term t2) {
            return t1 == ARG1 && t2 == ARG2;
         }
      };

      assertSame(PredicateUtils.TRUE, pf.getPredicate(new Term[] {ARG1, ARG2}));

      assertSame(PredicateUtils.FALSE, pf.getPredicate(new Term[] {ARG1, ARG1}));
   }

   @Test
   public void testThreeArguments() {
      AbstractSingleResultPredicate pf = new AbstractSingleResultPredicate() {
         @Override
         protected boolean evaluate(Term t1, Term t2, Term t3) {
            return t1 == ARG1 && t2 == ARG2 && t3 == ARG3;
         }
      };

      assertSame(PredicateUtils.TRUE, pf.getPredicate(new Term[] {ARG1, ARG2, ARG3}));

      assertSame(PredicateUtils.FALSE, pf.getPredicate(new Term[] {ARG1, ARG1, ARG1}));
   }

   @Test
   public void testFourArguments() {
      AbstractSingleResultPredicate pf = new AbstractSingleResultPredicate() {
         @Override
         protected boolean evaluate(Term t1, Term t2, Term t3, Term t4) {
            return t1 == ARG1 && t2 == ARG2 && t3 == ARG3 && t4 == ARG4;
         }
      };

      assertSame(PredicateUtils.TRUE, pf.getPredicate(new Term[] {ARG1, ARG2, ARG3, ARG4}));

      assertSame(PredicateUtils.FALSE, pf.getPredicate(new Term[] {ARG1, ARG1, ARG1, ARG1}));
   }

   private void assertIllegalArgumentException(AbstractSingleResultPredicate pf, int numberOfArguments) {
      try {
         pf.getPredicate(new Term[numberOfArguments]);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("The predicate factory: " + pf.getClass().getName() + " does next accept the number of arguments: " + numberOfArguments, e.getMessage());
      }
   }
}
