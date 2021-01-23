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

import java.util.Arrays;
import java.util.Optional;

import org.projog.core.term.Atom;

public class MultiSolutionsAtomQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_NUMERIC_EXCEPTION_MESSAGE = "Expected Numeric but got: ATOM with value: a";
   private static final String FIRST_ATOM_NAME = "a";
   private static final String SECOND_ATOM_NAME = "b";
   private static final String THIRD_ATOM_NAME = "c";

   public MultiSolutionsAtomQueryTest() {
      super("test(X).", "test(a).test(b).test(c).");
   }

   @Override
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertEquals(new Atom(FIRST_ATOM_NAME));
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertEquals(Optional.of(new Atom(FIRST_ATOM_NAME)));
   }

   @Override
   public void testFindAllAsTerm() {
      findAllAsTerm().assertEquals(Arrays.asList(new Atom(FIRST_ATOM_NAME), new Atom(SECOND_ATOM_NAME), new Atom(THIRD_ATOM_NAME)));
   }

   @Override
   public void testFindFirstAsAtomName() {
      findFirstAsAtomName().assertEquals(FIRST_ATOM_NAME);
   }

   @Override
   public void testFindFirstAsOptionalAtomName() {
      findFirstAsOptionalAtomName().assertEquals(Optional.of(FIRST_ATOM_NAME));
   }

   @Override
   public void testFindAllAsAtomName() {
      findAllAsAtomName().assertEquals(Arrays.asList(FIRST_ATOM_NAME, SECOND_ATOM_NAME, THIRD_ATOM_NAME));
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
