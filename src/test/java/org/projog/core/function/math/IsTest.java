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
package org.projog.core.function.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class IsTest {
   @Test
   public void testPreprocess_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("X is Y.");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      assertSame(is, is.preprocess(isTerm));
   }

   @Test
   public void testPreprocess_numeric() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("X is 1.");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      PredicateFactory optimised = is.preprocess(isTerm);
      assertEquals("org.projog.core.function.math.Is$Unify", optimised.getClass().getName());
      assertSame(AbstractSingletonPredicate.TRUE, optimised.getPredicate(isTerm.getArgs()));
      assertEquals("is(1, 1)", isTerm.toString());
   }

   @Test
   public void testPreprocess_binary_expression_without_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("X is 3 * 7.");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      PredicateFactory optimised = is.preprocess(isTerm);
      assertEquals("org.projog.core.function.math.Is$Unify", optimised.getClass().getName());
      assertSame(AbstractSingletonPredicate.TRUE, optimised.getPredicate(isTerm.getArgs()));
      assertEquals("is(21, *(3, 7))", isTerm.toString());
   }

   @Test
   public void testPreprocess_binary_expression_with_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("F is C * 9 / 5 + 32.");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      PredicateFactory optimised = is.preprocess(isTerm);
      assertEquals("org.projog.core.function.math.Is$PreprocessedIs", optimised.getClass().getName());

      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Term copy = isTerm.copy(sharedVariables);
      Variable f = null;
      Variable c = null;
      for (Variable v : sharedVariables.values()) {
         if ("F".equals(v.getId())) {
            f = v;
         } else if ("C".equals(v.getId())) {
            c = v;
         }
      }
      c.unify(new IntegerNumber(100));
      assertSame(AbstractSingletonPredicate.TRUE, optimised.getPredicate(copy.getArgs()));
      assertEquals("is(212, +(/(*(100, 9), 5), 32))", copy.toString());
      assertEquals(new IntegerNumber(212), f.getTerm());
   }

   @Test
   public void testPreprocess_unary_expression_without_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("X is abs(-3 * 7).");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      PredicateFactory optimised = is.preprocess(isTerm);
      assertEquals("org.projog.core.function.math.Is$Unify", optimised.getClass().getName());
      assertSame(AbstractSingletonPredicate.TRUE, optimised.getPredicate(isTerm.getArgs()));
      assertEquals("is(21, abs(*(-3, 7)))", isTerm.toString());
   }

   @Test
   public void testPreprocess_unary_expression_with_variable() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("X is abs(1+Y).");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      PredicateFactory optimised = is.preprocess(isTerm);
      assertEquals("org.projog.core.function.math.Is$PreprocessedIs", optimised.getClass().getName());

      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Term copy = isTerm.copy(sharedVariables);
      Variable x = null;
      Variable y = null;
      for (Variable v : sharedVariables.values()) {
         if ("X".equals(v.getId())) {
            x = v;
         } else if ("Y".equals(v.getId())) {
            y = v;
         }
      }
      y.unify(new IntegerNumber(-7));
      assertSame(AbstractSingletonPredicate.TRUE, optimised.getPredicate(copy.getArgs()));
      assertEquals("is(6, abs(+(1, -7)))", copy.toString());
      assertEquals(new IntegerNumber(6), x.getTerm());
   }

   @Ignore
   @Test
   public void testPreprocess_time_test() {
      KnowledgeBase kb = createKnowledgeBase();
      Term isTerm = parseTerm("F is C * 9 / 5 + 32.");

      Is is = (Is) kb.getPredicateFactory(isTerm);
      PredicateFactory optimised = is.preprocess(isTerm);

      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Term copy = isTerm.copy(sharedVariables);
      Variable f = null;
      Variable c = null;
      for (Variable v : sharedVariables.values()) {
         if ("F".equals(v.getId())) {
            f = v;
         } else if ("C".equals(v.getId())) {
            c = v;
         }
      }
      c.unify(new IntegerNumber(100));
      final int numBatches = 1000;
      final int batchSize = 10000;
      int betterCtr = 0;
      for (int i2 = 0; i2 < numBatches; i2++) {
         long now = System.currentTimeMillis();
         for (int i = 0; i < batchSize; i++) {
            optimised.getPredicate(copy.getArgs());
            f.backtrack();
         }
         long duration1 = System.currentTimeMillis() - now;
         now = System.currentTimeMillis();
         for (int i = 0; i < batchSize; i++) {
            is.getPredicate(copy.getArgs());
            f.backtrack();
         }
         long duration2 = System.currentTimeMillis() - now;
         if (duration1 < duration2) {
            betterCtr++;
         }
      }

      // confirm that preprocessed is faster more than 90% of the time
      assertTrue("was: " + betterCtr, betterCtr < numBatches * .9);
   }
}
