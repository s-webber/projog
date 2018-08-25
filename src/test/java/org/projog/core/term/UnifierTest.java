/*
 * Copyright 2013-2014 S. Webber
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;

import org.junit.Test;

public class UnifierTest {
   /** [a] unified with [a] */
   @Test
   public void testExactMatchSingleImmutableArguments() {
      Atom inputArg = new Atom("a");
      Atom consequentArg = new Atom("a");
      Term[] input = {inputArg};
      Term[] consequent = {consequentArg};
      assertPreMatch(input, consequent);
      assertSame(inputArg, input[0]);
      assertSame(consequentArg, consequent[0]);
   }

   /** [a] unified with [b] */
   @Test
   public void testNoMatchSingleImmutableArguments() {
      Atom a = new Atom("a");
      Atom b = new Atom("b");
      assertPreMatchFailed(new Term[] {a}, new Term[] {b});
   }

   @Test
   public void testExactMatchManyImmutableArguments() {
      Term[] inputArgs = createListOfImmutableArguments();
      Term[] copyInputArgs = copy(inputArgs);

      Term[] consequentArgs = createListOfImmutableArguments();
      Term[] copyConsequentArgs = copy(consequentArgs);

      assertPreMatch(inputArgs, consequentArgs);

      for (int i = 0; i < copyInputArgs.length; i++) {
         assertSame(copyInputArgs[i], inputArgs[i]);
         assertSame(copyConsequentArgs[i], consequentArgs[i]);
         assertNotSame(inputArgs[i], consequentArgs[i]);
      }
   }

   private Term[] createListOfImmutableArguments() {
      return new Term[] {atom("a"), integerNumber(1), decimalFraction(1.5), structure("p", atom("x"), atom("y"))};
   }

   /** [X] unified with [a] */
   @Test
   public void testSingleVariableInInput() {
      Atom a = atom("a");
      Variable v = variable("X");
      Term[] input = {v};
      Term[] consequent = {a};
      assertPreMatch(input, consequent);
      assertSame(v, input[0]);
      assertSame(a, v.getTerm());
      assertSame(a, consequent[0]);
   }

   /** [a] unified with [X] */
   @Test
   public void testSingleVariableInConsequent() {
      Atom a = atom("a");
      Variable v = variable("X");
      Term[] input = {a};
      Term[] consequent = {v};
      assertPreMatch(input, consequent);
      assertSame(a, input[0]);
      assertSame(a, consequent[0]);
   }

   /** [X] unified with [Y] */
   @Test
   public void testVariableInInputAndConsequent() {
      Variable v1 = variable("X");
      Variable v2 = variable("Y");
      Term[] input = {v1};
      Term[] consequent = {v2};
      assertPreMatch(input, consequent);
      assertSame(v1, input[0]);
      assertSame(v2, consequent[0]);
   }

   /** [X] unified with [Y] when X already unified to a */
   @Test
   public void testSingleAssignedVariableInInput_1() {
      Variable x = variable("X");
      Variable y = variable("Y");
      Atom a = atom();

      x.unify(a);

      Term[] input = {x};
      Term[] consequent = {y};

      assertPreMatch(input, consequent);

      assertSame(x, input[0]);
      assertSame(a, x.getTerm());
      assertSame(a, consequent[0]);
   }

   /** [X] unified with [a] when X already unified to a */
   @Test
   public void testSingleAssignedVariableInInput_2() {
      Variable x = variable("X");
      Atom a1 = atom("a");
      Atom a2 = atom("a");

      x.unify(a1);

      Term[] input = {x};
      Term[] consequent = {a2};

      assertPreMatch(input, consequent);

      assertSame(x, input[0]);
      assertSame(a1, x.getTerm());
      assertSame(a2, consequent[0]);
   }

   /** [X] unified with [b] when X already unified to a */
   @Test
   public void testSingleAssignedVariableInInput_3() {
      Variable x = variable("X");
      Atom a = atom("a");
      Atom b = atom("b");

      x.unify(a);

      Term[] input = {x};
      Term[] consequent = {b};

      assertFalse(Unifier.preMatch(input, consequent));

      assertSame(x, input[0]);
      assertSame(a, x.getTerm());
      assertSame(b, consequent[0]);
   }

   /** [a, X] unified with [Y, b] */
   @Test
   public void testPrematch_1() {
      Atom a = atom("a");
      Variable x = variable("X");
      Term[] input = {a, x};

      Variable y = variable("Y");
      Atom b = atom("b");
      Term[] consequent = {y, b};

      assertPreMatch(input, consequent);

      assertSame(a, input[0]);
      assertSame(x, input[1]);
      assertSame(a, consequent[0]);
      assertSame(b, consequent[1]);

      assertSame(b, input[1].getTerm());
   }

   /** [a, X, 1] unified with [Y, b, 2] */
   @Test
   public void testPrematch_2() {
      Atom a = atom("a");
      Variable x = variable("X");
      IntegerNumber i1 = integerNumber(1);
      Term[] input = {a, x, i1};

      Variable y = variable("Y");
      Atom b = atom("b");
      IntegerNumber i2 = integerNumber(2);
      Term[] consequent = {y, b, i2};

      assertPreMatchFailed(input, consequent);

      assertSame(x, x.getTerm());
      assertSame(y, y.getTerm());
   }

   /** [p(X), a] unified with [p(Y), Y] */
   @Test
   public void testPrematch_3() {
      Variable x = variable("X");
      Term inputArg1 = structure("p", x);
      Term inputArg2 = atom("a");
      Term[] input = {inputArg1, inputArg2};

      Variable y = variable("Y");
      Term consequentArg1 = structure("p", y);
      Term consequentArg2 = y;
      Term[] consequent = {consequentArg1, consequentArg2};

      assertPreMatch(input, consequent);

      // input args should still refer to the same term instances
      assertSame(inputArg1, input[0]);
      assertSame(inputArg2, input[1]);

      // consequent second argument should of been replaced with input second argument
      assertSame(inputArg2, consequent[1]);

      // predicate that is first argument of consquent should now have an atom as 
      // its single argument rather than a variable
      assertSame(inputArg2, consequent[0].getArgument(0));

      // predicate that is first argument of input should still be a variable 
      // but now it should be unified with an atom
      assertSame(x, input[0].getArgument(0));
      assertSame(inputArg2, x.getTerm());

      // unification of input argument should be undone on backtrack
      inputArg1.backtrack();
      assertSame(x, x.getTerm());
   }

   /** [p(Y), Y] unified with [p(X), a] */
   @Test
   public void testPrematch_4() {
      Variable y = variable("Y");
      Term inputArg1 = structure("p", y);
      Term inputArg2 = y;
      Term[] input = {inputArg1, inputArg2};

      Variable x = variable("X");
      Term consequentArg1 = structure("p", x);
      Term consequentArg2 = atom("a");
      Term[] consequent = {consequentArg1, consequentArg2};

      assertPreMatch(input, consequent);

      assertSame(inputArg1, input[0]);
      assertSame(inputArg2, input[1]);
      assertSame(y, input[0].getArgument(0));
      assertSame(y, input[1]);
      assertSame(consequentArg2, y.getTerm());

      assertSame(consequentArg2, consequent[0].getArgument(0));
      assertSame(consequentArg2, consequent[1]);

      // unification of input arguments should be undone on backtrack
      TermUtils.backtrack(input);
      assertSame(y, y.getTerm());
      assertSame(y, input[0].getArgument(0));
      assertSame(y, input[1]);
   }

   /** [p(Y), Y] unified with [p(a), b] */
   @Test
   public void testPrematch_5() {
      Variable y = variable("Y");
      Structure inputArg1 = structure("p", y);
      Term[] input = {inputArg1, y};

      Structure consequentArg1 = structure("p", atom("a"));
      Atom consequentArg2 = atom("b");
      Term[] consequent = {consequentArg1, consequentArg2};

      assertPreMatchFailed(input, consequent);

      assertSame(inputArg1, input[0]);
      assertSame(y, input[1]);
      assertSame(y, input[0].getArgument(0));
      assertSame(y, y.getTerm());
   }

   /** [p(a), b] unified with [p(Y), Y] */
   @Test
   public void testPrematch_6() {
      Variable y = variable("Y");

      assertPreMatchFailed(new Term[] {structure("p", atom("a")), atom("b")}, new Term[] {structure("p", y), y});
   }

   private void assertPreMatch(Term[] input, Term[] consequent) {
      assertTrue(Unifier.preMatch(input, consequent));
   }

   private void assertPreMatchFailed(Term[] input, Term[] consequent) {
      Term[] copyInput = copy(input);
      Term[] copyConsequent = copy(consequent);

      assertFalse(Unifier.preMatch(input, consequent));

      TermUtils.backtrack(input);
      for (int i = 0; i < input.length; i++) {
         assertSame(copyInput[i], input[i]);
         assertSame(copyInput[i], input[i].getTerm());
      }

      TermUtils.backtrack(consequent);
      for (int i = 0; i < consequent.length; i++) {
         assertSame(copyConsequent[i], consequent[i]);
         assertSame(copyConsequent[i], consequent[i].getTerm());
      }
   }

   private Term[] copy(Term[] in) {
      Term[] out = new Term[in.length];
      System.arraycopy(in, 0, out, 0, in.length);
      return out;
   }
}
