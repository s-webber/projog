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

import org.projog.core.term.DecimalFraction;

public class MultiSolutionsDoubleQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_ATOM_EXCEPTION_MESSAGE = "Expected an atom but got: FRACTION with value: 42.5";
   private static final double FIRST_DOUBLE_VALUE = 42.5;
   private static final double SECOND_DOUBLE_VALUE = 180.2;
   private static final double THIRD_DOUBLE_VALUE = -7;

   public MultiSolutionsDoubleQueryTest() {
      super("test(X).", "test(42.5).test(180.2).test(-7.0).");
   }

   @Override
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertEquals(new DecimalFraction(FIRST_DOUBLE_VALUE));
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertEquals(Optional.of(new DecimalFraction(FIRST_DOUBLE_VALUE)));
   }

   @Override
   public void testFindAllAsTerm() {
      findAllAsTerm().assertEquals(Arrays.asList(new DecimalFraction(FIRST_DOUBLE_VALUE), new DecimalFraction(SECOND_DOUBLE_VALUE), new DecimalFraction(THIRD_DOUBLE_VALUE)));
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
      findFirstAsDouble().assertEquals(FIRST_DOUBLE_VALUE);
   }

   @Override
   public void testFindFirstAsOptionalDouble() {
      findFirstAsOptionalDouble().assertEquals(Optional.of(FIRST_DOUBLE_VALUE));
   }

   @Override
   public void testFindAllAsDouble() {
      findAllAsDouble().assertEquals(Arrays.asList(FIRST_DOUBLE_VALUE, SECOND_DOUBLE_VALUE, THIRD_DOUBLE_VALUE));
   }

   @Override
   public void testFindFirstAsLong() {
      findFirstAsLong().assertEquals((long) FIRST_DOUBLE_VALUE);
   }

   @Override
   public void testFindFirstAsOptionalLong() {
      findFirstAsOptionalLong().assertEquals(Optional.of((long) FIRST_DOUBLE_VALUE));
   }

   @Override
   public void testFindAllAsLong() {
      findAllAsLong().assertEquals(Arrays.asList((long) FIRST_DOUBLE_VALUE, (long) SECOND_DOUBLE_VALUE, (long) THIRD_DOUBLE_VALUE));
   }
}
