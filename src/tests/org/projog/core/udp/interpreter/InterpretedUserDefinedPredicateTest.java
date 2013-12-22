/*
 * Copyright 2013 S Webber
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
package org.projog.core.udp.interpreter;

import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.variable;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateKey;
import org.projog.core.SpyPoints;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class InterpretedUserDefinedPredicateTest extends TestCase {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();
   private final PredicateKey key = PredicateKey.createForTerm(atom("test"));
   private final SpyPoints.SpyPoint spyPoint = kb.getSpyPoints().getSpyPoint(key);
   private final DummyClauseAction singleResultA = new DummyClauseAction(atom("a"));
   private final DummyClauseAction singleResultB = new DummyClauseAction(atom("b"));
   private final DummyClauseAction singleResultC = new DummyClauseAction(atom("c"));
   private final DummyClauseAction multiResultXYZ = new DummyClauseAction(atom("x"), atom("y"), atom("z"));

   public void testEmpty() {
      InterpretedUserDefinedPredicate p = getInterpretedUserDefinedPredicate();
      assertTrue(p.isRetryable());
      assertFalse(p.evaluate());
   }

   public void testSingleClauseActionMatchingImmutableInputArg() {
      assertEvaluatesOnce(atom("a"), singleResultA);
      assertDoesNotEvaluate(atom("b"), singleResultA);
   }

   public void testImmutableInputArgMatchingMoreThanOnce() {
      ClauseAction[] rows = {singleResultA, singleResultA, singleResultA};
      assertEvaluates(atom("a"), rows.length, rows);
   }

   public void testManyClauseActionsMatchingImmutableInputArg() {
      ClauseAction[] rows = {singleResultA, singleResultB, singleResultC};
      assertEvaluatesOnce(atom("a"), rows);
      assertEvaluatesOnce(atom("b"), rows);
      assertEvaluatesOnce(atom("c"), rows);
      assertDoesNotEvaluate(atom("d"), rows);
   }

   public void testSingleRetryableClauseActionsMatchingImmutableInputArg() {
      assertEvaluatesOnce(atom("x"), multiResultXYZ);
      assertEvaluatesOnce(atom("y"), multiResultXYZ);
      assertEvaluatesOnce(atom("z"), multiResultXYZ);
      assertDoesNotEvaluate(atom("d"), multiResultXYZ);
   }

   public void testMixtureOfClauseActionsMatchingImmutableInputArg() {
      ClauseAction[] rows = {singleResultA, singleResultB, multiResultXYZ, singleResultC};
      assertEvaluatesOnce(atom("a"), rows);
      assertEvaluatesOnce(atom("b"), rows);
      assertEvaluatesOnce(atom("c"), rows);
      assertEvaluatesOnce(atom("x"), multiResultXYZ);
      assertEvaluatesOnce(atom("y"), multiResultXYZ);
      assertEvaluatesOnce(atom("z"), multiResultXYZ);
      assertDoesNotEvaluate(atom("d"), rows);
   }

   private void assertEvaluatesOnce(Term inputArg, ClauseAction... rows) {
      assertEvaluates(inputArg, 1, rows);
   }

   private void assertEvaluates(Term inputArg, int timesMatched, ClauseAction... rows) {
      InterpretedUserDefinedPredicate p = getInterpretedUserDefinedPredicate(rows);
      Term[] queryArgs = {inputArg};
      for (int i = 0; i < timesMatched; i++) {
         assertTrue(p.evaluate(queryArgs));
      }
      assertFalse(p.evaluate(queryArgs));
   }

   private void assertDoesNotEvaluate(Term inputArg, ClauseAction... rows) {
      InterpretedUserDefinedPredicate p = getInterpretedUserDefinedPredicate(rows);
      Term[] queryArgs = {inputArg};
      assertFalse(p.evaluate(queryArgs));
   }

   public void testSingleClauseActionMatchingVariable() {
      assertEvaluateWithVariableInputArgument(singleResultA);
   }

   public void testManyClauseActionsMatchingVariable() {
      assertEvaluateWithVariableInputArgument(singleResultA, singleResultB, singleResultC);
   }

   public void testSingleRetryableClauseActionsMatchingVariable() {
      assertEvaluateWithVariableInputArgument(multiResultXYZ);
   }

   public void testMixtureOfClauseActionsMatchingVariable() {
      assertEvaluateWithVariableInputArgument(singleResultA, singleResultB, multiResultXYZ, singleResultC);
   }

   private void assertEvaluateWithVariableInputArgument(DummyClauseAction... rows) {
      InterpretedUserDefinedPredicate p = getInterpretedUserDefinedPredicate(rows);
      Variable v = variable("X");
      Term[] queryArgs = {v};
      for (DummyClauseAction row : rows) {
         for (Term t : row.terms) {
            assertTrue(p.evaluate(queryArgs));
            assertSame(t, v.getTerm());
         }
      }
      assertFalse(p.evaluate(queryArgs));
      assertSame(v, v.getTerm());
   }

   private InterpretedUserDefinedPredicate getInterpretedUserDefinedPredicate(ClauseAction... rows) {
      List<ClauseAction> list = new ArrayList<ClauseAction>();
      for (ClauseAction row : rows) {
         list.add(row);
      }
      return new InterpretedUserDefinedPredicate(key, spyPoint, list.iterator());
   }

   private static class DummyClauseAction implements ClauseAction {
      final Term[] terms;
      int ctr;

      DummyClauseAction(Term... terms) {
         this.terms = terms;
      }

      @Override
      public ClauseAction getFree() {
         return new DummyClauseAction(terms);
      }

      @Override
      public boolean evaluate(Term[] queryArgs) {
         while (true) {
            if (ctr == terms.length) {
               return false;
            }
            if (ctr > 0) {
               queryArgs[0].backtrack();
            }
            Term t = terms[ctr++];
            if (t.unify(queryArgs[0])) {
               return true;
            }
         }
      }

      @Override
      public boolean couldReEvaluationSucceed() {
         return terms.length > 0;
      }
   };
}