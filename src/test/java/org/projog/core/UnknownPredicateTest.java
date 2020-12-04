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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TestUtils.variable;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

public class UnknownPredicateTest {
   private static final String FUNCTOR = "UnknownPredicateTest";

   @Test
   public void testUnknownPredicate() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);

      // create UnknownPredicate for a not-yet-defined UnknownPredicateTest/1 predicate
      UnknownPredicate e = new UnknownPredicate(kb, key);
      assertTrue(e.isRetryable());

      // assert that FAIL returned when UnknownPredicateTest/1 not yet defined
      assertSame(AbstractSingletonPredicate.FAIL, e.getPredicate(variable()));

      // define UnknownPredicateTest/1
      kb.createOrReturnUserDefinedPredicate(key);

      // assert that new InterpretedUserDefinedPredicate is returned once UnknownPredicateTest/1 defined
      assertSame(InterpretedUserDefinedPredicate.class, e.getPredicate(variable()).getClass());
      assertNotSame(e.getPredicate(variable()), e.getPredicate(variable()));
   }

   @Test
   public void testPreprocess_still_unknown() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);

      // create UnknownPredicate for a not-yet-defined predicate
      UnknownPredicate original = new UnknownPredicate(kb, key);

      PredicateFactory result = original.preprocess(Structure.createStructure(FUNCTOR, new Term[] {new Atom("a")}));

      assertSame(original, result);
   }

   @Test
   public void testPreprocess_not_PreprocessablePredicateFactory() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);

      // create UnknownPredicate for a predicate represented by a mock PredicateFactory (note not a PreprocessablePredicateFactory)
      UnknownPredicate original = new UnknownPredicate(kb, key);
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      kb.addPredicateFactory(key, mockPredicateFactory);

      PredicateFactory result = original.preprocess(Structure.createStructure(FUNCTOR, new Term[] {new Atom("a")}));

      assertSame(mockPredicateFactory, result);
      verifyZeroInteractions(mockPredicateFactory);
   }

   @Test
   public void testPreprocess_PreprocessablePredicateFactory() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      PredicateKey key = new PredicateKey(FUNCTOR, 1);

      // create UnknownPredicate for a predicate represented by a mock PreprocessablePredicateFactory
      UnknownPredicate original = new UnknownPredicate(kb, key);
      PreprocessablePredicateFactory mockPreprocessablePredicateFactory = mock(PreprocessablePredicateFactory.class);
      kb.addPredicateFactory(key, mockPreprocessablePredicateFactory);
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      Term arg = Structure.createStructure(FUNCTOR, new Term[] {new Atom("a")});
      when(mockPreprocessablePredicateFactory.preprocess(arg)).thenReturn(mockPredicateFactory);

      PredicateFactory result = original.preprocess(Structure.createStructure(FUNCTOR, new Term[] {new Atom("a")}));

      assertSame(mockPredicateFactory, result);
      verify(mockPreprocessablePredicateFactory).preprocess(arg);
      verifyNoMoreInteractions(mockPreprocessablePredicateFactory, mockPredicateFactory);
   }
}
