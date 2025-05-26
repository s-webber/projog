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
package org.projog.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.StringReader;

import org.junit.Test;
import org.projog.core.ProjogException;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class QueryPlanTest {
   /**
    * Confirm that a QueryPlan can be evaluated multiple times.
    */
   @Test
   public void testPlanReusable() {
      Projog projog = new Projog();
      projog.consultReader(new StringReader("test(a).test(b).test(c)."));
      QueryPlan plan = projog.createPlan("test(X).");

      QueryResult result1 = plan.executeQuery();
      QueryResult result2 = plan.executeQuery();
      QueryStatement statement = plan.createStatement();
      statement.setAtomName("X", "b");
      QueryResult result3 = statement.executeQuery();

      assertTrue(result1.next());
      assertEquals("a", result1.getAtomName("X"));

      assertTrue(result2.next());
      assertEquals("a", result2.getAtomName("X"));

      assertTrue(result3.next());
      assertFalse(result3.next());

      assertTrue(result1.next());
      assertEquals("b", result1.getAtomName("X"));

      assertTrue(result1.next());
      assertEquals("c", result1.getAtomName("X"));

      assertTrue(result2.next());
      assertEquals("b", result2.getAtomName("X"));

      assertTrue(result2.next());
      assertEquals("c", result2.getAtomName("X"));
   }

   /**
    * Confirm that QUeryPlan calls PreprocessablePredicateFactory.preprocess(Term).
    */
   @Test
   public void testPreprocessed() {
      PreprocessablePredicateFactory mockPreprocessablePredicateFactory = mock(PreprocessablePredicateFactory.class);
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      Projog projog = new Projog();
      projog.addPredicateFactory(new PredicateKey("test", 0), mockPreprocessablePredicateFactory);

      when(mockPreprocessablePredicateFactory.preprocess(new Atom("test"))).thenReturn(mockPredicateFactory);
      when(mockPredicateFactory.getPredicate(new Term[0])).thenReturn(PredicateUtils.TRUE);

      QueryPlan plan = projog.createPlan("test.");
      verify(mockPreprocessablePredicateFactory).preprocess(new Atom("test"));

      plan.executeOnce();
      plan.executeOnce();
      plan.executeOnce();
      verify(mockPredicateFactory, times(3)).getPredicate(new Term[0]);

      verifyNoMoreInteractions(mockPreprocessablePredicateFactory, mockPredicateFactory);
   }

   /**
    * Example of evaluating the same query as both a QueryPlan and a QueryStatement.
    * <p>
    * The QueryPlan version is able to determine that it is exhausted while the QueryStatement version does not.
    */
   @Test
   public void testComparePlanToStatement() {
      Projog projog = new Projog();
      projog.consultReader(new StringReader("test(1, a).test(_, b).test(3, c)."));
      String query = "test(2, X).";

      QueryPlan plan = projog.createPlan(query);
      QueryStatement statement = projog.createStatement(query);

      QueryResult planResult = plan.executeQuery();
      QueryResult statementResult = statement.executeQuery();

      assertTrue(planResult.next());
      assertTrue(statementResult.next());

      assertEquals("b", planResult.getAtomName("X"));
      assertEquals("b", statementResult.getAtomName("X"));

      assertTrue(planResult.isExhausted());
      assertFalse(statementResult.isExhausted());
   }

   @Test
   public void testExecuteOnce() {
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      when(mockPredicateFactory.getPredicate(new Term[0])).thenReturn(PredicateUtils.TRUE);
      Projog projog = new Projog();
      projog.addPredicateFactory(new PredicateKey("mock", 0), mockPredicateFactory);

      QueryPlan plan = projog.createPlan("repeat, mock.");
      plan.executeOnce();

      verify(mockPredicateFactory).getPredicate(new Term[0]);
      verifyNoMoreInteractions(mockPredicateFactory);
   }

   @Test
   public void testExecuteOnceNoSolution() {
      QueryPlan p = new Projog().createPlan("true, true, fail.");
      try {
         p.executeOnce();
         fail();
      } catch (ProjogException projogException) {
         assertEquals("Failed to find a solution for: ,(true, ,(true, fail))", projogException.getMessage());
      }
   }

   @Test
   public void testMoreThanOneSentenceInQuery() {
      try {
         new Projog().createPlan("X is 1. Y is 2.");
         fail();
      } catch (ProjogException e) {
         assertEquals("org.projog.core.ProjogException caught parsing: X is 1. Y is 2.", e.getMessage());
         assertEquals("More input found after . in X is 1. Y is 2.", e.getCause().getMessage());
      }
   }
}
