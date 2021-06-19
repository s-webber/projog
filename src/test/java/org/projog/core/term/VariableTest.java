/*
 * Copyright 2013 S. Webber
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.assertStrictEquality;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.projog.core.ProjogException;

/**
 * @see TermTest
 */
public class VariableTest {
   @Test
   public void testUnassignedVariableMethods() {
      Variable v = new Variable("X");

      assertEquals(v, v);
      assertEquals("X", v.getId());
      assertEquals("X", v.toString());
      assertSame(v, v.getTerm());
      assertSame(v, v.getBound());
      assertTrue(TermUtils.termsEqual(v, v));

      try {
         v.getName();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.getArgs();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.getNumberOfArguments();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.getArgument(0);
         fail();
      } catch (NullPointerException e) {
      }
      try {
         TermUtils.castToNumeric(v);
         fail();
      } catch (ProjogException e) {
         assertEquals("Expected Numeric but got: VARIABLE with value: X", e.getMessage());
      }

      assertTrue(v.unify(v));

      // just check backtrack doesn't throw an exception
      v.backtrack();
   }

   @Test
   public void testUnifyVariables_1() {
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      assertStrictEquality(x, y, false);
      assertTrue(x.unify(y));
      assertStrictEquality(x, y, true);
      x.backtrack();
      assertStrictEquality(x, y, false);
   }

   @Test
   public void testUnifyVariables_2() {
      Atom a = atom();
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      assertTrue(y.unify(a));
      assertTrue(x.unify(y));
      assertSame(a, x.getTerm());
      assertSame(a, x.getBound());
      x.backtrack();
      assertSame(x, x.getTerm());
      assertSame(a, y.getTerm());
      assertSame(x, x.getBound());
      assertSame(a, y.getBound());
   }

   @Test
   public void testUnifyVariables_3() {
      Atom a = atom();
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      assertTrue(x.unify(y));
      assertTrue(y.unify(a));
      assertSame(a, x.getTerm());
      assertSame(a, x.getBound());
   }

   @Test
   public void testVariablesUnifiedToTheSameTerm() {
      Atom a = atom();
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      assertStrictEquality(x, y, false);
      assertTrue(x.unify(a));
      assertTrue(y.unify(a));
      assertStrictEquality(x, y, true);
      x.backtrack();
      assertStrictEquality(x, y, false);
      assertSame(x, x.getTerm());
      assertSame(a, y.getTerm());
      assertSame(x, x.getBound());
      assertSame(a, y.getBound());
   }

   @Test
   public void testVariableUnifiedToMutableTerm() {
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Structure p = structure("p1", structure("p2", x, atom(), x), y);

      Variable result = new Variable("Result");
      assertSame(result, result.getTerm());
      assertSame(result, result.getBound());

      assertTrue(result.unify(p));
      assertSame(p, result.getTerm());
      assertSame(p, result.getBound());

      assertTrue(x.unify(a));
      assertNotSame(p, result.getTerm());
      assertEquals(structure("p1", structure("p2", a, atom(), a), y), result.getTerm());
      assertSame(p, result.getBound());

      assertTrue(y.unify(b));
      assertNotSame(p, result.getTerm());
      assertEquals(structure("p1", structure("p2", a, atom(), a), b), result.getTerm());
      assertSame(p, result.getBound());

      x.backtrack();
      y.backtrack();
      assertSame(p, result.getTerm());
      assertSame(p, result.getBound());

      result.backtrack();
      assertSame(result, result.getTerm());
      assertSame(result, result.getBound());
   }

   @Test
   public void testCopy() {
      Variable v = variable();
      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Term copy = v.copy(sharedVariables);
      assertEquals(1, sharedVariables.size());
      assertSame(copy, sharedVariables.get(v));
      assertFalse(TermUtils.termsEqual(v, copy));
      assertTrue(v.unify(copy));
      assertTrue(TermUtils.termsEqual(v, copy));
   }

   /**
    * Tests that, when {@link Variable#copy(Map)} is called on a variable whose "copy" (contained in the specified Map)
    * is already instantiated, the term the "copy" is instantiated with gets returned rather than the "copy" itself.
    * <p>
    * This behaviour is required for things like
    * {@link org.projog.core.udp.interpreter.InterpretedTailRecursivePredicate} to work.
    */
   @Test
   public void testCopy_2() {
      Variable v = variable();
      Atom a = atom();
      Structure s1 = structure("name", v);
      Structure s2 = structure("name", v);

      Map<Variable, Variable> sharedVariables = new HashMap<>();

      Structure c1 = s1.copy(sharedVariables);
      assertTrue(c1.unify(structure("name", a)));

      Structure c2 = s2.copy(sharedVariables);
      // check that the single argument of the newly copied structure is the atom itself
      // rather than a variable assigned to the atom
      assertSame(a, c2.getArgument(0));
      // check that, while backtracking does affect the first copied structure,
      // it does not alter the second copied structure
      c1.backtrack();
      c2.backtrack();
      assertSame(Variable.class, c1.getArgument(0).getClass());
      assertSame(a, c2.getArgument(0));
   }

