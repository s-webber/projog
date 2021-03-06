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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.assertStrictEquality;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.decimalFraction;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.list;
import static org.projog.TestUtils.parseSentence;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @see TermTest
 */
public class StructureTest {
   @Test
   public void testCreationWithArguments() {
      Term[] args = {atom(), structure(), integerNumber(), decimalFraction(), variable()};
      Structure p = structure("test", args);
      assertEquals("test", p.getName());
      assertArrayEquals(args, p.getArgs());
      assertEquals(5, p.getNumberOfArguments());
      for (int i = 0; i < args.length; i++) {
         assertSame(args[i], p.getArgument(i));
      }
      assertSame(TermType.STRUCTURE, p.getType());
      assertEquals("test(test, test(test), 1, 1.0, X)", p.toString());
   }

   @Test
   public void testGetValueNoVariables() {
      Structure p = structure("p", atom(), structure("p", atom()), list(integerNumber(), decimalFraction()));
      Structure p2 = p.getTerm();
      assertSame(p, p2);
   }

   @Test
   public void testGetValueUnassignedVariables() {
      Structure p = structure("p", variable(), structure("p", variable()), list(variable(), variable()));
      assertSame(p, p.getTerm());
   }

   @Test
   public void testGetValueAssignedVariable() {
      Variable x = variable("X");
      Structure p1 = structure("p", atom(), structure("p", atom(), x, integerNumber()), list(integerNumber(), decimalFraction()));
      x.unify(atom());
      Structure p2 = p1.getTerm();
      assertNotSame(p1, p2);
      assertEquals(p1.toString(), p2.toString());
      assertStrictEquality(p1, p2, true);
   }

   @Test
   public void testGetBoundNoVariables() {
      Structure p = structure("p", atom(), structure("p", atom()), list(integerNumber(), decimalFraction()));
      assertSame(p, p.getBound());
   }

   @Test
   public void testGetBoundUnassignedVariables() {
      Structure p = structure("p", variable(), structure("p", variable()), list(variable(), variable()));
      assertSame(p, p.getBound());
   }

   @Test
   public void testGetBoundAssignedVariable() {
      Variable x = variable("X");
      Structure p = structure("p", atom(), structure("p", atom(), x, integerNumber()), list(integerNumber(), decimalFraction()));
      x.unify(atom());
      assertSame(p, p.getBound());
   }

   @Test
   public void testCreationList() {
      Term t = Structure.createStructure(".", new Term[] {atom("a"), atom("b")});
      assertEquals(TermType.LIST, t.getType());
      assertTrue(t instanceof List);
      Term l = parseSentence("[a | b].");
      assertEquals(l.toString(), t.toString());
   }

   @Test
   public void testUnifyWhenBothPredicatesHaveVariableArguments() {
      // test(x, Y)
      Structure p1 = structure("test", new Atom("x"), new Variable("Y"));
      // test(X, y)
      Structure p2 = structure("test", new Variable("X"), new Atom("y"));
      assertTrue(p1.unify(p2));
      assertEquals("test(x, y)", p1.toString());
      assertEquals(p1.toString(), p2.toString());
   }

   @Test
   public void testUnifyWhenPredicateHasSameVariableTwiceAsArgument() {
      // test(x, y)
      Structure p1 = structure("test", new Atom("x"), new Atom("y"));
      // test(X, X)
      Variable v = new Variable("X");
      Structure p2 = structure("test", v, v);

      assertFalse(p2.unify(p1));
      assertEquals("test(x, y)", p1.toString());
      // Note: following is expected quirk - predicate doesn't automatically backtrack on failure
      assertEquals("test(x, x)", p2.toString());

      p2.backtrack();
      assertEquals("test(X, X)", p2.toString());

      assertFalse(p1.unify(p2));
      assertEquals("test(x, y)", p1.toString());
      // Note: following is expected quirk - predicate doesn't automatically backtrack on failure
      assertEquals("test(x, x)", p2.toString());

      p2.backtrack();
      assertEquals("test(X, X)", p2.toString());
   }

