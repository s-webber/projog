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
package org.projog.core.predicate.builtin.compound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;

import org.junit.Test;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class LimitTest {
   @Test
   public void testPreprocess_cannot_optimise_variable() {
      Limit o = new Limit();

      Term t = StructureFactory.createStructure("limit", new Term[] {new IntegerNumber(3), new Variable("Y")});
      PredicateFactory optimised = o.preprocess(t);

      assertSame(o, optimised);
   }

   @Test
   public void testPreprocess_not_PreprocessablePredicateFactory() {
      KnowledgeBase kb = createKnowledgeBase();
      Term limitTerm = parseTerm("limit(3, test(a, b)).");
      Term queryArg = limitTerm.secondArgument();
      // note not a PreprocessablePredicateFactory
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      Predicate mockPredicate = mock(Predicate.class);
      PredicateKey key = PredicateKey.createForTerm(queryArg);
      kb.getPredicates().addPredicateFactory(key, mockPredicateFactory);
      when(mockPredicateFactory.getPredicate(queryArg)).thenReturn(mockPredicate);
      when(mockPredicate.evaluate()).thenReturn(true);
      when(mockPredicate.couldReevaluationSucceed()).thenReturn(true);

      Limit o = (Limit) kb.getPredicates().getPredicateFactory(limitTerm);
      PredicateFactory optimised = o.preprocess(limitTerm);

      assertEquals("org.projog.core.predicate.builtin.compound.Limit$OptimisedLimit", optimised.getClass().getName());
      Term queryArgs = StructureFactory.createStructure("limit", new Term[] {limitTerm.firstArgument(), limitTerm.secondArgument()});
      Predicate p = optimised.getPredicate(queryArgs);
      assertEquals("org.projog.core.predicate.builtin.compound.Limit$LimitPredicate", p.getClass().getName());
      assertNotSame(p, optimised.getPredicate(queryArgs));

      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertFalse(p.evaluate());

      verify(mockPredicateFactory, times(2)).getPredicate(queryArg);
      verify(mockPredicate, times(3)).evaluate();
      verify(mockPredicate, times(4)).couldReevaluationSucceed();
      verifyNoMoreInteractions(mockPredicateFactory, mockPredicate);
   }

   @Test
   public void testPreprocess_PreprocessablePredicateFactory() {
      KnowledgeBase kb = createKnowledgeBase();
      Term limitTerm = parseTerm("limit(3, test(a, b)).");
      Term queryArg = limitTerm.secondArgument();
      PreprocessablePredicateFactory mockPreprocessablePredicateFactory = mock(PreprocessablePredicateFactory.class);
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      Predicate mockPredicate = mock(Predicate.class);
      PredicateKey key = PredicateKey.createForTerm(queryArg);
      kb.getPredicates().addPredicateFactory(key, mockPreprocessablePredicateFactory);
      when(mockPreprocessablePredicateFactory.preprocess(queryArg)).thenReturn(mockPredicateFactory);
      when(mockPredicateFactory.getPredicate(queryArg)).thenReturn(mockPredicate);
      when(mockPredicate.evaluate()).thenReturn(true);
      when(mockPredicate.couldReevaluationSucceed()).thenReturn(true);

      Limit o = (Limit) kb.getPredicates().getPredicateFactory(limitTerm);
      PredicateFactory optimised = o.preprocess(limitTerm);

      assertEquals("org.projog.core.predicate.builtin.compound.Limit$OptimisedLimit", optimised.getClass().getName());
      Term queryArgs = StructureFactory.createStructure("limit", new Term[] {limitTerm.firstArgument(), limitTerm.secondArgument()});
      Predicate p = optimised.getPredicate(queryArgs);
      assertEquals("org.projog.core.predicate.builtin.compound.Limit$LimitPredicate", p.getClass().getName());
      assertNotSame(p, optimised.getPredicate(queryArgs));

      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertTrue(p.couldReevaluationSucceed());
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertFalse(p.evaluate());

      verify(mockPreprocessablePredicateFactory).preprocess(queryArg);
      verify(mockPredicateFactory, times(2)).getPredicate(queryArg);
      verify(mockPredicate, times(3)).evaluate();
      verify(mockPredicate, times(4)).couldReevaluationSucceed();
      verifyNoMoreInteractions(mockPreprocessablePredicateFactory, mockPredicateFactory, mockPredicate);
   }
}
