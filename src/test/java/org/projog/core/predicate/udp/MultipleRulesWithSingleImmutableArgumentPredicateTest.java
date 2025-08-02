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
package org.projog.core.predicate.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.structure;

import org.junit.Before;
import org.junit.Test;
import org.projog.SimpleProjogListener;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.SucceedsNeverPredicate;
import org.projog.core.predicate.SucceedsOncePredicate;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class MultipleRulesWithSingleImmutableArgumentPredicateTest {
   private static final String FUNCTOR = "test";

   private KnowledgeBase kb;
   private PredicateFactory testObject;

   @Before
   public void init() {
      String[] atomNames = {"a", "b", "c", "c", "c", "c", "c", "d", "e", "b", "f"};

      kb = TestUtils.createKnowledgeBase(TestUtils.PROJOG_DEFAULT_PROPERTIES);
      PredicateKey key = new PredicateKey(FUNCTOR, 1);
      StaticUserDefinedPredicateFactory pf = new StaticUserDefinedPredicateFactory(kb, key);
      for (String atomName : atomNames) {
         ClauseModel clause = ClauseModel.createClauseModel(structure(FUNCTOR, atom(atomName)));
         pf.addLast(clause);
      }
      kb.getPredicates().addUserDefinedPredicate(pf);
      assertSame(pf, kb.getPredicates().getPredicateFactory(key));
      testObject = pf.getActualPredicateFactory();
   }

   @Test
   public void testSuceedsNever() {
      assertSame(SucceedsNeverPredicate.SINGLETON, testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("z")})));
   }

   @Test
   public void testSucceedsOnce() {
      assertSame(SucceedsOncePredicate.SINGLETON, testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("a")})));
      assertSame(SucceedsOncePredicate.SINGLETON, testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("d")})));
      assertSame(SucceedsOncePredicate.SINGLETON, testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("e")})));
      assertSame(SucceedsOncePredicate.SINGLETON, testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("f")})));
   }

   @Test
   public void testSucceedsMany() {
      assertSucceedsMany(atom("b"), 2);
      assertSucceedsMany(atom("c"), 5);
      assertNotSame(testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("b")})),
                  testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("b")})));
   }

   private void assertSucceedsMany(Term arg, int expectedSuccesses) {
      Predicate p = testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {arg}));
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
      kb.getProjogListeners().addListener(o);

      kb.getSpyPoints().setTraceEnabled(true);

      Predicate p = testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("z")}));
      assertFalse(p.evaluate());
      assertSame(SucceedsNeverPredicate.class, p.getClass());
      assertEquals("CALLtest(z)FAILtest(z)", o.result());
   }

   @Test
   public void testSpyPointEnabled_succeedsOnce() {
      final SimpleProjogListener o = new SimpleProjogListener();
      kb.getProjogListeners().addListener(o);

      kb.getSpyPoints().setTraceEnabled(true);

      Predicate p = testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("a")}));
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertSame(SucceedsOncePredicate.class, p.getClass());
      assertEquals("CALLtest(a)EXITtest(a)", o.result());
   }

   @Test
   public void testSpyPointEnabled_succeedsMany() {
      final SimpleProjogListener o = new SimpleProjogListener();
      kb.getProjogListeners().addListener(o);

      kb.getSpyPoints().setTraceEnabled(true);

      Predicate p = testObject.getPredicate(Structure.createStructure(FUNCTOR, new Term[] {atom("c")}));
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
