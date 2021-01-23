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

public class NoSolutionQueryTest extends AbstractQueryTest {
   private static final String NO_SOLUTION_EXCEPTION_MESSAGE = "No solution found.";

   public NoSolutionQueryTest() {
      super("X = true, fail.");
   }

   @Override
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertException(NO_SOLUTION_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertEquals(Optional.empty());
   }

   @Override
   public void testFindAllAsTerm() {
      findAllAsTerm().assertEquals(Collections.emptyList());
   }

   @Override
   public void testFindFirstAsAtomName() {
      findFirstAsAtomName().assertException(NO_SOLUTION_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalAtomName() {
      findFirstAsOptionalAtomName().assertEquals(Optional.empty());
   }

   @Override
   public void testFindAllAsAtomName() {
      findAllAsAtomName().assertEquals(Collections.emptyList());
   }

   @Override
   public void testFindFirstAsDouble() {
      findFirstAsDouble().assertException(NO_SOLUTION_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalDouble() {
      findFirstAsOptionalDouble().assertEquals(Optional.empty());
   }

   @Override
   public void testFindAllAsDouble() {
      findAllAsDouble().assertEquals(Collections.emptyList());
   }

   @Override
   public void testFindFirstAsLong() {
      findFirstAsLong().assertException(NO_SOLUTION_EXCEPTION_MESSAGE);
   }

   @Override
   public void testFindFirstAsOptionalLong() {
      findFirstAsOptionalLong().assertEquals(Optional.empty());
   }

   @Override
   public void testFindAllAsLong() {
      findAllAsLong().assertEquals(Collections.emptyList());
   }
}