   @Test
   public void testIsImmutable() {
      Variable v = new Variable("X");
      assertFalse(v.isImmutable());
      Atom a = atom();
      assertTrue(v.unify(a));
      assertFalse(v.isImmutable());
   }

   @Test
   public void testUnifyAnonymousVariable() {
      Variable v = variable();
      Variable anon = new Variable();
      assertTrue(v.unify(anon));
      assertSame(anon, v.getTerm());
      assertSame(anon, v.getBound());
   }

   @Test
   public void testUnifyWithSelf() {
      Variable x = new Variable("X");
      x.unify(x);
      assertSame(x, x.getTerm());
   }

   @Test
   public void testUnifyPair() {
      Variable v = new Variable("V");
      Variable w = new Variable("W");
      v.unify(w);
      w.unify(v);
      assertSame(w, v.getTerm());
      assertSame(w, v.getTerm());
   }

   @Test
   public void testUnifyCyclicChain() {
      Variable v = new Variable("V");
      Variable w = new Variable("W");
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      Variable z = new Variable("Z");

      v.unify(w);
      w.unify(x);
      x.unify(y);
      y.unify(z);
      z.unify(x);

      assertSame(z, v.getTerm());
      assertSame(z, w.getTerm());
      assertSame(z, x.getTerm());
      assertSame(z, y.getTerm());
      assertSame(z, z.getTerm());

      v.unify(v);
      v.unify(x);
      v.unify(w);
      v.unify(y);
      v.unify(z);
      w.unify(v);
      w.unify(w);
      w.unify(x);
      w.unify(y);
      w.unify(z);
      x.unify(v);
      x.unify(w);
      x.unify(x);
      x.unify(y);
      x.unify(z);
      y.unify(v);
      y.unify(w);
      y.unify(x);
      y.unify(y);
      y.unify(z);
      z.unify(v);
      z.unify(w);
      z.unify(x);
      z.unify(y);
      z.unify(z);

      assertSame(z, v.getTerm());
      assertSame(z, w.getTerm());
      assertSame(z, x.getTerm());
      assertSame(z, y.getTerm());
      assertSame(z, z.getTerm());

      Variable u = new Variable("U");
      u.unify(x);
      assertSame(z, u.getTerm());

      Variable t = new Variable("T");
      x.unify(t);
      assertSame(t, t.getTerm());
      assertSame(t, u.getTerm());
      assertSame(t, v.getTerm());
      assertSame(t, w.getTerm());
      assertSame(t, x.getTerm());
      assertSame(t, y.getTerm());
      assertSame(t, z.getTerm());
   }

   @Test
   public void testVariableChain() {
      final Variable v1 = variable();
      Variable v2 = v1;
      for (int i = 0; i < 10000; i++) {
         Variable tmpVar = variable("V" + i);
         v2.unify(tmpVar);
         v2 = tmpVar;
      }
      assertNotEquals(v1, v2);
      Structure t = structure("name", atom("a"), atom("b"), atom("c"));
      assertTrue(v2.unify(t));
      assertNotEquals(v1, v2);
      assertStrictEquality(v1, v2, true);
      assertStrictEquality(v1, t, true);
      assertStrictEquality(v2, t, true);

      assertSame(t, v1.getTerm());
      assertSame(t, v1.getBound());
      assertSame(t, v1.copy(null));
      assertEquals(t.toString(), v1.toString());
      assertSame(t.getName(), v1.getName());
      assertSame(t.getType(), v1.getType());
      assertSame(t.getNumberOfArguments(), v1.getNumberOfArguments());
      assertSame(t.getArgs(), v1.getArgs());
      assertSame(t.getArgument(0), v1.getArgument(0));
      assertTrue(TermUtils.termsEqual(t, v1));
      assertTrue(TermUtils.termsEqual(v1, v1));
      assertTrue(t.unify(v1));
      assertTrue(v1.unify(t));
      assertFalse(v1.unify(atom()));
      assertFalse(atom().unify(v1));

      v2.backtrack();
      assertSame(v2, v1.getTerm());
      assertSame(v2, v1.getBound());

      v1.backtrack();
      assertSame(v1, v1.getTerm());
      assertSame(v1, v1.getBound());
   }

   @Test
   public void testInfiniteTerm() {
      Variable v = variable("X");
      Structure t = structure("name", v);
      assertTrue(v.unify(t));

      assertSame(t, v.getBound());
      assertSame(t, t.getBound());

      try {
         v.copy(new HashMap<>());
         fail();
      } catch (StackOverflowError e) {
      }
      try {
         t.copy(new HashMap<>());
         fail();
      } catch (StackOverflowError e) {
      }
      try {
         v.getTerm();
         fail();
      } catch (StackOverflowError e) {
      }
      try {
         t.getTerm();
         fail();
      } catch (StackOverflowError e) {
      }
      try {
         v.toString();
         fail();
      } catch (StackOverflowError e) {
      }
      try {
         t.toString();
         fail();
      } catch (StackOverflowError e) {
      }
   }

