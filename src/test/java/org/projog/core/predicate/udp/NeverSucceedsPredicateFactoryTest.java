/*
 * Copyright 2021 S. Webber
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
import static org.junit.Assert.assertSame;
import static org.projog.TestUtils.array;
import static org.projog.TermFactory.atom;

import org.junit.Before;
import org.junit.Test;
import org.projog.SimpleProjogListener;
import org.projog.TestUtils;
import org.projog.core.event.ProjogListeners;
import org.projog.core.event.SpyPoints;
import org.projog.core.event.SpyPoints.SpyPoint;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

public class NeverSucceedsPredicateFactoryTest {
   private SpyPoints spyPoints;
   private NeverSucceedsPredicateFactory testObject;
   private Term[] queryArgs = array(atom("a"), atom("b"), atom("c"));
   private SimpleProjogListener listener;

   @Before
   public void before() {
      this.listener = new SimpleProjogListener();
      ProjogListeners observable = new ProjogListeners();
      observable.addListener(listener);
      this.spyPoints = new SpyPoints(observable, TestUtils.createTermFormatter());
      SpyPoint spyPoint = spyPoints.getSpyPoint(new PredicateKey("test", 3));

      this.testObject = new NeverSucceedsPredicateFactory(spyPoint);
   }

   @Test
   public void testGetPredicate_spy_point_disabled() {
      Predicate predicate = testObject.getPredicate(queryArgs);

      assertSame(PredicateUtils.FALSE, predicate);
      assertEquals("", listener.result());
   }

   @Test
   public void testGetPredicate_spy_point_enabled() {
      spyPoints.setTraceEnabled(true);

      Predicate predicate = testObject.getPredicate(queryArgs);

      assertSame(PredicateUtils.FALSE, predicate);
      assertEquals("CALLtest(a, b, c)FAILtest(a, b, c)", listener.result());
   }

   @Test
   public void testIsRetryable() {
      assertFalse(testObject.isRetryable());
   }
}
