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
package org.projog.core;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

public class UnknownPredicateTest {
   @Test
   public void testUnknownPredicate() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey("UnknownPredicateTest", 1);

      // create UnknownPredicate for a not-yet-defined UnknownPredicateTest/1 predicate
      UnknownPredicate e = new UnknownPredicate(kb, key);
      assertTrue(e.isRetryable());

      // assert that FAIL returned when UnknownPredicateTest/1 not yet defined
      assertSame(AbstractSingletonPredicate.FAIL, e.getPredicate());
      assertSame(AbstractSingletonPredicate.FAIL, e.getPredicate());

      // define UnknownPredicateTest/1
      kb.createOrReturnUserDefinedPredicate(key);

      // assert that new InterpretedUserDefinedPredicate is returned once UnknownPredicateTest/1 defined
      assertSame(InterpretedUserDefinedPredicate.class, e.getPredicate().getClass());
      assertSame(InterpretedUserDefinedPredicate.class, e.getPredicate().getClass());
      assertNotSame(e.getPredicate(), e.getPredicate());
   }
}
