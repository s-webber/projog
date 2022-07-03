/*
 * Copyright 2022 S. Webber
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
package org.projog.core.predicate.builtin.clp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.structure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Variable;

public class ClpExpressionsTest {
   private static final Structure DUMMY_TERM = structure("dummy_clp_expression", integerNumber(7));
   private static final PredicateKey DUMMY_KEY = PredicateKey.createForTerm(DUMMY_TERM);

   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testToExpressionClpVariable() {
      ClpExpressions expressions = new ClpExpressions(kb);
      ClpVariable v = new ClpVariable();
      Set<ClpVariable> vars = new HashSet<>();

      assertSame(v, expressions.toExpression(v, vars));
      assertEquals(1, vars.size());
      assertTrue(vars.contains(v));
   }

   @Test
   public void testToExpressionVariable() {
      ClpExpressions expressions = new ClpExpressions(kb);
      Variable projogVariable = new Variable();
      Set<ClpVariable> vars = new HashSet<>();

      ClpVariable clpVariable = (ClpVariable) expressions.toExpression(projogVariable, vars);
      assertEquals(1, vars.size());
      assertTrue(vars.contains(clpVariable));
      assertSame(clpVariable, projogVariable.getTerm());
   }

   @Test
   public void testToExpressionIntegerNumber() {
      ClpExpressions expressions = new ClpExpressions(kb);
      IntegerNumber i = integerNumber(42);

      FixedValue expression = (FixedValue) expressions.toExpression(i, Collections.emptySet());
      assertSame(i.getLong(), expression.getMin(null));
   }

   @Test
   public void testToExpressionDecimalFraction() {
      ClpExpressions expressions = new ClpExpressions(kb);
      DecimalFraction d = new DecimalFraction(1.5);

      try {
         expressions.toExpression(d, Collections.emptySet());
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot get CLP expression for term: 1.5 of type: FRACTION", e.getMessage());
      }
   }

   @Test
   public void testToExpressionUnknownAtom() {
      ClpExpressions expressions = new ClpExpressions(kb);
      Atom a = new Atom("a");

      try {
         expressions.toExpression(a, Collections.emptySet());
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find CLP expression: a/0", e.getMessage());
      }
   }

   @Test
   public void testToExpressionPredicate() {
      ClpExpressions expressions = new ClpExpressions(kb);

      // try to use CLP expression factory by a name that there is no match for (expect exception)
      try {
         expressions.toExpression(DUMMY_TERM, Collections.emptySet());
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot find CLP expression: dummy_clp_expression/1", e.getMessage());
      }

      // add new CLP expression factory
      expressions.addClpExpressionFactory(DUMMY_KEY, DummyClpExpressionDefaultConstructor.class.getName());

      // assert that the factory is now using the newly added CLP expression factory
      Expression expression = expressions.toExpression(DUMMY_TERM, Collections.emptySet());
      assertSame(DummyClpExpressionDefaultConstructor.DUMMY_EXPRESSION, expression);
   }

   @Test
   public void testAddDuplicate() {
      ClpExpressions expressions = new ClpExpressions(kb);
      expressions.addClpExpressionFactory(DUMMY_KEY, DummyClpExpressionDefaultConstructor.class.getName());

      try {
         expressions.addClpExpressionFactory(DUMMY_KEY, DummyClpExpressionDefaultConstructor.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined CLP expression: dummy_clp_expression/1", e.getMessage());
      }
   }

   public static class DummyClpExpressionDefaultConstructor implements ClpExpressionFactory {
      private final static Expression DUMMY_EXPRESSION = new FixedValue(180);

      @Override
      public Expression createExpression(Expression[] args) {
         assertEquals(1, args.length);
         long expectedValue = ((IntegerNumber) DUMMY_TERM.getArgument(0)).getLong();
         assertEquals(expectedValue, ((FixedValue) args[0]).getMin(null));
         return DUMMY_EXPRESSION;
      }
   }
}
