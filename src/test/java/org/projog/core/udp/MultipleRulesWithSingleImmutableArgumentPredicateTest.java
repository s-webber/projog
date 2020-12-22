/*
 * Copyright 2020 S. Webber
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
package org.projog.core.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.structure;

import org.junit.Before;
import org.junit.Test;
import org.projog.SimpleProjogListener;
import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.function.SucceedsNeverPredicate;
import org.projog.core.function.SucceedsOncePredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

public class MultipleRulesWithSingleImmutableArgumentPredicateTest {
   private static final String FUNCTOR = "test";

   private KnowledgeBase kb;
   private PredicateFactory testObject;

   @Before
   public void init() {
      String[] atomNames = {"a", "b", "c", "c", "c", "c", "c", "d", "e", "b", "f"};

      kb = TestUtils.createKnowledgeBase(TestUtils.COMPILATION_DISABLED_PROPERTIES);
      PredicateKey key = new PredicateKey(FUNCTOR, 1);
      StaticUserDefinedPredicateFactory pf = new StaticUserDefinedPredicateFactory(kb, key);
      for (String atomName : atomNames) {
         ClauseModel clause = ClauseModel.createClauseModel(structure(FUNCTOR, atom(atomName)));
         pf.addLast(clause);
      }
      kb.addUserDefinedPredicate(pf);
      assertSame(pf, kb.getPredicateFactory(key));
      testObject = pf.getActualPredicateFactory();
   }

   @Test
   public void testSuceedsNever() {
      assertSame(SucceedsNeverPredicate.FAIL, testObject.getPredicate(atom("z")));
   }

   @Test
   public void testSucceedsOnce() {
      assertSame(SucceedsOncePredicate.TRUE, testObject.getPredicate(atom("a")));
      assertSame(SucceedsOncePredicate.TRUE, testObject.getPredicate(atom("d")));
      assertSame(SucceedsOncePredicate.TRUE, testObject.getPredicate(atom("e")));
      assertSame(SucceedsOncePredicate.TRUE, testObject.getPredicate(atom("f")));
   }

   @Test
   public void testSucceedsMany() {
      assertSucceedsMany(atom("b"), 2);
      assertSucceedsMany(atom("c"), 5);
      assertNotSame(testObject.getPredicate(atom("b")), testObject.getPredicate(atom("b")));
   }

   private void assertSucceedsMany(Term arg, int expectedSuccesses) {
      Predicate p = testObject.getPredicate(arg);
      assertSame(InterpretedUserDefinedPredicate.class, p.getClass()); // TODO add assertClass to TestUtils
      for (int i = 0; i < expectedSuccesses; i++) {
         assertTrue(p.couldReevaluationSucceed());
         assertTrue(p.evaluate());
      }
      assertFalse(p.evaluate());
   }

   @Test
   public void testSpyPointEnabled_fails() {
      final SimpleProjogListener o = new SimpleProjogListener();
      KnowledgeBaseUtils.getProjogListeners(kb).addListener(o);

      KnowledgeBaseUtils.getSpyPoints(kb).setTraceEnabled(true);

      Predicate p = testObject.getPredicate(atom("z"));
      assertFalse(p.evaluate());
      assertSame(SucceedsNeverPredicate.class, p.getClass());
      assertEquals("CALLtest(z)FAILtest(z)", o.result());
   }

   @Test
   public void testSpyPointEnabled_succeedsOnce() {
      final SimpleProjogListener o = new SimpleProjogListener();
      KnowledgeBaseUtils.getProjogListeners(kb).addListener(o);

      KnowledgeBaseUtils.getSpyPoints(kb).setTraceEnabled(true);

      Predicate p = testObject.getPredicate(atom("a"));
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertSame(SucceedsOncePredicate.class, p.getClass());
      assertEquals("CALLtest(a)EXITtest(a)", o.result());
   }

   @Test
   public void testSpyPointEnabled_succeedsMany() {
      final SimpleProjogListener o = new SimpleProjogListener();
      KnowledgeBaseUtils.getProjogListeners(kb).addListener(o);

      KnowledgeBaseUtils.getSpyPoints(kb).setTraceEnabled(true);

      Predicate p = testObject.getPredicate(atom("c"));
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertSame(InterpretedUserDefinedPredicate.class, p.getClass());
      assertEquals("CALLtest(c)EXITtest(c)REDOtest(c)EXITtest(c)REDOtest(c)EXITtest(c)REDOtest(c)EXITtest(c)REDOtest(c)EXITtest(c)", o.result());
   }
}
