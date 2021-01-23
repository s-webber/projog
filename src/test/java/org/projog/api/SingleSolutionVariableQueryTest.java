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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class SingleSolutionVariableQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_NUMERIC_EXCEPTION_MESSAGE = "Expected Numeric but got: VARIABLE with value: X";
   private static final String EXPECTED_ATOM_EXCEPTION_MESSAGE = "Expected an atom but got: VARIABLE with value: X";

   public SingleSolutionVariableQueryTest() {
      super("var(X).");
   }

   @Override
   public void testFindFirstAsTerm() {
      Term result = findFirstAsTerm().get();
      assertTrue(result.getType().isVariable());
      assertEquals("X", ((Variable) result).getId());
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      Term result = findFirstAsOptionalTerm().get().get();
      assertTrue(result.getType().isVariable());
      assertEquals("X", ((Variable) result).getId());
   }

   @Override
   public void testFindAllAsTerm() {
      List<Term> results = findAllAsTerm().get();
      assertEquals(1, results.size());
      Term result = results.get(0);
      assertTrue(result.getType().isVariable());
      assertEquals("X", ((Variable) result).getId());
   }

   @Override
   public void testFindFirstAsAtomName() {
      findFirstAsAtomName().assertException(EXPECTED_ATOM_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalAtomName() {
      findFirstAsOptionalAtomName().assertException(EXPECTED_ATOM_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindAllAsAtomName() {
      findAllAsAtomName().assertException(EXPECTED_ATOM_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsDouble() {
      findFirstAsDouble().assertException(EXPECTED_NUMERIC_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalDouble() {
      findFirstAsOptionalDouble().assertException(EXPECTED_NUMERIC_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindAllAsDouble() {
      findAllAsDouble().assertException(EXPECTED_NUMERIC_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsLong() {
      findFirstAsLong().assertException(EXPECTED_NUMERIC_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalLong() {
      findFirstAsOptionalLong().assertException(EXPECTED_NUMERIC_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindAllAsLong() {
      findAllAsLong().assertException(EXPECTED_NUMERIC_EXCEPTION_MESSAGE);
   }
}
