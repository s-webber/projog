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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.decimalFraction;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.list;
import static org.projog.TestUtils.structure;
import static org.projog.TestUtils.variable;
import static org.projog.core.term.TermComparator.TERM_COMPARATOR;

import org.junit.Test;

public class TermComparatorTest {
   /**
    * selection of terms ordered in lowest precedence first order
    * <p>
    * Note: only one variable and no ANONYMOUS_VARIABLE as ANONYMOUS_VARIABLE and "variable against variable"
    * comparisons tested separately.
    */
   private static final Term[] TERMS_ORDERED_IN_LOWEST_PRECEDENCE = {variable("A"),

   decimalFraction(-2.1), decimalFraction(-1.9), decimalFraction(0), decimalFraction(1),

   integerNumber(-2), integerNumber(0), integerNumber(1),

   EmptyList.EMPTY_LIST,

   atom("a"), atom("z"),

   structure("a", atom("b")), structure("b", atom("a")), structure("b", structure("a", atom())),

   structure("!", atom("a"), atom("b")),

   list(atom("a"), atom("b")), list(atom("b"), atom("a")), list(atom("b"), atom("a"), atom("b")), list(atom("c"), atom("a")), list(structure("a", atom()), atom("b")),

   structure("a", atom("a"), atom("b")), structure("a", atom("a"), atom("z")), structure("a", atom("a"), structure("z", atom()))};

   @Test
   public void testCompareTerms() {
      for (int i = 0; i < TERMS_ORDERED_IN_LOWEST_PRECEDENCE.length; i++) {
         Term t1 = TERMS_ORDERED_IN_LOWEST_PRECEDENCE[i];
         testEqual(t1, t1);
         for (int z = i + 1; z < TERMS_ORDERED_IN_LOWEST_PRECEDENCE.length; z++) {
            Term t2 = TERMS_ORDERED_IN_LOWEST_PRECEDENCE[z];
            testIsGreater(t2, t1);

            Term v1 = variable("X");
            Term v2 = variable("Y");
            v1.unify(t1);
            v2.unify(t2);
            testIsGreater(v2, t1);
            testIsGreater(t2, v1);
            testIsGreater(v2, v1);
         }
      }
   }

   @Test
   public void testVariablesAssignedToEachOther() {
      Atom a = atom("a");
      Variable x = new Variable("X");
      Variable y = new Variable("Y");
      Variable z = new Variable("Z");

      testNotEqual(z, y);
      testNotEqual(z, x);
      testNotEqual(y, x);

      x.unify(z);

      testNotEqual(z, y);
      testEqual(z, x);
      testNotEqual(x, y);

      x.unify(atom("a"));

      testEqual(x, z);
      testEqual(x, a);
      testEqual(z, a);
      testIsGreater(x, y);
      testIsGreater(z, y);

      y.unify(x);
      testEqual(x, y);
      testEqual(x, z);
      testEqual(y, z);
      testEqual(z, a);
   }

   private void testNotEqual(Term t1, Term t2) {
      assertTrue(t1 + " " + t2, TERM_COMPARATOR.compare(t1, t2) != 0);
      assertTrue(t2 + " " + t1, TERM_COMPARATOR.compare(t2, t1) != 0);
   }

   private void testEqual(Term t1, Term t2) {
      assertEquals(t1 + " " + t2, 0, TERM_COMPARATOR.compare(t1, t2));
      assertEquals(t2 + " " + t1, 0, TERM_COMPARATOR.compare(t2, t1));
   }

   private void testIsGreater(Term t1, Term t2) {
      assertTrue(t1 + " " + t2, TERM_COMPARATOR.compare(t1, t2) > 0);
      assertTrue(t2 + " " + t1, TERM_COMPARATOR.compare(t2, t1) < 0);
   }
}
