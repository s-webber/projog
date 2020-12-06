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
package org.projog.core.function.math;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ArithmeticOperators;
import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.PredicateKey;
import org.projog.core.term.Term;

public class RandomTest {
   /** Because random is not pure we do not want it to be preprocessed. */
   @Test
   public void testNotPreprocessed() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      Term expression = TestUtils.parseSentence("random(" + Long.MAX_VALUE + ").");
      ArithmeticOperators operators = KnowledgeBaseUtils.getArithmeticOperators(kb);
      Random r = (Random) operators.getArithmeticOperator(PredicateKey.createForTerm(expression));

      assertSame(r, r.preprocess(expression));
      assertSame(r, r.preprocess(expression));
   }
}
