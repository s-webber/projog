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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;
import org.projog.core.term.Variable;

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

      assertSame(Conjunction.class, optimised.getClass());
      assertFalse(optimised.isRetryable());
      assertSame(PredicateUtils.TRUE, optimised.getPredicate(term));
   }

   @Test
   public void testPreprocess_OptimisedSingletonConjuction_with_variables() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("X=6, \\+ atom(X).");
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);

      assertSame(Conjunction.class, optimised.getClass());
      assertFalse(optimised.isRetryable());
      assertFalse(optimised.isAlwaysCutOnBacktrack());
      Map<Variable, Term> sharedVariables = new HashMap<>();
      Term copy = term.copy(sharedVariables);
      assertSame(PredicateUtils.TRUE, optimised.getPredicate(copy));
      Variable variable = (Variable) sharedVariables.values().iterator().next();
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
      Predicate predicate = optimised.getPredicate(term);

      assertSame(Conjunction.class, optimised.getClass());
      assertTrue(optimised.isRetryable());
      assertFalse(optimised.isAlwaysCutOnBacktrack());
      assertEquals("org.projog.core.predicate.builtin.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
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
      Predicate predicate = optimised.getPredicate(term);

      assertSame(Conjunction.class, optimised.getClass());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.predicate.builtin.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
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
      Predicate predicate = optimised.getPredicate(term);

      assertSame(Conjunction.class, optimised.getClass());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.predicate.builtin.flow.RepeatSetAmount$RepeatSetAmountPredicate", predicate.getClass().getName());
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
      Predicate predicate = optimised.getPredicate(term);

      assertSame(Conjunction.class, optimised.getClass());
      assertTrue(optimised.isRetryable());
      assertEquals("org.projog.core.predicate.builtin.compound.Conjunction$ConjunctionPredicate", predicate.getClass().getName());
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

   @ParameterizedTest
   @ValueSource(strings = {"!,true.", "true,!.", "!,true,!.", "repeat,!.", "repeat,!,true.", "!,repeat,!.",})
   public void testIsAlwaysCutOnBacktrack_true(String clause) {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm(clause);
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);
      assertTrue(optimised.isAlwaysCutOnBacktrack());
   }

   @ParameterizedTest
   @ValueSource(strings = {
               "true,true.",
               "repeat,repeat.",
               "true,repeat.",
               "repeat,true.",
               "!,repeat.",
               "true,!,repeat.",
               "!,true,!,repeat.",
               "repeat,!,repeat.",
               "!,repeat,!,repeat.",})
   public void testIsAlwaysCutOnBacktrack_false(String clause) {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm(clause);
      Conjunction c = (Conjunction) kb.getPredicates().getPredicateFactory(term);
      PredicateFactory optimised = c.preprocess(term);
      assertFalse(optimised.isAlwaysCutOnBacktrack());
   }
}
