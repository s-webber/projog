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

import java.util.Collections;
import java.util.Optional;

import org.projog.core.term.Atom;

public class SingleSolutionAtomQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_NUMERIC_EXCEPTION_MESSAGE = "Expected Numeric but got: ATOM with value: test";
   private static final String ATOM_NAME = "test";

   public SingleSolutionAtomQueryTest() {
      super("X = test.");
   }

   @Override
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertEquals(new Atom(ATOM_NAME));
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertEquals(Optional.of(new Atom(ATOM_NAME)));
   }

   @Override
   public void testFindAllAsTerm() {
      findAllAsTerm().assertEquals(Collections.singletonList(new Atom(ATOM_NAME)));
   }

   @Override
   public void testFindFirstAsAtomName() {
      findFirstAsAtomName().assertEquals(ATOM_NAME);
   }

   @Override
   public void testFindFirstAsOptionalAtomName() {
      findFirstAsOptionalAtomName().assertEquals(Optional.of(ATOM_NAME));
   }

   @Override
   public void testFindAllAsAtomName() {
      findAllAsAtomName().assertEquals(Collections.singletonList(ATOM_NAME));
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
