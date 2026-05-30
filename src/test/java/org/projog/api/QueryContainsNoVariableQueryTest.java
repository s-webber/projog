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

import org.junit.jupiter.api.Test;

public class QueryContainsNoVariableQueryTest extends AbstractQueryTest {
   private static final String EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE = "Expected exactly one uninstantiated variable but found none in: true {}";

   public QueryContainsNoVariableQueryTest() {
      super("true.");
   }

   @Override
   @Test
   public void testFindFirstAsTerm() {
      findFirstAsTerm().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsOptionalTerm() {
      findFirstAsOptionalTerm().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindAllAsTerm() {
      findAllAsTerm().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsAtomName() {
      findFirstAsAtomName().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsOptionalAtomName() {
      findFirstAsOptionalAtomName().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindAllAsAtomName() {
      findAllAsAtomName().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsDouble() {
      findFirstAsDouble().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsOptionalDouble() {
      findFirstAsOptionalDouble().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindAllAsDouble() {
      findAllAsDouble().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsLong() {
      findFirstAsLong().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindFirstAsOptionalLong() {
      findFirstAsOptionalLong().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }

   @Override
   @Test
   public void testFindAllAsLong() {
      findAllAsLong().assertException(EXPECTED_ONE_VARIABLE_EXCEPTION_MESSAGE);
   }
}
