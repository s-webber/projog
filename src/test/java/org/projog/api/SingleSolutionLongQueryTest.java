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

import org.projog.core.term.IntegerNumber;

public class SingleSolutionLongQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_ATOM_EXCEPTION_MESSAGE = "Expected ATOM but got: INTEGER with value: 42";
   private static final long LONG_VALUE = 42;

   public SingleSolutionLongQueryTest() {
      super("X = 42.");
   }

   @Override
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertEquals(new IntegerNumber(LONG_VALUE));
   }

   @Override
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertEquals(Optional.of(new IntegerNumber(LONG_VALUE)));
   }

   @Override
   public void testFindAllAsTerm() {
      findAllAsTerm().assertEquals(Collections.singletonList(new IntegerNumber(LONG_VALUE)));
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
      findFirstAsDouble().assertEquals((double) LONG_VALUE);
   }

   @Override
   public void testFindFirstAsOptionalDouble() {
      findFirstAsOptionalDouble().assertEquals(Optional.of((double) LONG_VALUE));
   }

   @Override
   public void testFindAllAsDouble() {
      findAllAsDouble().assertEquals(Collections.singletonList((double) LONG_VALUE));
   }

   @Override
   public void testFindFirstAsLong() {
      findFirstAsLong().assertEquals(LONG_VALUE);
   }

   @Override
   public void testFindFirstAsOptionalLong() {
      findFirstAsOptionalLong().assertEquals(Optional.of(LONG_VALUE));
   }

   @Override
   public void testFindAllAsLong() {
      findAllAsLong().assertEquals(Collections.singletonList(LONG_VALUE));
   }
}
