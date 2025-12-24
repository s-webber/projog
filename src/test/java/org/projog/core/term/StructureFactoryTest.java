/*
 * Copyright 2025 S. Webber
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
package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class StructureFactoryTest {
   private static final String FUNCTOR = "functorName";
   private static final Atom ATOM_1 = new Atom("first");
   private static final Atom ATOM_2 = new Atom("second");
   private static final Atom ATOM_3 = new Atom("third");
   private static final Atom ATOM_4 = new Atom("fourth");
   private static final Atom ATOM_5 = new Atom("fifth");

   @Test
   public void testSingleArgumentStructure_immutable() {
      Term term = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1});

      assertEquals("org.projog.core.term.StructureFactory$SingleArgumentStructure", term.getClass().getName());

      assertSame(TermType.STRUCTURE, term.getType());

      assertSame(FUNCTOR, term.getName());

      assertEquals(1, term.getNumberOfArguments());

      assertEquals("functorName(first)", term.toString());

      assertTrue(term.isImmutable());

      assertSame(term, term.getBound());

      assertSame(term, term.getTerm());

      assertSame(term, term.copy(null));

      assertSame(ATOM_1, term.firstArgument());
      assertThrows(IllegalArgumentException.class, () -> term.secondArgument());
      assertThrows(IllegalArgumentException.class, () -> term.thirdArgument());
      assertThrows(IllegalArgumentException.class, () -> term.fourthArgument());

      assertSame(ATOM_1, term.getArgument(0));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(1));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(2));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(3));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(4));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(5));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(100));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(-1));

      // confirm backtrack has no affect
      term.backtrack();
      assertSame(ATOM_1, term.firstArgument());

      assertTermEqual(term, term);
      assertTermEqual(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1}));

      assertNotUnifiable(term, StructureFactory.createStructure("x", new Term[] {ATOM_1}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_1}));
   }

   @Test
   public void testSingleArgumentStructure_mutable() {
      Term immutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1});
      assertEquals("org.projog.core.term.StructureFactory$SingleArgumentStructure", immutableStructure.getClass().getName());
      assertTrue(immutableStructure.isImmutable());

      Variable variable = new Variable();
      Term mutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {variable});

      assertEquals("functorName(_)", mutableStructure.toString());
      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      // test unification succeeds
      assertTrue(immutableStructure.unify(mutableStructure));

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertTrue(mutableStructure.getTerm().isImmutable());

      assertSame(ATOM_1, variable.getTerm());
      assertSame(variable, mutableStructure.firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().firstArgument());

      // test backtrack
      mutableStructure.backtrack();

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      assertSame(variable, variable.getTerm());
      assertSame(variable, mutableStructure.firstArgument());
      assertSame(variable, mutableStructure.getTerm().firstArgument());

      // test unification fails when functor is different
      assertFalse(mutableStructure.unify(StructureFactory.createStructure("x", new Term[] {ATOM_1})));

      // test unification succeeds
      assertTrue(mutableStructure.unify(StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2})));
      assertSame(ATOM_2, mutableStructure.getTerm().firstArgument());
      assertEquals("functorName(second)", mutableStructure.toString());

      // test unification fails
      assertFalse(immutableStructure.unify(mutableStructure));
      assertFalse(mutableStructure.unify(immutableStructure));
   }

   @Test
   public void testTwoArgumentStructure_immutable() {
      Term term = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2});

      assertEquals("org.projog.core.term.StructureFactory$TwoArgumentStructure", term.getClass().getName());

      assertSame(TermType.STRUCTURE, term.getType());

      assertSame(FUNCTOR, term.getName());

      assertEquals(2, term.getNumberOfArguments());

      assertEquals("functorName(first, second)", term.toString());

      assertTrue(term.isImmutable());

      assertSame(term, term.getBound());

      assertSame(term, term.getTerm());

      assertSame(term, term.copy(null));

      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
      assertThrows(IllegalArgumentException.class, () -> term.thirdArgument());
      assertThrows(IllegalArgumentException.class, () -> term.fourthArgument());

      assertSame(ATOM_1, term.getArgument(0));
      assertSame(ATOM_2, term.getArgument(1));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(2));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(3));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(4));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(5));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(100));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(-1));

      // confirm backtrack has no affect
      term.backtrack();
      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());

      assertTermEqual(term, term);
      assertTermEqual(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2}));

      assertNotUnifiable(term, StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3}));
   }

   @Test
   public void testTwoArgumentStructure_mutable() {
      Term immutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2});
      assertEquals("org.projog.core.term.StructureFactory$TwoArgumentStructure", immutableStructure.getClass().getName());
      assertTrue(immutableStructure.isImmutable());

      Variable variable1 = new Variable();
      Variable variable2 = new Variable();
      Term mutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {variable1, variable2});

      assertEquals("functorName(_, _)", mutableStructure.toString());
      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      // test unification succeeds
      assertTrue(immutableStructure.unify(mutableStructure));

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertTrue(mutableStructure.getTerm().isImmutable());

      assertSame(ATOM_1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(ATOM_2, mutableStructure.getTerm().secondArgument());

      // test backtrack
      mutableStructure.backtrack();

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      assertSame(variable1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(variable1, mutableStructure.getTerm().firstArgument());
      assertSame(variable2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(variable2, mutableStructure.getTerm().secondArgument());

      // test unification fails when functor is different
      assertFalse(mutableStructure.unify(StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2})));

      // test unification succeeds
      assertTrue(mutableStructure.unify(StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1})));
      assertSame(ATOM_2, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().secondArgument());
      assertEquals("functorName(second, first)", mutableStructure.toString());

      // test unification fails
      assertFalse(immutableStructure.unify(mutableStructure));
      assertFalse(mutableStructure.unify(immutableStructure));
   }

   @Test
   public void testThreeArgumentStructure_immutable() {
      Term term = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3});

      assertEquals("org.projog.core.term.StructureFactory$ThreeArgumentStructure", term.getClass().getName());

      assertSame(TermType.STRUCTURE, term.getType());

      assertSame(FUNCTOR, term.getName());

      assertEquals(3, term.getNumberOfArguments());

      assertEquals("functorName(first, second, third)", term.toString());

      assertTrue(term.isImmutable());

      assertSame(term, term.getBound());

      assertSame(term, term.getTerm());

      assertSame(term, term.copy(null));

      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
      assertSame(ATOM_3, term.thirdArgument());
      assertThrows(IllegalArgumentException.class, () -> term.fourthArgument());

      assertSame(ATOM_1, term.getArgument(0));
      assertSame(ATOM_2, term.getArgument(1));
      assertSame(ATOM_3, term.getArgument(2));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(3));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(4));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(5));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(100));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(-1));

      // confirm backtrack has no affect
      term.backtrack();
      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
      assertSame(ATOM_3, term.thirdArgument());

      assertTermEqual(term, term);
      assertTermEqual(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3}));

      assertNotUnifiable(term, StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2, ATOM_3}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_3, ATOM_2}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1, ATOM_3}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_3, ATOM_1}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_3, ATOM_1, ATOM_2}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_3, ATOM_2, ATOM_1}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4}));
   }

   @Test
   public void testThreeArgumentStructure_mutable() {
      Term immutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3});
      assertEquals("org.projog.core.term.StructureFactory$ThreeArgumentStructure", immutableStructure.getClass().getName());
      assertTrue(immutableStructure.isImmutable());

      Variable variable1 = new Variable();
      Variable variable2 = new Variable();
      Variable variable3 = new Variable();
      Term mutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {variable1, variable2, variable3});

      assertEquals("functorName(_, _, _)", mutableStructure.toString());
      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      // test unification succeeds
      assertTrue(immutableStructure.unify(mutableStructure));

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertTrue(mutableStructure.getTerm().isImmutable());

      assertSame(ATOM_1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(ATOM_2, mutableStructure.getTerm().secondArgument());
      assertSame(ATOM_3, variable3.getTerm());
      assertSame(variable3, mutableStructure.thirdArgument());
      assertSame(ATOM_3, mutableStructure.getTerm().thirdArgument());

      // test backtrack
      mutableStructure.backtrack();

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      assertSame(variable1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(variable1, mutableStructure.getTerm().firstArgument());
      assertSame(variable2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(variable2, mutableStructure.getTerm().secondArgument());
      assertSame(variable3, variable3.getTerm());
      assertSame(variable3, mutableStructure.thirdArgument());
      assertSame(variable3, mutableStructure.getTerm().thirdArgument());

      // test unification fails when functor is different
      assertFalse(mutableStructure.unify(StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2, ATOM_3})));

      // test unification succeeds
      assertTrue(mutableStructure.unify(StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1, ATOM_3})));
      assertSame(ATOM_2, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().secondArgument());
      assertSame(ATOM_3, mutableStructure.getTerm().thirdArgument());
      assertEquals("functorName(second, first, third)", mutableStructure.toString());

      // test unification fails
      assertFalse(immutableStructure.unify(mutableStructure));
      assertFalse(mutableStructure.unify(immutableStructure));
   }

   @Test
   public void testFourFourArgumentStructure_immutable() {
      Term term = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4});

      assertEquals("org.projog.core.term.StructureFactory$FourArgumentStructure", term.getClass().getName());

      assertSame(TermType.STRUCTURE, term.getType());

      assertSame(FUNCTOR, term.getName());

      assertEquals(4, term.getNumberOfArguments());

      assertEquals("functorName(first, second, third, fourth)", term.toString());

      assertTrue(term.isImmutable());

      assertSame(term, term.getBound());

      assertSame(term, term.getTerm());

      assertSame(term, term.copy(null));

      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
      assertSame(ATOM_3, term.thirdArgument());
      assertSame(ATOM_4, term.fourthArgument());

      assertSame(ATOM_1, term.getArgument(0));
      assertSame(ATOM_2, term.getArgument(1));
      assertSame(ATOM_3, term.getArgument(2));
      assertSame(ATOM_4, term.getArgument(3));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(4));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(5));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(100));
      assertThrows(IllegalArgumentException.class, () -> term.getArgument(-1));

      // confirm backtrack has no affect
      term.backtrack();
      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
      assertSame(ATOM_3, term.thirdArgument());
      assertSame(ATOM_4, term.fourthArgument());

      assertTermEqual(term, term);
      assertTermEqual(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4}));

      assertNotUnifiable(term, StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_4, ATOM_3}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1, ATOM_3, ATOM_4}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_5, ATOM_2, ATOM_3, ATOM_4}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_5, ATOM_3, ATOM_4}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_5}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5}));
   }

   @Test
   public void testFourArgumentStructure_mutable() {
      Term immutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4});
      assertEquals("org.projog.core.term.StructureFactory$FourArgumentStructure", immutableStructure.getClass().getName());
      assertTrue(immutableStructure.isImmutable());

      Variable variable1 = new Variable();
      Variable variable2 = new Variable();
      Variable variable3 = new Variable();
      Variable variable4 = new Variable();
      Term mutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {variable1, variable2, variable3, variable4});

      assertEquals("functorName(_, _, _, _)", mutableStructure.toString());
      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      // test unification succeeds
      assertTrue(immutableStructure.unify(mutableStructure));

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertTrue(mutableStructure.getTerm().isImmutable());

      assertSame(ATOM_1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(ATOM_2, mutableStructure.getTerm().secondArgument());
      assertSame(ATOM_3, variable3.getTerm());
      assertSame(variable3, mutableStructure.thirdArgument());
      assertSame(ATOM_3, mutableStructure.getTerm().thirdArgument());
      assertSame(ATOM_4, variable4.getTerm());
      assertSame(variable4, mutableStructure.fourthArgument());
      assertSame(ATOM_4, mutableStructure.getTerm().fourthArgument());

      // test backtrack
      mutableStructure.backtrack();

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      assertSame(variable1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(variable1, mutableStructure.getTerm().firstArgument());
      assertSame(variable2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(variable2, mutableStructure.getTerm().secondArgument());
      assertSame(variable3, variable3.getTerm());
      assertSame(variable3, mutableStructure.thirdArgument());
      assertSame(variable3, mutableStructure.getTerm().thirdArgument());
      assertSame(variable4, variable4.getTerm());
      assertSame(variable4, mutableStructure.fourthArgument());
      assertSame(variable4, mutableStructure.getTerm().fourthArgument());

      // test unification fails when functor is different
      assertFalse(mutableStructure.unify(StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4})));

      // test unification succeeds
      assertTrue(mutableStructure.unify(StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1, ATOM_3, ATOM_4})));
      assertSame(ATOM_2, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().secondArgument());
      assertSame(ATOM_3, mutableStructure.getTerm().thirdArgument());
      assertSame(ATOM_4, mutableStructure.getTerm().fourthArgument());
      assertEquals("functorName(second, first, third, fourth)", mutableStructure.toString());

      // test unification fails
      assertFalse(immutableStructure.unify(mutableStructure));
      assertFalse(mutableStructure.unify(immutableStructure));
   }

   @Test
   public void testMultipleArgumentStructure_immutable() {
      Term term = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5});

      assertEquals("org.projog.core.term.StructureFactory$MultipleArgumentStructure", term.getClass().getName());

      assertSame(TermType.STRUCTURE, term.getType());

      assertSame(FUNCTOR, term.getName());

      assertEquals(5, term.getNumberOfArguments());

      assertEquals("functorName(first, second, third, fourth, fifth)", term.toString());

      assertTrue(term.isImmutable());

      assertSame(term, term.getBound());

      assertSame(term, term.getTerm());

      assertSame(term, term.copy(null));

      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
      assertSame(ATOM_3, term.thirdArgument());
      assertSame(ATOM_4, term.fourthArgument());

      assertSame(ATOM_1, term.getArgument(0));
      assertSame(ATOM_2, term.getArgument(1));
      assertSame(ATOM_3, term.getArgument(2));
      assertSame(ATOM_4, term.getArgument(3));
      assertSame(ATOM_5, term.getArgument(4));
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> term.getArgument(5));
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> term.getArgument(100));
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> term.getArgument(-1));

      // confirm backtrack has no affect
      term.backtrack();
      assertSame(ATOM_1, term.getArgument(0));
      assertSame(ATOM_2, term.getArgument(1));
      assertSame(ATOM_3, term.getArgument(2));
      assertSame(ATOM_4, term.getArgument(3));
      assertSame(ATOM_5, term.getArgument(4));

      assertTermEqual(term, term);
      assertTermEqual(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5}));

      assertNotUnifiable(term, StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_2, ATOM_3, ATOM_4, ATOM_5}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_1, ATOM_3, ATOM_4, ATOM_5}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_1, ATOM_4, ATOM_5}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_1, ATOM_5}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_1}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_4, ATOM_3, ATOM_5, ATOM_1, ATOM_2}));
      assertNotUnifiable(term, StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5, ATOM_1}));
   }

   @Test
   public void testMultipleArgumentStructure_mutable() {
      Term immutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5});
      assertEquals("org.projog.core.term.StructureFactory$MultipleArgumentStructure", immutableStructure.getClass().getName());
      assertTrue(immutableStructure.isImmutable());

      Variable variable1 = new Variable();
      Variable variable2 = new Variable();
      Variable variable3 = new Variable();
      Variable variable4 = new Variable();
      Variable variable5 = new Variable();
      Term mutableStructure = StructureFactory.createStructure(FUNCTOR, new Term[] {variable1, variable2, variable3, variable4, variable5});

      assertEquals("functorName(_, _, _, _, _)", mutableStructure.toString());
      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      // test unification succeeds
      assertTrue(immutableStructure.unify(mutableStructure));

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertTrue(mutableStructure.getTerm().isImmutable());

      assertSame(ATOM_1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(ATOM_2, mutableStructure.getTerm().secondArgument());
      assertSame(ATOM_3, variable3.getTerm());
      assertSame(variable3, mutableStructure.thirdArgument());
      assertSame(ATOM_3, mutableStructure.getTerm().thirdArgument());
      assertSame(ATOM_4, variable4.getTerm());
      assertSame(variable4, mutableStructure.fourthArgument());
      assertSame(ATOM_4, mutableStructure.getTerm().fourthArgument());
      assertSame(ATOM_5, variable5.getTerm());
      assertSame(variable5, mutableStructure.getArgument(4));
      assertSame(ATOM_5, mutableStructure.getTerm().getArgument(4));

      // test backtrack
      mutableStructure.backtrack();

      assertSame(mutableStructure, mutableStructure.getBound());
      assertFalse(mutableStructure.isImmutable());
      assertFalse(mutableStructure.getTerm().isImmutable());

      assertSame(variable1, variable1.getTerm());
      assertSame(variable1, mutableStructure.firstArgument());
      assertSame(variable1, mutableStructure.getTerm().firstArgument());
      assertSame(variable2, variable2.getTerm());
      assertSame(variable2, mutableStructure.secondArgument());
      assertSame(variable2, mutableStructure.getTerm().secondArgument());
      assertSame(variable3, variable3.getTerm());
      assertSame(variable3, mutableStructure.thirdArgument());
      assertSame(variable3, mutableStructure.getTerm().thirdArgument());
      assertSame(variable4, variable4.getTerm());
      assertSame(variable4, mutableStructure.fourthArgument());
      assertSame(variable4, mutableStructure.getTerm().fourthArgument());
      assertSame(variable5, variable5.getTerm());
      assertSame(variable5, mutableStructure.getArgument(4));
      assertSame(variable5, mutableStructure.getTerm().getArgument(4));

      // test unification fails when functor is different
      assertFalse(mutableStructure.unify(StructureFactory.createStructure("x", new Term[] {ATOM_1, ATOM_2, ATOM_3, ATOM_4, ATOM_5})));

      // test unification succeeds
      assertTrue(mutableStructure.unify(StructureFactory.createStructure(FUNCTOR, new Term[] {ATOM_2, ATOM_1, ATOM_3, ATOM_4, ATOM_5})));
      assertSame(ATOM_2, mutableStructure.getTerm().firstArgument());
      assertSame(ATOM_1, mutableStructure.getTerm().secondArgument());
      assertSame(ATOM_3, mutableStructure.getTerm().thirdArgument());
      assertSame(ATOM_4, mutableStructure.getTerm().fourthArgument());
      assertEquals("functorName(second, first, third, fourth, fifth)", mutableStructure.toString());

      // test unification fails
      assertFalse(immutableStructure.unify(mutableStructure));
      assertFalse(mutableStructure.unify(immutableStructure));
   }

   /** Confirm that createStructure(String, Term[]) returns Atom when array length is 0. */
   @Test
   public void testCreateStructure_emptyArray() {
      Term term = StructureFactory.createStructure(FUNCTOR, new Term[0]);

      assertEquals(Atom.class, term.getClass());
      assertSame(FUNCTOR, term.getName());
   }

   /** Confirm that createStructure(String, Term) returns SingleArgumentStructure */
   @Test
   public void testCreateStructure_singleTerm() {
      Term term = StructureFactory.createStructure(FUNCTOR, ATOM_1);

      assertEquals("org.projog.core.term.StructureFactory$SingleArgumentStructure", term.getClass().getName());
      assertSame(ATOM_1, term.firstArgument());
   }

   /** Confirm that createStructure(String, Term, Term) returns TwoArgumentStructure if functor is not ".". */
   @Test
   public void testCreateStructure_twoTerms() {
      Term term = StructureFactory.createStructure(FUNCTOR, ATOM_1, ATOM_2);

      assertEquals("org.projog.core.term.StructureFactory$TwoArgumentStructure", term.getClass().getName());
      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
   }

   /** Confirm that createStructure(String, Term, Term) returns List if functor is ".". */
   @Test
   public void testCreateList_twoTerms() {
      Term term = StructureFactory.createStructure(".", ATOM_1, ATOM_2);

      assertEquals(List.class, term.getClass());
      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
   }

   /** Confirm that createStructure(String, Term[]) returns List if functor is "." and array length is 2. */
   @Test
   public void testCreateList_termArray() {
      Term term = StructureFactory.createStructure(".", new Term[] {ATOM_1, ATOM_2});

      assertEquals(List.class, term.getClass());
      assertSame(ATOM_1, term.firstArgument());
      assertSame(ATOM_2, term.secondArgument());
   }

   /** Confirm it is possible to create a structure with a large number of arguments. */
   @Test
   public void testVeryLargeArgumentStructure() {
      Term[] args = new Term[10000];
      for (int i = 0; i < args.length; i++) {
         args[i] = new Atom("atom" + i);
      }

      Term term = StructureFactory.createStructure(FUNCTOR, args);

      assertEquals("org.projog.core.term.StructureFactory$MultipleArgumentStructure", term.getClass().getName());

      assertEquals(args.length, term.getNumberOfArguments());

      for (int i = 0; i < args.length; i++) {
         assertSame(args[i], term.getArgument(i));
      }

      assertThrows(ArrayIndexOutOfBoundsException.class, () -> term.getArgument(args.length));
   }

   @Test
   public void testStructuresOfDifferentLenghtsContainingOneVariable() {
      // test range of structure argument lengths
      // from structures with a single argument to structure's with many arguments
      for (int numArgs = 1; numArgs < 8; numArgs++) {
         // test with variable in different index
         // e.g. structure with variable as first argument, structure with variable as second argument, etc.
         for (int indexOfVariable = 0; indexOfVariable < numArgs; indexOfVariable++) {
            // create variable that will be the only variable in the arguments of the structure that will be tested
            Variable variable = new Variable();

            Term[] args = new Term[numArgs];
            for (int i = 0; i < numArgs; i++) {
               args[i] = i == indexOfVariable ? variable : new Atom("a" + i);
            }

            // create the structure to be tested
            Term mutableStructure = StructureFactory.createStructure(FUNCTOR, args);

            // test behaviour when variable is uninstantiated
            assertSame(mutableStructure, mutableStructure.getTerm());
            assertSame(mutableStructure, mutableStructure.getBound());
            assertFalse(mutableStructure.isImmutable());
            assertSame(variable, mutableStructure.getArgument(indexOfVariable));

            // unify the variable that is an argument of the structure
            variable.unify(ATOM_1);

            // test behaviour of mutableStructure when variable is instantiated
            assertSame(mutableStructure, mutableStructure.getBound());
            assertFalse(mutableStructure.isImmutable());
            assertSame(variable, mutableStructure.getArgument(indexOfVariable));

            // test behaviour of mutableStructure.getTerm() when variable is instantiated
            assertNotSame(mutableStructure, mutableStructure.getTerm());
            assertTrue(mutableStructure.getTerm().isImmutable());
            assertSame(ATOM_1, mutableStructure.getTerm().getArgument(indexOfVariable));

            // backtrack the variable that is an argument of the structure (so is no longer instantiated)
            variable.backtrack();

            // confirm that, now the variable is uninstantiated again, mutableStructure.getTerm() returns itself
            assertSame(mutableStructure, mutableStructure.getTerm());

            // test copy behaviour with uninstantiated variable as argument of structure
            Map<Variable, Term> sharedVariables = new HashMap<>();
            Term copy = mutableStructure.copy(sharedVariables);
            assertNotSame(mutableStructure, copy);
            assertSame(mutableStructure.getClass(), copy.getClass());
            assertEquals(1, sharedVariables.size());
            assertTrue(sharedVariables.containsKey(variable));
            assertNotEquals(variable, sharedVariables.get(variable));
            assertSame(sharedVariables.get(variable), copy.getArgument(indexOfVariable));

            // confirm that original and copy can be instantiated to different terms (really are separate copies)
            variable.unify(ATOM_2);
            copy.getArgument(indexOfVariable).unify(ATOM_3);
            assertSame(ATOM_2, mutableStructure.getArgument(indexOfVariable).getTerm());
            assertSame(ATOM_3, copy.getArgument(indexOfVariable).getTerm());
         }
      }
   }

   private static void assertTermEqual(Term a, Term b) {
      assertTrue(a.equals(b));
      assertTrue(b.equals(a));

      assertEquals(a.hashCode(), b.hashCode());

      assertTrue(a.unify(b));
      assertTrue(b.unify(a));
   }

   private static void assertNotUnifiable(Term a, Term b) {
      assertFalse(a.equals(b));
      assertFalse(b.equals(a));

      assertNotEquals(a.hashCode(), b.hashCode());

      assertFalse(a.unify(b));
      assertFalse(b.unify(a));
   }
}
