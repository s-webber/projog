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
package org.projog.core.function.compound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;
import org.projog.core.term.Variable;
import org.projog.core.udp.PredicateUtils;

public class ConjunctionTest {
   @Test
   public void testPreprocess_cannot_optimise_when_both_arguments_are_variables() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("X, Y.");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);

      assertSame(c, optimised);
   }

   @Test
   public void testPreprocess_cannot_optimise_when_first_argument_is_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("X, true.");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);

      assertSame(c, optimised);
   }

   @Test
   public void testPreprocess_cannot_optimise_when_second_argument_is_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("true, Y.");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);

      assertSame(c, optimised);
   }

   @Test
   public void testPreprocess_OptimisedSingletonConjuction() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("true, true.");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);

      assertEquals("org.projog.core.function.compound.Conjunction$OptimisedSingletonConjuction", optimised.getClass().getName());
      assertFalse(optimised.isRetryable());
      assertSame(PredicateUtils.TRUE, optimised.getPredicate(term.getArgs()));
   }

   @Test
   public void testPreprocess_OptimisedSingletonConjuction_with_variables() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("X=6, \\+ atom(X).");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);

      assertEquals("org.projog.core.function.compound.Conjunction$OptimisedSingletonConjuction", optimised.getClass().getName());
      assertFalse(optimised.isRetryable());
      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Term copy = term.copy(sharedVariables);
      assertSame(PredicateUtils.TRUE, optimised.getPredicate(copy.getArgs()));
      Variable variable = sharedVariables.values().iterator().next();
      // confirm the backtrack implemented by Not did not unassign X
      assertEquals("X", variable.getId());
      assertEquals(new IntegerNumber(6), variable.getTerm());
   }

   @Test
   public void testPreprocess_first_argument_retryable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("repeat(2), true.");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);
      Predicate predicate = optimised.getPredicate(term.getArgs());

      assertEquals("org.projog.core.function.compound.Conjunction$OptimisedRetryableConjuction", optimised.getClass().getName());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.function.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertFalse(predicate.couldReevaluationSucceed());
      assertFalse(predicate.evaluate());
   }

   @Test
   public void testPreprocess_first_argument_retryable_with_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      TermFormatter tf = kb.getTermFormatter();
      Term term = parseTerm("member(X, [a,b]), Y=X.");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);
      Predicate predicate = optimised.getPredicate(term.getArgs());

      assertEquals("org.projog.core.function.compound.Conjunction$OptimisedRetryableConjuction", optimised.getClass().getName());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.function.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertEquals("member(a, [a,b]) , a = a", tf.formatTerm(term));
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertEquals("member(b, [a,b]) , b = b", tf.formatTerm(term));
      assertFalse(predicate.couldReevaluationSucceed());
      assertFalse(predicate.evaluate());
   }

   @Test
   public void testPreprocess_second_argument_retryable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("true, repeat(2).");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);
      Predicate predicate = optimised.getPredicate(term.getArgs());

      assertEquals("org.projog.core.function.compound.Conjunction$OptimisedRetryableConjuction", optimised.getClass().getName());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.function.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertFalse(predicate.couldReevaluationSucceed());
      assertFalse(predicate.evaluate());
   }

   @Test
   public void testPreprocess_both_arguments_retryable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("member(X, [2,3]), repeat(X).");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);
      Predicate predicate = optimised.getPredicate(term.getArgs());

      assertEquals("org.projog.core.function.compound.Conjunction$OptimisedRetryableConjuction", optimised.getClass().getName());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.function.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertTrue(predicate.couldReevaluationSucceed());
      assertTrue(predicate.evaluate());
      assertFalse(predicate.couldReevaluationSucceed());
      assertFalse(predicate.evaluate());
   }
}