   @Test
   public void testUnifyVariableThatIsPredicateArgument() {
      // test(X, X)
      Variable v = new Variable("X");
      Structure p = structure("test", v, v);
      assertEquals("test(X, X)", p.toString());
      assertTrue(v.unify(new Atom("x")));
      assertEquals("test(x, x)", p.toString());
   }

   @Test
   public void testUnifyDifferentNamesSameArguments() {
      Term[] args = {atom(), integerNumber(), decimalFraction()};
      Structure p1 = structure("test1", args);
      Structure p2 = structure("test2", args);
      Structure p3 = structure("test", args);
      assertStrictEqualityAndUnify(p1, p2, false);
      assertStrictEqualityAndUnify(p1, p3, false);
   }

   @Test
   public void testSameNamesDifferentArguments() {
      Structure[] predicates = {
                  structure("test1", new Atom("a"), new Atom("b"), new Atom("c")),
                  structure("test2", new Atom("a"), new Atom("b"), new Atom("d")),
                  structure("test3", new Atom("a"), new Atom("c"), new Atom("b")),
                  structure("test4", new Atom("a"), new Atom("b"))};
      for (int i = 0; i < predicates.length; i++) {
         for (int j = i; j < predicates.length; j++) {
            if (i == j) {
               // check they all compare to a copy of themselves
               assertStrictEqualityAndUnify(predicates[i], predicates[i].copy(null), true);
            } else {
               assertStrictEqualityAndUnify(predicates[i], predicates[j], false);
            }
         }
      }
   }

   @Test
   public void testUnifyWrongType() {
      Structure p = structure("1", new Term[] {atom()});
      assertStrictEqualityAndUnify(p, new Atom("1"), false);
      assertStrictEqualityAndUnify(p, new IntegerNumber(1), false);
      assertStrictEqualityAndUnify(p, new DecimalFraction(1), false);
   }

   @Test
   public void testNoArguments() {
      try {
         structure("test", TermUtils.EMPTY_ARRAY);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Cannot create structure with no arguments", e.getMessage());
      }
   }

   @Test
   public void testCopyWithoutVariablesOrNestedArguments() {
      Structure p = structure("test", atom(), integerNumber(), decimalFraction());
      Structure copy = p.copy(null);
      assertSame(p, copy);
   }

   @Test
   public void testCopyWithUnassignedVariables() {
      // create structure where some arguments are variables
      String name = "test";
      Atom a = atom();
      IntegerNumber i = integerNumber();
      DecimalFraction d = decimalFraction();
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      Structure original = structure(name, a, x, i, y, d, x);

      // make a copy
      HashMap<Variable, Variable> sharedVariables = new HashMap<Variable, Variable>();
      Structure copy = original.copy(sharedVariables);

      // compare copy to original
      assertEquals(2, sharedVariables.size());
      assertEquals("X", sharedVariables.get(x).getId());
      assertNotEquals(x, sharedVariables.get(x));
      assertEquals("Y", sharedVariables.get(y).getId());
      assertNotEquals(y, sharedVariables.get(y));

      assertEquals(name, copy.getName());
      assertEquals(6, copy.getNumberOfArguments());
      assertSame(a, copy.getArgs()[0]);
      assertSame(sharedVariables.get(x), copy.getArgs()[1]);
      assertSame(i, copy.getArgs()[2]);
      assertSame(sharedVariables.get(y), copy.getArgs()[3]);
      assertSame(d, copy.getArgs()[4]);
      assertSame(sharedVariables.get(x), copy.getArgs()[5]);
   }

