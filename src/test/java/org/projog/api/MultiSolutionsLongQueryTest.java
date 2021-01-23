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

import org.projog.core.term.IntegerNumber;

public class MultiSolutionsLongQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_ATOM_EXCEPTION_MESSAGE = "Expected an atom but got: INTEGER with value: 42";
   private static final long FIRST_LONG_VALUE = 42;
   private static final long SECOND_LONG_VALUE = 180;
   private static final long THIRD_LONG_VALUE = -7;

   public MultiSolutionsLongQueryTest() {
      super("test(X).", "test(42).test(180).test(-7).");
   }

   @Override
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertEquals(new IntegerNumber(FIRST_LONG_VALUE));
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertEquals(Optional.of(new IntegerNumber(FIRST_LONG_VALUE)));
   }

   @Override
   public void testFindAllAsTerm() {
      findAllAsTerm().assertEquals(Arrays.asList(new IntegerNumber(FIRST_LONG_VALUE), new IntegerNumber(SECOND_LONG_VALUE), new IntegerNumber(THIRD_LONG_VALUE)));
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
      findFirstAsDouble().assertEquals((double) FIRST_LONG_VALUE);
   }

   @Override
   public void testFindFirstAsOptionalDouble() {
      findFirstAsOptionalDouble().assertEquals(Optional.of((double) FIRST_LONG_VALUE));
   }

   @Override
   public void testFindAllAsDouble() {
      findAllAsDouble().assertEquals(Arrays.asList((double) FIRST_LONG_VALUE, (double) SECOND_LONG_VALUE, (double) THIRD_LONG_VALUE));
   }

   @Override
   public void testFindFirstAsLong() {
      findFirstAsLong().assertEquals(FIRST_LONG_VALUE);
   }

   @Override
   public void testFindFirstAsOptionalLong() {
      findFirstAsOptionalLong().assertEquals(Optional.of(FIRST_LONG_VALUE));
   }

   @Override
   public void testFindAllAsLong() {
      findAllAsLong().assertEquals(Arrays.asList(FIRST_LONG_VALUE, SECOND_LONG_VALUE, THIRD_LONG_VALUE));
   }
}
