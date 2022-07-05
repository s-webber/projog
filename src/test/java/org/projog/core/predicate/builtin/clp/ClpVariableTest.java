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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.core.ProjogException;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

public class ClpVariableTest {
   @Test(expected = UnsupportedOperationException.class)
   public void testGetName() {
      ClpVariable v = new ClpVariable();
      v.getName();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testGetArgs() {
      ClpVariable v = new ClpVariable();
      v.getArgs();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testGetArgument() {
      ClpVariable v = new ClpVariable();
      v.getArgument(0);
   }

   @Test
   public void testGetNumberOfArguments() {
      ClpVariable v = new ClpVariable();
      assertEquals(0, v.getNumberOfArguments());
   }

   @Test
   public void testGetType() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();

      assertSame(TermType.CLP_VARIABLE, v.getType());

      v.setMin(environment, 7);
      assertSame(TermType.CLP_VARIABLE, v.getType());

      v.setMax(environment, 8);
      assertSame(TermType.CLP_VARIABLE, v.getType());

      v.setMin(environment, 8);
      assertSame(TermType.INTEGER, v.getType());
   }

   @Test
   public void testIsImmutable() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();

      assertFalse(v.isImmutable());

      v.setMin(environment, 7);
      assertFalse(v.isImmutable());
      assertFalse(v.getTerm().isImmutable());

      v.setMax(environment, 8);
      assertFalse(v.isImmutable());
      assertFalse(v.getTerm().isImmutable());

      v.setMin(environment, 8);
      assertFalse(v.isImmutable());
      assertTrue(v.getTerm().isImmutable());
   }

   @Test
   public void testUnify_integer() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore e = new CoreConstraintStore();

      long min = 7;
      long max = 9;

      v.setMin(e, min);
      v.setMax(e, max);
      v = v.getTerm();

      // test successful unify with every value within range
      for (long i = min; i <= max; i++) {
         IntegerNumber n = new IntegerNumber(i);

         assertTrue(n.unify(v));
         assertEquals(TermType.INTEGER, v.getType());
         assertEquals(i, v.getLong());

         v.backtrack();
         assertEquals(TermType.CLP_VARIABLE, v.getType());

         assertTrue(v.unify(n));
         assertEquals(TermType.INTEGER, v.getType());
         assertEquals(i, v.getLong());

         v.backtrack();
         assertEquals(TermType.CLP_VARIABLE, v.getType());
      }

      // test unsuccessful unify with values either side of range
      IntegerNumber maxPlus1 = new IntegerNumber(max + 1);
      assertFalse(maxPlus1.unify(v));
      assertFalse(v.unify(maxPlus1));

