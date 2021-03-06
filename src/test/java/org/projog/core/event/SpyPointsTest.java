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
package org.projog.core.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.list;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;

import org.junit.Test;
import org.projog.SimpleProjogListener;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.DynamicUserDefinedPredicateFactory;
import org.projog.core.term.EmptyList;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class SpyPointsTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testGetSameSpyPointForSamePredicateKey() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key = createKey("test", 2);
      assertSame(testObject.getSpyPoint(key), testObject.getSpyPoint(key));
   }

   @Test
   public void testGetSameSpyPointWhenPredicateKeysEqual() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key1 = createKey("test", 2);
      PredicateKey key2 = createKey("test", 2);
      assertNotSame(key1, key2);
      assertSame(testObject.getSpyPoint(key1), testObject.getSpyPoint(key2));
   }

   @Test
   public void testGetDifferentSpyPointWhenPredicateKeysNotEqual() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key1 = createKey("test1", 0);
      PredicateKey key2 = createKey("test1", 1);
      PredicateKey key3 = createKey("test1", 2);
      PredicateKey key4 = createKey("test2", 2);
      assertNotSame(testObject.getSpyPoint(key1), testObject.getSpyPoint(key2));
      assertNotSame(testObject.getSpyPoint(key2), testObject.getSpyPoint(key3));
      assertNotSame(testObject.getSpyPoint(key3), testObject.getSpyPoint(key4));
   }

   @Test
   public void testGetPredicateKey() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key1 = createKey("test1", 2);
      PredicateKey key2 = createKey("test1", 2);
      PredicateKey key3 = createKey("test1", 1);
      PredicateKey key4 = createKey("test2", 1);
      assertNotSame(key1, key2);

      assertSame(key1, testObject.getSpyPoint(key1).getPredicateKey());
      assertSame(key1, testObject.getSpyPoint(key2).getPredicateKey());
      assertSame(key3, testObject.getSpyPoint(key3).getPredicateKey());
      assertSame(key4, testObject.getSpyPoint(key4).getPredicateKey());
   }

   @Test
   public void testGetSpyPoints() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey[] keys = {createKey("test1", 0), createKey("test1", 2), createKey("test2", 2)};
      SpyPoints.SpyPoint[] spyPoints = new SpyPoints.SpyPoint[keys.length];

      assertTrue(testObject.getSpyPoints().isEmpty());
      for (int i = 0; i < keys.length; i++) {
         spyPoints[i] = testObject.getSpyPoint(keys[i]);
         assertEquals(i + 1, testObject.getSpyPoints().size());
      }

      for (int i = 0; i < keys.length; i++) {
         assertSame(spyPoints[i], testObject.getSpyPoints().get(keys[i]));
      }
   }

   @Test
   public void testSetSpyPointOnlyAltersSingleSpyPoint() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key1 = createKey("test1", 2);
      PredicateKey key2 = createKey("test2", 2);
      SpyPoints.SpyPoint sp1 = testObject.getSpyPoint(key1);
      SpyPoints.SpyPoint sp2 = testObject.getSpyPoint(key2);

      assertFalse(sp1.isSet());
      assertFalse(sp2.isSet());

      testObject.setSpyPoint(key1, true);
      assertTrue(sp1.isSet());
      assertFalse(sp2.isSet());

      testObject.setSpyPoint(key1, true);
      assertTrue(sp1.isSet());
      assertFalse(sp2.isSet());

      testObject.setSpyPoint(key1, false);
      assertFalse(sp1.isSet());
      assertFalse(sp2.isSet());
   }

   @Test
   public void testSetSpyPointWorksEvenBeforeGetSpyPointCalled() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key = createKey("test", 2);
      testObject.setSpyPoint(key, true);
      assertTrue(testObject.getSpyPoint(key).isSet());
   }

   @Test
   public void testSpyPointAffectedBySetTraceEnabledCallAfterItWasCreated() {
      SpyPoints testObject = new SpyPoints(kb);
      SpyPoints.SpyPoint sp = testObject.getSpyPoint(createKey("test", 1));
      assertFalse(sp.isEnabled());
      testObject.setTraceEnabled(true);
      assertTrue(sp.isEnabled());
      testObject.setTraceEnabled(false);
      assertFalse(sp.isEnabled());
   }

   @Test
   public void testSpyPointAffectedBySetTraceEnabledCallBeforeItWasCreated() {
      SpyPoints testObject = new SpyPoints(kb);
      testObject.setTraceEnabled(true);
      SpyPoints.SpyPoint sp = testObject.getSpyPoint(createKey("test", 1));
      assertTrue(sp.isEnabled());
      testObject.setTraceEnabled(false);
      assertFalse(sp.isEnabled());
   }

   @Test
   public void testSetTraceEnabledIndependantOfSetSpyPoint() {
      SpyPoints testObject = new SpyPoints(kb);
      PredicateKey key = createKey("test", 2);

      SpyPoints.SpyPoint sp = testObject.getSpyPoint(key);
      assertTrue(sp.isSet() == false && sp.isEnabled() == false);

      testObject.setTraceEnabled(true);
      assertTrue(sp.isSet() == false && sp.isEnabled() == true);

      testObject.setTraceEnabled(false);
      assertTrue(sp.isSet() == false && sp.isEnabled() == false);

      testObject.setSpyPoint(key, true);
      assertTrue(sp.isSet() == true && sp.isEnabled() == true);

      testObject.setTraceEnabled(true);
      assertTrue(sp.isSet() == true && sp.isEnabled() == true);

      testObject.setTraceEnabled(false);
      assertTrue(sp.isSet() == true && sp.isEnabled() == true);

      testObject.setTraceEnabled(true);
      assertTrue(sp.isSet() == true && sp.isEnabled() == true);

      testObject.setSpyPoint(key, false);
      assertTrue(sp.isSet() == false && sp.isEnabled() == true);
   }

   @Test
   public void testSpyPointUpdatesObserver() {
      // add a listener to the KnowledgeBase's ProjogListeners
      // so we can keep track of ProjogEvent objects created by the SpyPoint
      final SimpleProjogListener listener = new SimpleProjogListener();
      kb.getProjogListeners().addListener(listener);

      PredicateKey key = createKey("test", 1);
      DynamicUserDefinedPredicateFactory pf = new DynamicUserDefinedPredicateFactory(kb, key);
      pf.addFirst(ClauseModel.createClauseModel(structure("test", new Variable())));
      kb.getPredicates().addUserDefinedPredicate(pf);

      SpyPoints testObject = new SpyPoints(kb);
      SpyPoints.SpyPoint sp = testObject.getSpyPoint(key);

      // make a number of log calls to the spy point -
      // the observer should not be updated with any of them as the spy point is not set
      assertFalse(sp.isSet());
      sp.logCall(this, new Term[] {atom("a")});
      sp.logExit(this, new Term[] {atom("b")}, 1);
      sp.logFail(this, new Term[] {atom("c")});
      sp.logRedo(this, new Term[] {atom("d")});
      assertTrue(listener.isEmpty());

      // set the spy point and then make a number of log calls to the spy point -
      // the observer should now be updated with each call in the order they are made
      testObject.setSpyPoint(key, true);
      sp.logCall(this, new Term[] {atom("z")});
      sp.logExit(this, new Term[] {list(atom("a"), variable("X"))}, 0);
      sp.logFail(this, new Term[] {structure("c", EmptyList.EMPTY_LIST, atom("z"), integerNumber(1))});
      sp.logRedo(this, new Term[] {new Variable()});
      assertEquals(4, listener.size());
      assertProjogEvent(listener.get(0), "CALL", "test(z)");
      assertProjogEvent(listener.get(1), "EXIT", "test([a,X])");
      assertProjogEvent(listener.get(2), "FAIL", "test(c([], z, 1))");
      assertProjogEvent(listener.get(3), "REDO", "test(_)");
   }

   private void assertProjogEvent(String event, String expectedType, String expectedMessage) {
      assertEquals(expectedType + expectedMessage, event);
   }

   private PredicateKey createKey(String name, int numArgs) {
      return new PredicateKey(name, numArgs);
   }
}