   @Test
   public void testAnonymous() {
      assertTrue(new Variable("_").isAnonymous());
      assertTrue(new Variable().isAnonymous());
   }

   @Test
   public void testNotAnonymous() {
      assertFalse(new Variable("__").isAnonymous());
      assertFalse(new Variable("_1").isAnonymous());
      assertFalse(new Variable("_X").isAnonymous());
      assertFalse(new Variable("X").isAnonymous());
      assertFalse(new Variable("XYZ").isAnonymous());
      assertFalse(new Variable("X_").isAnonymous());
      assertFalse(new Variable("X_Y").isAnonymous());
      assertFalse(new Variable("_X_Y_").isAnonymous());
   }

   @Test
   public void testAnonymousId() {
      assertEquals("_", Variable.ANONYMOUS_VARIABLE_ID);
      assertEquals("_", new Variable().getId());
   }

   @Test
   public void testVariablesEqualToSelf() {
      Variable v = new Variable("X");
      int originalHashCode = v.hashCode();

      // an uninstantiated variable is equal to itself
      assertEquals(v, v);
      assertStrictEquality(v, v, true);

      // instantiate variable
      v.unify(new Atom("test"));

      // an instantiated variable is equal to itself and has its original hashcode
      assertEquals(v, v);
      assertStrictEquality(v, v, true);
      assertEquals(originalHashCode, v.hashCode());

      v.backtrack();

      // after backtracking an uninstantiated variable is still equal to itself and has its original hashcode
      assertEquals(v, v);
      assertStrictEquality(v, v, true);
      assertEquals(originalHashCode, v.hashCode());
   }

   @Test
   public void testVariablesNotEqualWhenUnassigned() {
      Variable v1 = new Variable("X");

      Variable v2 = new Variable("X");
      assertNotEquals(v1, v2);
      assertNotEquals(v1.hashCode(), v2.hashCode());
      assertStrictEquality(v1, v2, false);

      Variable v3 = new Variable("Y");
      assertNotEquals(v1, v3);
      assertNotEquals(v1.hashCode(), v3.hashCode());
      assertStrictEquality(v1, v3, false);
   }

   @Test
   public void testVariablesNotEqualWhenUnifiedWithEachOther() {
      Variable v1 = new Variable("X");
      Variable v2 = new Variable("Y");
      v1.unify(v2);

      assertNotEquals(v1, v2);
      assertNotEquals(v1.hashCode(), v2.hashCode());
      assertStrictEquality(v1, v2, true);
   }

   @Test
   public void testVariablesNotEqualWhenUnifiedWithEachOtherAndSomethingElse() {
      Variable v1 = new Variable("X");
      Variable v2 = new Variable("Y");
      Atom a = new Atom("test");
      v1.unify(v2);
      v2.unify(a);

      assertNotEquals(v1, v2);
      assertNotEquals(v1.hashCode(), v2.hashCode());
      assertStrictEquality(v1, v2, true);
      assertStrictEquality(v1, a, true);
      assertStrictEquality(v2, a, true);
   }

   @Test
   public void testVariablesNotEqualWhenBothUnifiedToSameTerm() {
      Variable v1 = new Variable("X");
      Variable v2 = new Variable("Y");
      Atom a = new Atom("test");
      v1.unify(a);
      v2.unify(a);

      assertNotEquals(v1, v2);
      assertNotEquals(v1.hashCode(), v2.hashCode());
      assertStrictEquality(v1, v2, true);
      assertStrictEquality(v1, a, true);
      assertStrictEquality(v2, a, true);
   }

   @Test
   public void testVariableNotEqualToUnifiedAtom() {
      assertVariableNotEqualToUnifiedTerm(new Atom("test"));
   }

   @Test
   public void testVariableNotEqualToUnifiedInteger() {
      assertVariableNotEqualToUnifiedTerm(new IntegerNumber(7));
   }

   @Test
   public void testVariableNotEqualToUnifiedFraction() {
      assertVariableNotEqualToUnifiedTerm(new DecimalFraction(7));
   }

   @Test
   public void testVariableNotEqualToUnifiedStructure() {
      assertVariableNotEqualToUnifiedTerm(Structure.createStructure("test", new Term[] {atom()}));
   }

   @Test
   public void testVariableNotEqualToUnifiedList() {
      assertVariableNotEqualToUnifiedTerm(new List(atom(), atom()));
   }

   @Test
   public void testVariableNotEqualToUnifiedEmptyList() {
      assertVariableNotEqualToUnifiedTerm(EmptyList.EMPTY_LIST);
   }

   private void assertVariableNotEqualToUnifiedTerm(Term t) {
      Variable v = new Variable(t.getName());
      assertTrue(v.unify(t));

      assertStrictEquality(v, t, true);
      assertFalse(v.equals(t));
      assertFalse(t.equals(v));
      assertNotEquals(v.hashCode(), t.hashCode());
   }
}
