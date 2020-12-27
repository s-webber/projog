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
package org.projog.core.predicate.builtin.compound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

public class NotTest {
   @Test
   public void testBacktrackOnSuccess() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("not((X=4, X<3)).");
      Not n = (Not) kb.getPredicates().getPredicateFactory(term);

      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Term copy = term.copy(sharedVariables);
      assertSame(PredicateUtils.TRUE, n.getPredicate(copy.getArgs()));
      Variable variable = sharedVariables.values().iterator().next();
      // confirm the backtrack implemented by Not did not unassign X
      assertEquals("X", variable.getId());
      assertEquals(TermType.VARIABLE, variable.getType());
   }

   @Test
   public void testPreprocess_cannot_optimise_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("not(X).");
      Not n = (Not) kb.getPredicates().getPredicateFactory(term);

      PredicateFactory optimised = n.preprocess(term);

      assertSame(n, optimised);
   }

   @Test
   public void testPreprocess_not_PreprocessablePredicateFactory() {
      KnowledgeBase kb = createKnowledgeBase();
      Term notTerm = parseTerm("not(test(a, b)).");
      Term queryArg = notTerm.getArgument(0);
      // note not a PreprocessablePredicateFactory
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      Predicate mockPredicate = mock(Predicate.class);
      PredicateKey key = PredicateKey.createForTerm(queryArg);
      kb.getPredicates().addPredicateFactory(key, mockPredicateFactory);
      when(mockPredicateFactory.getPredicate(queryArg.getArgs())).thenReturn(mockPredicate);
      when(mockPredicate.evaluate()).thenReturn(true, false, true);

      Not n = (Not) kb.getPredicates().getPredicateFactory(notTerm);
      PredicateFactory optimised = n.preprocess(notTerm);

      assertEquals("org.projog.core.predicate.builtin.compound.Not$OptimisedNot", optimised.getClass().getName());
      assertSame(PredicateUtils.FALSE, optimised.getPredicate(new Term[] {queryArg}));
      assertSame(PredicateUtils.TRUE, optimised.getPredicate(new Term[] {queryArg}));
      assertSame(PredicateUtils.FALSE, optimised.getPredicate(new Term[] {queryArg}));

      verify(mockPredicateFactory, times(3)).getPredicate(queryArg.getArgs());
      verify(mockPredicate, times(3)).evaluate();
      verifyNoMoreInteractions(mockPredicateFactory, mockPredicate);
   }

   @Test
   public void testPreprocess_PreprocessablePredicateFactory() {
      KnowledgeBase kb = createKnowledgeBase();
      Term notTerm = parseTerm("not(test(a, b)).");
      Term queryArg = notTerm.getArgument(0);
      PreprocessablePredicateFactory mockPreprocessablePredicateFactory = mock(PreprocessablePredicateFactory.class);
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      Predicate mockPredicate = mock(Predicate.class);
      PredicateKey key = PredicateKey.createForTerm(queryArg);
      kb.getPredicates().addPredicateFactory(key, mockPreprocessablePredicateFactory);
      when(mockPreprocessablePredicateFactory.preprocess(queryArg)).thenReturn(mockPredicateFactory);
      when(mockPredicateFactory.getPredicate(queryArg.getArgs())).thenReturn(mockPredicate);
      when(mockPredicate.evaluate()).thenReturn(true, false, true);

      Not n = (Not) kb.getPredicates().getPredicateFactory(notTerm);
      PredicateFactory optimised = n.preprocess(notTerm);

      assertEquals("org.projog.core.predicate.builtin.compound.Not$OptimisedNot", optimised.getClass().getName());
      assertSame(PredicateUtils.FALSE, optimised.getPredicate(new Term[] {queryArg}));
      assertSame(PredicateUtils.TRUE, optimised.getPredicate(new Term[] {queryArg}));
      assertSame(PredicateUtils.FALSE, optimised.getPredicate(new Term[] {queryArg}));

      verify(mockPreprocessablePredicateFactory).preprocess(queryArg);
      verify(mockPredicateFactory, times(3)).getPredicate(queryArg.getArgs());
      verify(mockPredicate, times(3)).evaluate();
      verifyNoMoreInteractions(mockPreprocessablePredicateFactory, mockPredicateFactory, mockPredicate);
   }
}