      IntegerNumber minMinus1 = new IntegerNumber(min - 1);
      assertFalse(minMinus1.unify(v));
      assertFalse(v.unify(minMinus1));
   }

   @Test
   public void testUnify_variable() {
      ClpVariable v1 = new ClpVariable();
      Variable v2 = new Variable();

      assertTrue(v1.unify(v2));
      assertSame(v1, v2.getTerm());

      v2.backtrack();
      assertSame(v2, v2.getTerm());

      assertTrue(v2.unify(v1));
      assertSame(v1, v2.getTerm());
   }

   @Test
   public void testUnify_clpVariable_self() {
      ClpVariable v1 = new ClpVariable();

      assertTrue(v1.unify(v1));
      assertSame(v1, v1.getTerm());

      v1.backtrack();
      assertSame(v1, v1.getTerm());
   }

   @Test
   public void testUnify_clpVariable_both_unbound() {
      ClpVariable v1 = new ClpVariable();
      ClpVariable v2 = new ClpVariable();

      assertTrue(v1.unify(v2));
      assertSame(v1, v2.getTerm());

      v2.backtrack();
      assertSame(v2, v2.getTerm());

      assertTrue(v2.unify(v1));
      assertSame(v2, v1.getTerm());
   }

   @Test
   public void testUnify_clpVariable_overlap() {
      CoreConstraintStore e = new CoreConstraintStore();

      ClpVariable v1 = new ClpVariable();
      v1.setMin(e, 10);
      v1.setMax(e, 12);
      v1 = v1.getTerm();

      ClpVariable v2 = new ClpVariable();
      v2.setMin(e, 11);
      v2.setMax(e, 14);
      v2 = v2.getTerm();

      assertTrue(v1.unify(v2));
      assertNotSame(v1, v1.getTerm());
      assertNotSame(v2, v1.getTerm());
      assertNotSame(v1, v2.getTerm());
      assertNotSame(v2, v2.getTerm());
      assertSame(v1.getTerm(), v2.getTerm());
      assertEquals(11, v1.getMin(e));
      assertEquals(12, v1.getMax(e));
      assertEquals(11, v2.getMin(e));
      assertEquals(12, v2.getMax(e));

      v1.backtrack();
      assertEquals(10, v1.getMin(e));
      assertEquals(12, v1.getMax(e));
      assertEquals(11, v2.getMin(e));
      assertEquals(12, v2.getMax(e));

      v2.backtrack();
      assertEquals(10, v1.getMin(e));
      assertEquals(12, v1.getMax(e));
      assertEquals(11, v2.getMin(e));
      assertEquals(14, v2.getMax(e));
   }

   @Test
   public void testUnify_clpVariable_fail() {
      CoreConstraintStore e = new CoreConstraintStore();

      ClpVariable v1 = new ClpVariable();
      v1.setMin(e, 10);
      v1.setMax(e, 11);

      ClpVariable v2 = new ClpVariable();
      v2.setMin(e, 12);
      v2.setMax(e, 14);

      assertFalse(v1.unify(v2));
      assertEquals(10, v1.getMin(e));
      assertEquals(11, v1.getMax(e));
      assertEquals(12, v2.getMin(e));
      assertEquals(14, v2.getMax(e));
   }

   @Test
   public void testUnify_decimal() {
      ClpVariable v = new ClpVariable();
      DecimalFraction d = new DecimalFraction(7);

      assertFalse(v.unify(d));
      assertFalse(d.unify(v));

      v.setMin(new CoreConstraintStore(), 7);
      v.setMax(new CoreConstraintStore(), 7);

      assertEquals(d.getDouble(), v.getDouble(), 0);

      assertFalse(v.unify(d));
      assertFalse(d.unify(v));
   }

   @Test
   public void testUnify_atom() {
      ClpVariable v = new ClpVariable();
      Atom a = new Atom("a");
      assertFalse(v.unify(a));
      assertFalse(a.unify(v));
   }

   @Test
   public void testUnify_structure() {
      ClpVariable v = new ClpVariable();
      Term s = Structure.createStructure("test", new Term[] {new Atom("a")});
      assertFalse(v.unify(s));
      assertFalse(s.unify(v));
   }

   @Test
   public void testUnify_list() {
      ClpVariable v = new ClpVariable();
      Term list = ListFactory.createList(new Atom("a"), EmptyList.EMPTY_LIST);
      assertFalse(v.unify(list));
      assertFalse(list.unify(v));
   }

   @Test
   public void testUnify_emptyList() {
      ClpVariable v = new ClpVariable();
      assertFalse(v.unify(EmptyList.EMPTY_LIST));
      assertFalse(EmptyList.EMPTY_LIST.unify(v));
   }

   @Test
   public void testBacktrack() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();

      assertSame(v, v.getTerm());
      assertEquals(Long.MIN_VALUE, v.getMin(environment));
      assertEquals(Long.MAX_VALUE, v.getMax(environment));

      v.backtrack(); // has no affect, nothing to backtrack

      assertSame(v, v.getTerm());
      assertEquals(Long.MIN_VALUE, v.getMin(environment));
      assertEquals(Long.MAX_VALUE, v.getMax(environment));

      v.setMin(environment, 7);

      ClpVariable other1 = v.getTerm();
      assertNotSame(v, other1);
      assertEquals(7, v.getMin(environment));
      assertEquals(Long.MAX_VALUE, v.getMax(environment));
      assertEquals(7, other1.getMin(environment));
      assertEquals(Long.MAX_VALUE, other1.getMax(environment));

      v.setMax(environment, 12);

      ClpVariable other2 = v.getTerm();
      assertSame(other2, other1.getTerm());
      assertNotSame(v, other2);
      assertNotSame(other1, other2);
      assertEquals(7, v.getMin(environment));
      assertEquals(12, v.getMax(environment));
      assertEquals(7, other1.getMin(environment));
      assertEquals(12, other1.getMax(environment));
      assertEquals(7, other2.getMin(environment));
      assertEquals(12, other2.getMax(environment));

      v.backtrack(); // backtrack set minimum to 7, set maximum to 12

      assertSame(v, v.getTerm());
      assertEquals(Long.MIN_VALUE, v.getMin(environment));
      assertEquals(Long.MAX_VALUE, v.getMax(environment));
      assertEquals(7, other1.getMin(environment));
      assertEquals(12, other1.getMax(environment));
      assertEquals(7, other2.getMin(environment));
      assertEquals(12, other2.getMax(environment));

      other1.backtrack(); // backtrack set maximum to 12

      assertSame(other1, other1.getTerm());
      assertEquals(7, other1.getMin(environment));
      assertEquals(Long.MAX_VALUE, other1.getMax(environment));
      assertEquals(7, other2.getMin(environment));
      assertEquals(12, other2.getMax(environment));
   }

   @Test
   public void testBacktrack_structure_example_1() {
      ClpVariable v = new ClpVariable();
      v.setMin(new CoreConstraintStore(), 0);
      v.setMax(new CoreConstraintStore(), 0);

      Term structure = Structure.createStructure("test", new Term[] {v});

      assertNotSame(v, v.getTerm());

      structure.backtrack();

      assertSame(v, v.getTerm());
   }

   @Test
   public void testBacktrack_structure_example_2() {
      ClpVariable v = new ClpVariable();

      Term structure = Structure.createStructure("test", new Term[] {v});

      v.setMin(new CoreConstraintStore(), 0);
      v.setMax(new CoreConstraintStore(), 0);

      assertNotSame(v, v.getTerm());

      structure.backtrack();

      assertSame(v, v.getTerm());
   }

   @Test
   public void testBacktrack_structure_example_3() {
      ClpVariable v = new ClpVariable();

      Term structure = Structure.createStructure("test", new Term[] {v});

      v.setMin(new CoreConstraintStore(), 0);

      assertNotSame(v, v.getTerm());

      structure.backtrack();

      assertSame(v, v.getTerm());
   }

   @Test
   public void testBacktrack_list_example_1() {
      ClpVariable v1 = new ClpVariable();
      v1.setMin(new CoreConstraintStore(), 0);
      v1.setMax(new CoreConstraintStore(), 0);

      ClpVariable v2 = new ClpVariable();
      v2.setMin(new CoreConstraintStore(), 0);
      v2.setMax(new CoreConstraintStore(), 0);

      Term list = ListFactory.createList(v1, v2);

      assertNotSame(v1, v1.getTerm());
      assertNotSame(v2, v2.getTerm());

      list.backtrack();

      assertSame(v1, v1.getTerm());
      assertSame(v2, v2.getTerm());
   }

   @Test
   public void testBacktrack_list_example_2() {
      ClpVariable v1 = new ClpVariable();
      ClpVariable v2 = new ClpVariable();

      Term list = ListFactory.createList(v1, v2);

      v1.setMin(new CoreConstraintStore(), 0);
      v1.setMax(new CoreConstraintStore(), 0);
      v2.setMin(new CoreConstraintStore(), 0);
      v2.setMax(new CoreConstraintStore(), 0);

      assertNotSame(v1, v1.getTerm());
      assertNotSame(v2, v2.getTerm());

      list.backtrack();

      assertSame(v1, v1.getTerm());
      assertSame(v2, v2.getTerm());
   }

   @Test
   public void testBacktrack_list_example_3() {
      ClpVariable v1 = new ClpVariable();
      ClpVariable v2 = new ClpVariable();

      Term list = ListFactory.createList(v1, v2);

      v1.setMin(new CoreConstraintStore(), 0);
      v2.setMax(new CoreConstraintStore(), 0);

      assertNotSame(v1, v1.getTerm());
      assertNotSame(v2, v2.getTerm());

      list.backtrack();

      assertSame(v1, v1.getTerm());
      assertSame(v2, v2.getTerm());
   }

   @Test
   public void testNumeric() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 7);
      v.setMax(environment, 7);

      assertEquals(7, v.getLong());
      assertEquals(7d, v.getDouble(), 0);
   }

   @Test
   public void testNumeric_exception() {
      ClpVariable v = new ClpVariable();
      String expected = "Cannot use CLP_VARIABLE as a number as has more than one possible value: -9223372036854775808..9223372036854775807";

      try {
         v.getLong();
         fail();
      } catch (ProjogException e) {
         assertEquals(expected, e.getMessage());
      }
      try {
         v.getDouble();
         fail();
      } catch (ProjogException e) {
         assertEquals(expected, e.getMessage());
      }
   }

   @Test
   public void testCalculate() {
      ClpVariable v = new ClpVariable();
      assertSame(v, v.calculate(null));
   }

   @Test
   public void testConstraints() {
      ClpVariable v = new ClpVariable();
      Constraint c1 = mock(Constraint.class);
      Constraint c2 = mock(Constraint.class);
      Constraint c3 = mock(Constraint.class);

      assertTrue(v.getConstraints().isEmpty());

      v.addConstraint(c1);
      v.addConstraint(c2);
      assertEquals(Arrays.asList(c1, c2), v.getConstraints());

      // duplicates are allowed - but maybe they shouldn't be? TODO
      v.addConstraint(c2);
      assertEquals(Arrays.asList(c1, c2, c2), v.getConstraints());

      // altering copy of constraints doesn't alter ClpVariable
      v.getConstraints().clear();
      assertEquals(Arrays.asList(c1, c2, c2), v.getConstraints());

      // altering ClpVariable doesn't alter copy of constraints
      List<Constraint> constraints = v.getConstraints();
      v.addConstraint(c3);
      assertEquals(Arrays.asList(c1, c2, c2), constraints);
      assertEquals(Arrays.asList(c1, c2, c2, c3), v.getConstraints());

      assertNotSame(v.getConstraints(), v.getConstraints());
      assertEquals(v.getConstraints(), v.getConstraints());
   }

   @Test
   public void testSetMax() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 7);

      assertSame(ExpressionResult.VALID, v.setMax(environment, 15));
      assertEquals(7, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMax(environment, 15));
      assertEquals(7, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMax(environment, 16));
      assertEquals(7, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMax(environment, 8));
      assertEquals(7, v.getMin(environment));
      assertEquals(8, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMax(environment, 7));
      assertEquals(7, v.getMin(environment));
      assertEquals(7, v.getMax(environment));

      assertSame(ExpressionResult.INVALID, v.setMax(environment, 6));
   }

   @Test
   public void testSetMin() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMax(environment, 15);

      assertSame(ExpressionResult.VALID, v.setMin(environment, 7));
      assertEquals(7, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMin(environment, 7));
      assertEquals(7, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMin(environment, 6));
      assertEquals(7, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMin(environment, 14));
      assertEquals(14, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setMin(environment, 15));
      assertEquals(15, v.getMin(environment));
      assertEquals(15, v.getMax(environment));

      assertSame(ExpressionResult.INVALID, v.setMin(environment, 16));
   }

   @Test
   public void testSetNot() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 6);
      v.setMax(environment, 9);

      assertSame(ExpressionResult.VALID, v.setNot(environment, 6));
      assertEquals(7, v.getMin(environment));
      assertEquals(9, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setNot(environment, 6));
      assertEquals(7, v.getMin(environment));
      assertEquals(9, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setNot(environment, 5));
      assertEquals(7, v.getMin(environment));
      assertEquals(9, v.getMax(environment));

      assertSame(ExpressionResult.VALID, v.setNot(environment, 10));
      assertEquals(7, v.getMin(environment));
      assertEquals(9, v.getMax(environment));
      assertEquals("7..9", v.toString());

      assertSame(ExpressionResult.VALID, v.setNot(environment, 8));
      assertEquals(7, v.getMin(environment));
      assertEquals(9, v.getMax(environment));
      assertEquals("{7, 9}", v.toString());

      assertSame(ExpressionResult.VALID, v.setNot(environment, 9));
      assertEquals(7, v.getMin(environment));
      assertEquals(7, v.getMax(environment));
      assertEquals("7", v.toString());

      assertSame(ExpressionResult.INVALID, v.setNot(environment, 7));
   }

   @Test
   public void testSetMin_updated() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      Constraint constraint = mock(Constraint.class);
      when(constraint.enforce(environment)).thenReturn(ConstraintResult.FAILED);
      v.addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setMin(environment, 6));

      verifyNoMoreInteractions(constraint);

      assertFalse(environment.resolve());

      verify(constraint).enforce(environment);
   }

   @Test
   public void testSetNot_updated() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMax(environment, 10);
      Constraint constraint = mock(Constraint.class);
      when(constraint.enforce(environment)).thenReturn(ConstraintResult.MATCHED);
      v.getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setNot(environment, 6));

      verifyNoMoreInteractions(constraint);

      assertTrue(environment.resolve());

      verify(constraint).enforce(environment);
   }

   @Test
   public void testSetMax_updated() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      Constraint constraint = mock(Constraint.class);
      when(constraint.enforce(environment)).thenReturn(ConstraintResult.UNRESOLVED);
      v.addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setMax(environment, 6));

      verifyNoMoreInteractions(constraint);

      assertTrue(environment.resolve());

      verify(constraint).enforce(environment);
   }

   @Test
   public void testSetMin_no_change() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      Constraint constraint = mock(Constraint.class);
      v.addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setMin(environment, Long.MIN_VALUE));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testSetMax_no_change() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      Constraint constraint = mock(Constraint.class);
      v.getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setMax(environment, Long.MAX_VALUE));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testSetNot_no_change_outside_range() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMin(environment, 10);
      Constraint constraint = mock(Constraint.class);
      v.getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setNot(environment, 11));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testSetNot_no_change_unbound() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMin(environment, 10);
      Constraint constraint = mock(Constraint.class);
      v.getTerm().getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.VALID, v.setNot(environment, 11));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testSetMin_Failed() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMax(environment, 5);
      Constraint constraint = mock(Constraint.class);
      v.getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.INVALID, v.setMin(environment, 6));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testSetMax_Failed() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 7);
      Constraint constraint = mock(Constraint.class);
      v.getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.INVALID, v.setMax(environment, 6));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testSetNot_Failed() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 6);
      v.setMax(environment, 6);
      Constraint constraint = mock(Constraint.class);
      v.getTerm().addConstraint(constraint);

      assertSame(ExpressionResult.INVALID, v.setNot(environment, 6));

      environment.resolve();

      verifyNoMoreInteractions(constraint);
   }

   @Test
   public void testReifyMatched() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 1);
      v.setMax(environment, 1);
      assertSame(ConstraintResult.MATCHED, v.reify(environment));
   }

   @Test
   public void testReifyFailed() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMax(environment, 0);
      assertSame(ConstraintResult.FAILED, v.reify(environment));
   }

   @Test
   public void testReifyUnresolved() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMax(environment, 1);
      assertSame(ConstraintResult.UNRESOLVED, v.reify(environment));
   }

   @Test
   public void testReifyToHigh() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 2);
      v.setMax(environment, 2);
      try {
         v.reify(environment);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1 but got 2", e.getMessage());
      }

   }

   @Test
   public void testReifyToLow() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, -1);
      v.setMax(environment, -1);
      try {
         v.reify(environment);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1 but got -1", e.getMessage());
      }
   }

   @Test
   public void testEnforceMatched() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 1);
      v.setMax(environment, 1);
      assertSame(ConstraintResult.MATCHED, v.enforce(environment));
   }

   @Test
   public void testEnforceMatchedAndUpdated() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, Long.MIN_VALUE);
      v.setMax(environment, Long.MAX_VALUE);
      assertSame(ConstraintResult.MATCHED, v.enforce(environment));
      assertEquals(1, v.getMin(environment));
      assertEquals(1, v.getMax(environment));
   }

   @Test
   public void testEnforceFailed() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMax(environment, 0);
      assertSame(ConstraintResult.FAILED, v.enforce(environment));
   }

   @Test
   public void testEnforceToHigh() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 2);
      v.setMax(environment, 2);
      try {
         v.enforce(environment);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testEnforceToLow() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, -1);
      v.setMax(environment, -1);
      try {
         v.enforce(environment);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testPreventMatched() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 0);
      v.setMax(environment, 0);
      assertSame(ConstraintResult.MATCHED, v.prevent(environment));
   }

   @Test
   public void testPreventMatchedAndUpdated() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, Long.MIN_VALUE);
      v.setMax(environment, Long.MAX_VALUE);
      assertSame(ConstraintResult.MATCHED, v.prevent(environment));
      assertEquals(0, v.getMin(environment));
      assertEquals(0, v.getMax(environment));
   }

   @Test
   public void testPreventFailed() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 1);
      v.setMax(environment, 1);
      assertSame(ConstraintResult.FAILED, v.prevent(environment));
   }

   @Test
   public void testPreventToHigh() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, 2);
      v.setMax(environment, 2);
      try {
         v.prevent(environment);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testPreventToLow() {
      ClpVariable v = new ClpVariable();
      CoreConstraintStore environment = new CoreConstraintStore();
      v.setMin(environment, -1);
      v.setMax(environment, -1);
      try {
         v.prevent(environment);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Expected 0 or 1", e.getMessage());
      }
   }

   @Test
   public void testWalk() {
      // given
      ClpVariable testObject = new ClpVariable();
      @SuppressWarnings("unchecked")
      Consumer<Expression> consumer = mock(Consumer.class);

      // when
      testObject.walk(consumer);

      // then
      verify(consumer).accept(testObject);
      verifyNoMoreInteractions(consumer);
   }

   @Test
   public void testReplace_null() {
      // given
      ClpVariable testObject = new ClpVariable();
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      when(testObject.replace(function)).thenReturn(null);

      // when
      Expression replacement = testObject.replace(function);
      assertSame(testObject, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }

   @Test
   public void testReplace_replacement() {
      // given
      ClpVariable testObject = new ClpVariable();
      org.projog.clp.Variable expectedReplacement = new ClpConstraintStore.Builder().createVariable();
      @SuppressWarnings("unchecked")
      Function<LeafExpression, LeafExpression> function = mock(Function.class);
      when(testObject.replace(function)).thenReturn(expectedReplacement);

      // when
      Expression replacement = testObject.replace(function);
      assertSame(expectedReplacement, replacement);

      // then
      verify(function).apply(testObject);
      verifyNoMoreInteractions(function);
   }

   @Test
   public void testCopy_no_bitset() {
      CoreConstraintStore e = new CoreConstraintStore();
      Constraint c1 = mock(Constraint.class);
      Constraint c2 = mock(Constraint.class);

      ClpVariable original = new ClpVariable();
      original.addConstraint(c1);
      original.setMin(e, 8);
      original.setMax(e, 12);
      original = original.getTerm();

      ClpVariable copy = original.copy();
      assertNotSame(copy, original);
      assertSame(copy, original.getTerm());

      copy.addConstraint(c2);

      original.setMin(e, 9);
      original.setMax(e, 11);

      original.backtrack();

      assertEquals(8, original.getMin(e));
      assertEquals(12, original.getMax(e));
      assertEquals("8..12", original.toString());
      assertEquals(Arrays.asList(c1), original.getConstraints());
      assertEquals(9, copy.getMin(e));
      assertEquals(11, copy.getMax(e));
      assertEquals("9..11", copy.toString());
      assertEquals(Arrays.asList(c1, c2), copy.getTerm().getConstraints());
   }

   @Test
   public void testCopy_bitset() {
      CoreConstraintStore e = new CoreConstraintStore();
      Constraint c1 = mock(Constraint.class);
      Constraint c2 = mock(Constraint.class);

      ClpVariable original = new ClpVariable();
      original.addConstraint(c1);
      original.setMin(e, 7);
      original.setMax(e, 15);
      original.setNot(e, 12);
      original = original.getTerm();

      ClpVariable copy = original.copy();
      assertNotSame(copy, original);
      assertSame(copy, original.getTerm());

      copy.addConstraint(c2);

      original.setMin(e, 8);
      original.setMax(e, 14);
      original.setNot(e, 11);

      original.backtrack();

      assertEquals(7, original.getMin(e));
      assertEquals(15, original.getMax(e));
      assertEquals("{7, 8, 9, 10, 11, 13, 14, 15}", original.toString());
      assertEquals(Arrays.asList(c1), original.getConstraints());
      assertEquals(8, copy.getMin(e));
      assertEquals(14, copy.getMax(e));
      assertEquals("{8, 9, 10, 13, 14}", copy.toString());
      assertEquals(Arrays.asList(c1, c2), copy.getTerm().getConstraints());
   }

   @Test
   public void testTermCopy() {
      ClpVariable v = new ClpVariable();
      try {
         v.copy(new HashMap<>());
         fail();
      } catch (ProjogException e) {
         assertEquals("CLP_VARIABLE does not support copy, so is not suitable for use in this scenario", e.getMessage());
      }

      CoreConstraintStore e = new CoreConstraintStore();
      v.setMin(e, 8);
      v.setMax(e, 8);

      assertSame(v.getTerm(), v.copy(new HashMap<>()));
   }

   @Test
   public void testTermCompare() {
      ClpVariable clpVariable1 = new ClpVariable();
      ClpVariable clpVariable2 = new ClpVariable();
      clpVariable2.setMin(new CoreConstraintStore(), 8);
      clpVariable2.setMax(new CoreConstraintStore(), 8);

      Atom a = new Atom("a");
      EmptyList el = EmptyList.EMPTY_LIST;
      Term l = ListFactory.createList(a, el);
      IntegerNumber i7 = new IntegerNumber(7);
      IntegerNumber i10 = new IntegerNumber(10);
      DecimalFraction d7 = new DecimalFraction(7.5);
      DecimalFraction d10 = new DecimalFraction(10.5);
      Term s = Structure.createStructure("test", new Term[] {i7, i10, d7, d10});
      Variable v1 = new Variable();
      Variable v2 = new Variable();
      v2.unify(new IntegerNumber(9));
      List<Term> list = Arrays.asList(a, v1, v2, el, l, i7, i10, d7, d10, s, clpVariable1, clpVariable2);
      Collections.shuffle(list);

      list.sort(TermComparator.TERM_COMPARATOR);

      assertEquals(list, Arrays.asList(v1, clpVariable1, d7, d10, i7, clpVariable2, v2, i10, el, a, l, s));
   }

   @Test
   public void testUnboundDoesNotEqualClpVariable() {
      ClpVariable v = new ClpVariable();

      assertTrue(v.equals(v));
      assertFalse(v.equals(new ClpVariable()));
   }

   @Test
   public void testUnboundDoesNotEqualInteger() {
      IntegerNumber i = new IntegerNumber(7);
      ClpVariable v = new ClpVariable();

      assertFalse(v.equals(i));
      assertFalse(i.equals(v));
   }

   @Test
   public void testUnboundDoesNotEqualIntegerMatchingMin() {
      IntegerNumber i = new IntegerNumber(7);

      ClpVariable v = new ClpVariable();
      assertSame(TermType.CLP_VARIABLE, v.getType());
      v.setMin(new CoreConstraintStore(), i.getLong());
      v.setMax(new CoreConstraintStore(), i.getLong() + 1);
      assertSame(TermType.CLP_VARIABLE, v.getType());
      assertFalse(v.isImmutable());
      assertFalse(v.getTerm().isImmutable());

      assertFalse(v.equals(i));
      assertFalse(i.equals(v));

      assertFalse(v.getTerm().equals(i));
      assertFalse(i.equals(v.getTerm()));
   }

   @Test
   public void testUnboundDoesNotEqualIntegerMatchingMax() {
      IntegerNumber i = new IntegerNumber(7);

      ClpVariable v = new ClpVariable();
      assertSame(TermType.CLP_VARIABLE, v.getType());
      v.setMin(new CoreConstraintStore(), i.getLong() - 1);
      v.setMax(new CoreConstraintStore(), i.getLong());
      assertSame(TermType.CLP_VARIABLE, v.getType());
      assertFalse(v.isImmutable());
      assertFalse(v.getTerm().isImmutable());

      assertFalse(v.equals(i));
      assertFalse(i.equals(v));

      assertFalse(v.getTerm().equals(i));
      assertFalse(i.equals(v.getTerm()));
   }

   @Test
   public void testBoundNotEqualsInteger() {
      IntegerNumber i = new IntegerNumber(7);

      ClpVariable v = new ClpVariable();
      assertSame(TermType.CLP_VARIABLE, v.getType());
      v.setMin(new CoreConstraintStore(), i.getLong() + 1);
      v.setMax(new CoreConstraintStore(), i.getLong() + 1);
      assertSame(TermType.INTEGER, v.getType());
      assertFalse(v.isImmutable());
      assertTrue(v.getTerm().isImmutable());

      assertFalse(v.equals(i));
      assertFalse(i.equals(v));

      assertFalse(v.getTerm().equals(i));
      assertFalse(i.equals(v.getTerm()));
      assertNotEquals(i.hashCode(), v.getTerm().hashCode());
   }

   @Test
   public void testBoundEqualsInteger() {
      IntegerNumber i = new IntegerNumber(7);

      ClpVariable v = new ClpVariable();
      assertSame(TermType.CLP_VARIABLE, v.getType());
      v.setMin(new CoreConstraintStore(), i.getLong());
      v.setMax(new CoreConstraintStore(), i.getLong());
      assertSame(TermType.INTEGER, v.getType());
      assertFalse(v.isImmutable());
      assertTrue(v.getTerm().isImmutable());

      assertFalse(v.equals(i));
      assertFalse(i.equals(v));

      assertTrue(v.getTerm().equals(i));
      assertTrue(i.equals(v.getTerm()));
      assertEquals(i.hashCode(), v.getTerm().hashCode());
   }

   @Test
   public void testBoundDoesNotEqualClpVariableMatchingMin() {
      int value = 42;

      ClpVariable v1 = new ClpVariable();
      v1.setMin(new CoreConstraintStore(), value);
      v1.setMax(new CoreConstraintStore(), value);

      ClpVariable v2 = new ClpVariable();
      v2.setMin(new CoreConstraintStore(), value);
      v2.setMax(new CoreConstraintStore(), value + 1);

      assertFalse(v1.equals(v2));
      assertFalse(v2.equals(v1));

      assertFalse(v1.equals(v2.getTerm()));
      assertFalse(v2.equals(v1.getTerm()));

      assertFalse(v1.getTerm().equals(v2.getTerm()));
      assertFalse(v2.getTerm().equals(v1.getTerm()));
   }

   @Test
   public void testBoundDoesNotEqualClpVariableMatchingMax() {
      int value = 42;

      ClpVariable v1 = new ClpVariable();
      v1.setMin(new CoreConstraintStore(), value);
      v1.setMax(new CoreConstraintStore(), value);

      ClpVariable v2 = new ClpVariable();
      v2.setMin(new CoreConstraintStore(), value - 1);
      v2.setMax(new CoreConstraintStore(), value);

      assertFalse(v1.equals(v2));
      assertFalse(v2.equals(v1));

      assertFalse(v1.equals(v2.getTerm()));
      assertFalse(v2.equals(v1.getTerm()));

      assertFalse(v1.getTerm().equals(v2.getTerm()));
      assertFalse(v2.getTerm().equals(v1.getTerm()));
   }

   @Test
   public void testBoundNotEqualsClpVariable() {
      int value = 42;

      ClpVariable v1 = new ClpVariable();
      v1.setMin(new CoreConstraintStore(), value);
      v1.setMax(new CoreConstraintStore(), value);

      ClpVariable v2 = new ClpVariable();
      v2.setMin(new CoreConstraintStore(), value + 1);
      v2.setMax(new CoreConstraintStore(), value + 1);

      assertFalse(v1.equals(v2));
      assertFalse(v2.equals(v1));

      assertFalse(v1.equals(v2.getTerm()));
      assertFalse(v2.equals(v1.getTerm()));

      assertFalse(v1.getTerm().equals(v2.getTerm()));
      assertFalse(v2.getTerm().equals(v1.getTerm()));
   }

   @Test
   public void testBoundEqualsClpVariable() {
      int value = 42;

      ClpVariable v1 = new ClpVariable();
      v1.setMin(new CoreConstraintStore(), value);
      v1.setMax(new CoreConstraintStore(), value);

      ClpVariable v2 = new ClpVariable();
      v2.setMin(new CoreConstraintStore(), value);
      v2.setMax(new CoreConstraintStore(), value);

      assertFalse(v1.equals(v2));
      assertFalse(v2.equals(v1));

      assertFalse(v1.equals(v2.getTerm()));
      assertFalse(v2.equals(v1.getTerm()));

      assertTrue(v1.getTerm().equals(v2.getTerm()));
      assertTrue(v2.getTerm().equals(v1.getTerm()));
      assertEquals(v1.getTerm().hashCode(), v2.getTerm().hashCode());
   }
}