   @Test
   public void testCopyWithAssignedVariable() {
      Variable X = new Variable("X");
      Structure arg = structure("p", X);
      Structure original = structure("p", arg);

      assertSame(original, original.getTerm());

      Map<Variable, Variable> sharedVariables = new HashMap<>();
      Structure copy1 = original.copy(sharedVariables);
      assertNotSame(original, copy1);
      assertStrictEquality(original, copy1, false);
      assertEquals(1, sharedVariables.size());
      assertTrue(sharedVariables.containsKey(X));
      assertEquals(original.toString(), copy1.toString());

      X.unify(atom("a"));

      Structure copy2 = original.copy(null);
      assertNotSame(original, copy2);
      assertStrictEquality(original, copy2, true);
      assertEquals(original.toString(), copy2.toString());
      assertSame(copy2, copy2.copy(null));
      assertSame(copy2, copy2.getTerm());

      X.backtrack();

      assertStrictEquality(original, copy2, false);

      assertEquals("p(p(X))", original.toString());
      assertEquals("p(p(a))", copy2.toString());
   }

   @Test
   public void testIsImmutable() {
      Variable v = variable("X");
      Atom a = atom("test");
      Structure p1 = structure("p", atom(), structure("p", atom(), v, integerNumber()), list(integerNumber(), decimalFraction()));
      assertFalse(p1.isImmutable());
      v.unify(a);
      Structure p2 = p1.copy(null);
      assertFalse(p1.isImmutable());
      assertTrue(p2.isImmutable());
      assertSame(v, p1.getArgument(1).getArgument(1));
      assertSame(a, p2.getArgument(1).getArgument(1));
   }

   @Test
   public void testBacktrack() {
      Variable x = variable("X");
      Variable y = variable("Y");
      Variable z = variable("Z");
      Atom a1 = atom("test1");
      Atom a2 = atom("test2");
      Atom a3 = atom("test3");
      Structure p1 = structure("p", x, structure("p", a1, y));
      Structure p2 = structure("p", z);
      x.unify(p2);
      y.unify(a2);
      z.unify(a3);

      assertSame(p2, x.getBound());
      assertSame(a2, y.getBound());
      assertSame(a3, z.getBound());

      p1.backtrack();

      // Note that backtracking unbounds X and Y from the "p2" structure
      // but it doesn't unbound the Z variable of the "p2" structure.
      assertSame(x, x.getBound());
      assertSame(y, y.getBound());
      assertSame(a3, z.getBound());
   }

   @Test
   public void testHashCode() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      Atom c = new Atom("c");
      Structure p = structure("p", a, b, c);

      assertHashCodeEquals(p, structure("p", a, b, c));
      assertHashCodeEquals(p, structure("p", new Atom("a"), new Atom("b"), new Atom("c")));

      assertHashCodeNotEquals(structure("p", a, b), structure("p", b, a));
      assertHashCodeNotEquals(structure("p", structure("p", a, b), structure("p", b, a)), //
                  structure("p", structure("p", b, a), structure("p", a, b)));

      // assert order of args affects hashCode
      assertHashCodeNotEquals(p, structure("p", a, c, b));
      assertHashCodeNotEquals(p, structure("p", b, a, c));
      assertHashCodeNotEquals(p, structure("p", b, c, a));
      assertHashCodeNotEquals(p, structure("p", c, a, b));
      assertHashCodeNotEquals(p, structure("p", c, b, a));

      // assert number of args affects hashCode
      assertHashCodeNotEquals(p, structure("p", a));
      assertHashCodeNotEquals(p, structure("p", a, b, c, a));

      // assert functor affects hashCode
      assertHashCodeNotEquals(p, structure("P", a, b, c));
      assertHashCodeNotEquals(p, structure("pp", a, b, c));

      // assert arg types affects hashCode
      assertHashCodeNotEquals(structure("p", new DecimalFraction(1)), structure("p", new IntegerNumber(1)));
   }

   private void assertHashCodeEquals(Object a, Object b) { // TODO move to TestUtils and use from other Term tests
      assertEquals(a.hashCode(), b.hashCode());
   }

   private void assertHashCodeNotEquals(Object a, Object b) {
      assertNotEquals(a.hashCode(), b.hashCode());
   }

   private void assertStrictEqualityAndUnify(Term t1, Term t2, boolean expectedResult) {
      assertStrictEquality(t1, t2, expectedResult);
      assertTrue(t1.unify(t2) == expectedResult);
   }
}
