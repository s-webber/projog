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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TermFactory.variable;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.udp.InterpretedUserDefinedPredicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Atom;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;

public class UnknownPredicateTest {
   private static final String FUNCTOR = "UnknownPredicateTest";

   @Test
   public void testUnknownPredicate() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);
      Term terms = StructureFactory.createStructure(FUNCTOR, new Term[] {variable()});

      // create UnknownPredicate for a not-yet-defined UnknownPredicateTest/1 predicate
      UnknownPredicate e = new UnknownPredicate(kb, key);
      assertTrue(e.isRetryable());

      // assert that FAIL returned when UnknownPredicateTest/1 not yet defined
      assertSame(PredicateUtils.FALSE, e.getPredicate(terms));

      // define UnknownPredicateTest/1
      kb.getPredicates().createOrReturnUserDefinedPredicate(key);

      // assert that new InterpretedUserDefinedPredicate is returned once UnknownPredicateTest/1 defined
      assertSame(InterpretedUserDefinedPredicate.class, e.getPredicate(terms).getClass());
      assertNotSame(e.getPredicate(terms), e.getPredicate(terms));
   }

   @Test
   public void testPreprocess_still_unknown() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);

      // create UnknownPredicate for a not-yet-defined predicate
      UnknownPredicate original = new UnknownPredicate(kb, key);

      PredicateFactory result = original.preprocess(StructureFactory.createStructure(FUNCTOR, new Term[] {new Atom("a")}));

      assertSame(original, result);
   }

   @Test
   public void testPreprocess_when_known() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);

      // create UnknownPredicate for a predicate represented by a mock PredicateFactory
      UnknownPredicate original = new UnknownPredicate(kb, key);
      PredicateFactory mockKnownPredicateFactory = mock(PredicateFactory.class);
      kb.getPredicates().addPredicateFactory(key, mockKnownPredicateFactory);
      PredicateFactory mockPreprocessedPredicateFactory = mock(PredicateFactory.class);
      Term arg = StructureFactory.createStructure(FUNCTOR, new Term[] {new Atom("a")});
      when(mockKnownPredicateFactory.preprocess(arg)).thenReturn(mockPreprocessedPredicateFactory);

      PredicateFactory result = original.preprocess(StructureFactory.createStructure(FUNCTOR, new Term[] {new Atom("a")}));

      assertSame(mockPreprocessedPredicateFactory, result);
      verify(mockKnownPredicateFactory).preprocess(arg);
      verifyNoMoreInteractions(mockKnownPredicateFactory, mockPreprocessedPredicateFactory);
   }
}
