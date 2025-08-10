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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;
import static org.projog.TestUtils.assertStrictEquality;

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
         v.getNumberOfArguments();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.firstArgument();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.secondArgument();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.thirdArgument();
         fail();
      } catch (NullPointerException e) {
      }
      try {
         v.fourthArgument();
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
      Term p = structure("p1", structure("p2", x, atom(), x), y);

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
      Term s1 = structure("name", v);
      Term s2 = structure("name", v);

      Map<Variable, Variable> sharedVariables = new HashMap<>();

      Term c1 = s1.copy(sharedVariables);
      assertTrue(c1.unify(structure("name", a)));

      Term c2 = s2.copy(sharedVariables);
      // check that the single argument of the newly copied structure is the atom itself
      // rather than a variable assigned to the atom
      assertSame(a, c2.firstArgument());
      // check that, while backtracking does affect the first copied structure,
      // it does not alter the second copied structure
      c1.backtrack();
      c2.backtrack();
      assertSame(Variable.class, c1.firstArgument().getClass());
      assertSame(a, c2.firstArgument());
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
      Term t = structure("name", atom("a"), atom("b"), atom("c"));
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
      assertSame(t.firstArgument(), v1.firstArgument());
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
      Term t = structure("name", v);
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
   public void testAttributes() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Variable v = new Variable("X");
      Term defaultValue = new Atom("default");
      Term term1 = new Atom("term1");
      Term term2 = new Atom("term2");

      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // add attribute 1
      v.putAttribute(attribute1, term1);

      assertSame(term1, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // replace attribute 1
      ((Variable) v.getTerm()).putAttribute(attribute1, term2);

      assertSame(term2, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // add attribute 2
      ((Variable) v.getTerm()).putAttribute(attribute2, term1);

      assertSame(term2, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term1, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // replace attribute 2
      ((Variable) v.getTerm()).putAttribute(attribute2, term2);

      assertSame(term2, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term2, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // remove attribute 1
      ((Variable) v.getTerm()).removeAttribute(attribute1);

      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term2, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // try to remove attribute 3
      ((Variable) v.getTerm()).removeAttribute(attribute3);

      // remove attribute 2
      ((Variable) v.getTerm()).removeAttribute(attribute2);

      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      // remove attribute 2
      ((Variable) v.getTerm()).removeAttribute(attribute1);

      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, ((Variable) v.getTerm()).getAttributeOrDefault(attribute2, defaultValue));

      verifyNoInteractions(attribute1, attribute2, attribute3);
   }

   @Test
   public void testAttributesBacktrack() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Variable v1 = new Variable("X");
      Term defaultValue = new Atom("default");
      Term term1 = new Atom("term1");
      Term term2 = new Atom("term2");
      Term term3 = new Atom("term3");

      v1.putAttribute(attribute1, term1);
      Variable v2 = (Variable) v1.getTerm();
      assertNotSame(v1, v2);
      assertSame(v1.getId(), v2.getId());

      v2.putAttribute(attribute2, term2);
      Variable v3 = (Variable) v2.getTerm();
      assertNotSame(v2, v3);

      v3.putAttribute(attribute1, term3);
      Variable v4 = (Variable) v2.getTerm();
      assertNotSame(v3, v4);
      assertSame(v1.getId(), v4.getId());

      v4.removeAttribute(attribute1);
      Variable v5 = (Variable) v4.getTerm();
      assertNotSame(v4, v5);

      // attempting to remove an attribute that does not exist does not cause the variable to be reassigned
      v5.removeAttribute(attribute3);
      assertSame(v5, v5.getTerm());

      assertSame(defaultValue, v5.getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term2, v5.getAttributeOrDefault(attribute2, defaultValue));

      // backtracking a variable does not cause it to lose the attributes it was created with
      v5.backtrack();

      assertSame(defaultValue, v5.getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term2, v5.getAttributeOrDefault(attribute2, defaultValue));

      v4.backtrack();
      assertSame(term3, v4.getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term2, v4.getAttributeOrDefault(attribute2, defaultValue));

      v3.backtrack();
      assertSame(term1, v3.getAttributeOrDefault(attribute1, defaultValue));
      assertSame(term2, v3.getAttributeOrDefault(attribute2, defaultValue));

      v2.backtrack();
      assertSame(term1, v2.getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, v2.getAttributeOrDefault(attribute2, defaultValue));

      v1.backtrack();
      assertSame(defaultValue, v1.getAttributeOrDefault(attribute1, defaultValue));
      assertSame(defaultValue, v1.getAttributeOrDefault(attribute2, defaultValue));

      assertSame(v1.getId(), v5.getId());

      verifyNoInteractions(attribute1, attribute2, attribute3);
   }

   @Test
   public void testAttributesOnVariableAssignedToAtom() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Variable v1 = new Variable("X");
      v1.unify(new Atom("test"));
      assertNotSame(v1, v1.getTerm());

      try {
         v1.getAttributeOrDefault(attribute1, new Atom("test"));
         fail();
      } catch (IllegalStateException e) {
         // expected - should not be able to call getAttributeOrDefault on variable that is assigned to something else
      }

      try {
         v1.putAttribute(attribute1, new Atom("test"));
         fail();
      } catch (IllegalStateException e) {
         // expected - should not be able to call putAttribute on variable that is assigned to something else
      }

      try {
         v1.removeAttribute(attribute1);
         fail();
      } catch (IllegalStateException e) {
         // expected - should not be able to call removeAttribute on variable that is assigned to something else
      }

      verifyNoInteractions(attribute1, attribute2, attribute3);
   }

   @Test
   public void testAttributesOnVariableAssignedToAnotherVariable() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Variable v1 = new Variable("X");
      v1.putAttribute(attribute1, new Atom("test"));
      assertNotSame(v1, v1.getTerm());

      try {
         v1.getAttributeOrDefault(attribute1, new Atom("test"));
         fail();
      } catch (IllegalStateException e) {
         // expected - should not be able to call getAttributeOrDefault on variable that is assigned to something else
      }

      try {
         v1.putAttribute(attribute1, new Atom("test"));
         fail();
      } catch (IllegalStateException e) {
         // expected - should not be able to call putAttribute on variable that is assigned to something else
      }

      try {
         v1.removeAttribute(attribute1);
         fail();
      } catch (IllegalStateException e) {
         // expected - should not be able to call removeAttribute on variable that is assigned to something else
      }

      verifyNoInteractions(attribute1, attribute2, attribute3);
   }

   // variable with an attribute tries to unify to variable without an attribute, unification succeeds
   @Test
   public void testPostUnify1() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue = new Atom("attributeValue");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");
      originalX.putAttribute(attribute, attributeValue);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute.postUnify(newX, attributeValue)).thenReturn(true);

      assertTrue(newX.unify(y));

      verify(attribute).postUnify(newX, attributeValue);
      verifyNoMoreInteractions(attribute);
   }

   // variable with an attribute tries to unify to variable without an attribute, unification fails
   @Test
   public void testPostUnify2() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue = new Atom("attributeValue");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");
      originalX.putAttribute(attribute, attributeValue);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute.postUnify(newX, attributeValue)).thenReturn(false);

      assertFalse(newX.unify(y));

      verify(attribute).postUnify(newX, attributeValue);
      verifyNoMoreInteractions(attribute);
   }

   // variable without an attribute tries to unify to variable with an attribute, unification succeeds
   @Test
   public void testPostUnify3() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue = new Atom("attributeValue");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");
      originalX.putAttribute(attribute, attributeValue);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute.postUnify(newX, attributeValue)).thenReturn(true);

      assertTrue(y.unify(newX));

      verify(attribute).postUnify(newX, attributeValue);
      verifyNoMoreInteractions(attribute);
   }

   // variable without an attribute tries to unify to variable with an attribute, unification fails
   @Test
   public void testPostUnify4() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue = new Atom("attributeValue");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");
      originalX.putAttribute(attribute, attributeValue);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute.postUnify(newX, attributeValue)).thenReturn(false);

      assertFalse(y.unify(newX));

      verify(attribute).postUnify(newX, attributeValue);
      verifyNoMoreInteractions(attribute);
   }

   // variable with an attribute tries to unify to an atom and then to another variable, postUnify only called for the first unification
   @Test
   public void testPostUnify5() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue = new Atom("attributeValue");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");
      originalX.putAttribute(attribute, attributeValue);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute.postUnify(newX, attributeValue)).thenReturn(true);
      newX.unify(new Atom("test"));
      verify(attribute).postUnify(newX, attributeValue);

      assertTrue(newX.unify(y));

      verifyNoMoreInteractions(attribute);
   }

   // variable with an attribute tries to unify to an atom and then another variable tries to unify with the original variable, postUnify only called for the first unification
   @Test
   public void testPostUnify6() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue = new Atom("attributeValue");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");
      originalX.putAttribute(attribute, attributeValue);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute.postUnify(newX, attributeValue)).thenReturn(true);
      newX.unify(new Atom("test"));
      verify(attribute).postUnify(newX, attributeValue);

      assertTrue(y.unify(newX));

      verifyNoMoreInteractions(attribute);
   }

   // two variables both have an attribute, both calls to postUnify return true and unification succeeds
   // variables have different VariableAttribute
   @Test
   public void testPostUnify7() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Variable originalX = new Variable("X");
      Variable originalY = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      Variable newX = (Variable) originalX.getTerm();

      originalY.putAttribute(attribute2, attributeValue2);
      Variable newY = (Variable) originalY.getTerm();

      when(attribute1.postUnify(any(Variable.class), eq(attributeValue1))).thenReturn(true);
      when(attribute2.postUnify(any(Variable.class), eq(attributeValue2))).thenReturn(true);

      assertTrue(newX.unify(newY));

      assertSame(newX.getTerm(), newY.getTerm());
      assertNotSame(newX, newX.getTerm());
      assertNotSame(newY, newY.getTerm());

      verify(attribute1).postUnify((Variable) newX.getTerm(), attributeValue1);
      verify(attribute2).postUnify((Variable) newY.getTerm(), attributeValue2);
      verifyNoMoreInteractions(attribute1, attribute2);
   }

   // two variables both have an attribute, both calls to postUnify will return false
   // variables have different VariableAttribute
   @Test
   public void testPostUnify8() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Variable originalX = new Variable("X");
      Variable originalY = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      Variable newX = (Variable) originalX.getTerm();

      originalY.putAttribute(attribute2, attributeValue2);
      Variable newY = (Variable) originalY.getTerm();

      when(attribute1.postUnify(any(Variable.class), eq(attributeValue1))).thenReturn(false);
      when(attribute2.postUnify(any(Variable.class), eq(attributeValue2))).thenReturn(false);

      assertFalse(newX.unify(newY));

      assertSame(newX.getTerm(), newY.getTerm());
      assertNotSame(newX, newX.getTerm());
      assertNotSame(newY, newY.getTerm());

      verify(attribute1, atMost(1)).postUnify((Variable) newX.getTerm(), attributeValue1);
      verify(attribute2, atMost(1)).postUnify((Variable) newX.getTerm(), attributeValue2);
      verifyNoMoreInteractions(attribute1, attribute2);
   }

   // two variables both have an attribute, one call to postUnify will return false and the other will return true
   // variables have different VariableAttribute
   @Test
   public void testPostUnify9() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Variable originalX = new Variable("X");
      Variable originalY = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      Variable newX = (Variable) originalX.getTerm();

      originalY.putAttribute(attribute2, attributeValue2);
      Variable newY = (Variable) originalY.getTerm();

      when(attribute1.postUnify(any(Variable.class), eq(attributeValue1))).thenReturn(true);
      when(attribute1.postUnify(any(Variable.class), eq(attributeValue2))).thenReturn(false);

      assertFalse(newX.unify(newY));

      assertSame(newX.getTerm(), newY.getTerm());
      assertNotSame(newX, newX.getTerm());
      assertNotSame(newY, newY.getTerm());

      verify(attribute1, atMost(1)).postUnify((Variable) newX.getTerm(), attributeValue1);
      verify(attribute2).postUnify((Variable) newY.getTerm(), attributeValue2);
      verifyNoMoreInteractions(attribute1, attribute2);
   }

   // two variables both have an attribute, both calls to postUnify return true and unification succeeds
   // both variables have the same VariableAttribute
   @Test
   public void testPostUnify10() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Variable originalX = new Variable("X");
      Variable originalY = new Variable("Y");
      Variable combinedValue = new Variable("combinedValue");

      originalX.putAttribute(attribute, attributeValue1);
      Variable newX = (Variable) originalX.getTerm();

      originalY.putAttribute(attribute, attributeValue2);
      Variable newY = (Variable) originalY.getTerm();

      when(attribute.join(attributeValue1, attributeValue2)).thenReturn(combinedValue);
      when(attribute.postUnify(any(Variable.class), eq(combinedValue))).thenReturn(true);

      assertTrue(newX.unify(newY));

      assertSame(newX.getTerm(), newY.getTerm());
      assertNotSame(newX, newX.getTerm());
      assertNotSame(newY, newY.getTerm());

      verify(attribute).join(attributeValue1, attributeValue2);
      verify(attribute).postUnify((Variable) newX.getTerm(), combinedValue);
      verifyNoMoreInteractions(attribute);
   }

   // two variables both have an attribute, first call to postUnify returns false
   // both variables have the same VariableAttribute
   @Test
   public void testPostUnify11() {
      VariableAttribute attribute = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Variable originalX = new Variable("X");
      Variable originalY = new Variable("Y");
      Variable combinedValue = new Variable("combinedValue");

      originalX.putAttribute(attribute, attributeValue1);
      Variable newX = (Variable) originalX.getTerm();

      originalY.putAttribute(attribute, attributeValue2);
      Variable newY = (Variable) originalY.getTerm();

      when(attribute.join(attributeValue1, attributeValue2)).thenReturn(combinedValue);
      when(attribute.postUnify(any(Variable.class), eq(combinedValue))).thenReturn(false);

      assertFalse(newX.unify(newY));

      assertSame(newX.getTerm(), newY.getTerm());
      assertNotSame(newX, newX.getTerm());
      assertNotSame(newY, newY.getTerm());

      verify(attribute).join(attributeValue1, attributeValue2);
      verify(attribute).postUnify((Variable) newX.getTerm(), combinedValue);
      verifyNoMoreInteractions(attribute);
   }

   // variable with 3 attributes attempts to unify with variable with none, all attributes have postUnify called and unification succeeds
   @Test
   public void testPostUnify12() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Term attributeValue3 = new Atom("attributeValue3");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      ((Variable) originalX.getTerm()).putAttribute(attribute2, attributeValue2);
      ((Variable) originalX.getTerm()).putAttribute(attribute3, attributeValue3);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute1.postUnify(newX, attributeValue1)).thenReturn(true);
      when(attribute2.postUnify(newX, attributeValue2)).thenReturn(true);
      when(attribute3.postUnify(newX, attributeValue3)).thenReturn(true);

      assertTrue(newX.unify(y));

      verify(attribute1).postUnify(newX, attributeValue1);
      verify(attribute2).postUnify(newX, attributeValue2);
      verify(attribute3).postUnify(newX, attributeValue3);
      verifyNoMoreInteractions(attribute1, attribute2, attribute3);
   }

   // variable with 3 attributes attempts to unify with variable with none, all attributes have postUnify called and unification succeeds
   @Test
   public void testPostUnify13() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Term attributeValue3 = new Atom("attributeValue3");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      ((Variable) originalX.getTerm()).putAttribute(attribute2, attributeValue2);
      ((Variable) originalX.getTerm()).putAttribute(attribute3, attributeValue3);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute1.postUnify(newX, attributeValue1)).thenReturn(true);
      when(attribute2.postUnify(newX, attributeValue2)).thenReturn(true);
      when(attribute3.postUnify(newX, attributeValue3)).thenReturn(false);

      assertFalse(newX.unify(y));

      verify(attribute1, atMost(1)).postUnify(newX, attributeValue1);
      verify(attribute2, atMost(1)).postUnify(newX, attributeValue2);
      verify(attribute3).postUnify(newX, attributeValue3);
      verifyNoMoreInteractions(attribute1, attribute2, attribute3);
   }

   // variable without attributes attempts to unify with variable that has 3 attributes, all attributes have postUnify called and unification succeeds
   @Test
   public void testPostUnify14() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Term attributeValue3 = new Atom("attributeValue3");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      ((Variable) originalX.getTerm()).putAttribute(attribute2, attributeValue2);
      ((Variable) originalX.getTerm()).putAttribute(attribute3, attributeValue3);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute1.postUnify(newX, attributeValue1)).thenReturn(true);
      when(attribute2.postUnify(newX, attributeValue2)).thenReturn(true);
      when(attribute3.postUnify(newX, attributeValue3)).thenReturn(true);

      assertTrue(y.unify(newX));

      verify(attribute1).postUnify(newX, attributeValue1);
      verify(attribute2).postUnify(newX, attributeValue2);
      verify(attribute3).postUnify(newX, attributeValue3);
      verifyNoMoreInteractions(attribute1, attribute2, attribute3);
   }

   // variable without attributes attempts to unify with variable that has 3 attributes, one postUnify returns false and unification fails
   @Test
   public void testPostUnify15() {
      VariableAttribute attribute1 = mock(VariableAttribute.class);
      VariableAttribute attribute2 = mock(VariableAttribute.class);
      VariableAttribute attribute3 = mock(VariableAttribute.class);
      Term attributeValue1 = new Atom("attributeValue1");
      Term attributeValue2 = new Atom("attributeValue2");
      Term attributeValue3 = new Atom("attributeValue3");
      Variable originalX = new Variable("X");
      Variable y = new Variable("Y");

      originalX.putAttribute(attribute1, attributeValue1);
      ((Variable) originalX.getTerm()).putAttribute(attribute2, attributeValue2);
      ((Variable) originalX.getTerm()).putAttribute(attribute3, attributeValue3);
      Variable newX = (Variable) originalX.getTerm();

      when(attribute1.postUnify(newX, attributeValue1)).thenReturn(true);
      when(attribute2.postUnify(newX, attributeValue2)).thenReturn(true);
      when(attribute3.postUnify(newX, attributeValue3)).thenReturn(false);

      assertFalse(y.unify(newX));

      verify(attribute1, atMost(1)).postUnify(newX, attributeValue1);
      verify(attribute2, atMost(1)).postUnify(newX, attributeValue2);
      verify(attribute3).postUnify(newX, attributeValue3);
      verifyNoMoreInteractions(attribute1, attribute2, attribute3);
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
      assertVariableNotEqualToUnifiedTerm(StructureFactory.createStructure("test", new Term[] {atom()}));
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
