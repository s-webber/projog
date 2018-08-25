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
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseSentence;
import static org.projog.core.KnowledgeBaseUtils.getTermFormatter;

import org.junit.Test;

public class TermFormatterTest {
   @Test
   public void testTermToString() {
      String inputSyntax = "?- X = -1 + 1.684 , p(1, 7.3, [_,[]|c])";
      Term inputTerm = parseSentence(inputSyntax + ".");

      TermFormatter tf = createFormatter();
      assertEquals(inputSyntax, tf.toString(inputTerm));
   }

   private TermFormatter createFormatter() {
      return getTermFormatter(createKnowledgeBase());
   }
}
