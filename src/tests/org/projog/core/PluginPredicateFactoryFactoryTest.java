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
package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;
import static org.projog.TestUtils.atom;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

public class PluginPredicateFactoryFactoryTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testAddPredicateFactory() {
      PluginPredicateFactoryFactory pf = getPluginPredicateFactoryFactory();

      // create PredicateKey and PredicateFactory to add to factory
      PredicateKey key = new PredicateKey("testAddPredicateFactory", 1);
      PredicateFactory ef = new PredicateFactory() {
         @Override
         public void setKnowledgeBase(KnowledgeBase kb) {
         }

         @Override
         public Predicate getPredicate(Term... args) {
            return null;
         }
      };

      // assert not already defined in either factory and knowledge base
      assertNotDefined(pf, key);

      // add
      pf.addPredicateFactory(key, ef);

      // assert now defined in both factory and knowledge base
      assertSame(ef, pf.getPredicateFactory(key));
      assertSame(ef, kb.getPredicateFactory(key));

      // assert exception thrown if try to re-add
      try {
         pf.addPredicateFactory(key, ef);
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactory/1", e.getMessage());
      }
   }

   @Test
   public void testEvaluate() {
      PluginPredicateFactoryFactory pf = getPluginPredicateFactoryFactory();
      String dummyPredicateName = "testEvaluate";
      String dummyPredicateClassName = new DummyPredicate().getClass().getName();
      Term evaluateArg1 = atom(dummyPredicateName);
      Term evaluateArg2 = atom(dummyPredicateClassName);
      PredicateKey key = PredicateKey.createForTerm(evaluateArg1);

      // assert not already defined in either factory and knowledge base
      assertNotDefined(pf, key);

      // add
      assertTrue(pf.evaluate(evaluateArg1, evaluateArg2));

      // assert now defined in both factory and knowledge base
      assertSame(DummyPredicate.class, pf.getPredicateFactory(key).getClass());
      assertSame(DummyPredicate.class, kb.getPredicateFactory(key).getClass());

      // assert cannot re-add Predicate
      try {
         pf.evaluate(evaluateArg1, evaluateArg2);
         fail("could readd add Predicate named: " + evaluateArg1);
      } catch (ProjogException e) {
         // expected
      }
   }

   private void assertNotDefined(PluginPredicateFactoryFactory pf, PredicateKey key) {
      assertNull(pf.getPredicateFactory(key));
      assertSame(UnknownPredicate.UNKNOWN_PREDICATE, kb.getPredicateFactory(key));
   }

   private PluginPredicateFactoryFactory getPluginPredicateFactoryFactory() {
      PredicateFactory ef = kb.getPredicateFactory(ADD_PREDICATE_KEY);
      assertSame(PluginPredicateFactoryFactory.class, ef.getClass());
      assertTrue(ef instanceof AbstractSingletonPredicate);
      return (PluginPredicateFactoryFactory) ef;
   }

   public static class DummyPredicate extends AbstractSingletonPredicate {
      @Override
      public boolean evaluate(Term... args) {
         return true;
      }
   }
}