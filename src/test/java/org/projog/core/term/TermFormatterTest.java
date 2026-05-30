/*
pom * Copyright 2013-2014 S. Webber
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseSentence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TermFormatterTest {
   private static final TermFormatter TERM_FORMATTER = createKnowledgeBase().getTermFormatter();

   @ParameterizedTest
   @ValueSource(strings = {
               "?- X = -1 + 1.684 , p(1, 7.3, [_,[]|c])", //
               "a :- z , (b , c ; e) , f", //
               "X = (1 :- 2) * (3 :- 4)", //
               "X = (?- 1) * (?- 2)", //
               "X = - Y"})
   public void testFormatTerm(String inputSyntax) {
      Term inputTerm = parseSentence(inputSyntax + ".");

      assertEquals(inputSyntax, TERM_FORMATTER.formatTerm(inputTerm));
   }
}
