/*
 * Copyright 2013 S Webber
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
package org.projog.core.function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;

public class AbstractSingletonPredicateTest {
   @Test
   public void testSimpleImplementation() {
      PredicateFactory pf = new AbstractSingletonPredicate() {
         @Override
         public boolean evaluate(Term... args) {
            return false;
         }
      };

      Predicate p = pf.getPredicate((Term[]) null);
      assertFalse(p.isRetryable());
      assertFalse(p.couldReEvaluationSucceed());
      assertSame(pf, p);

      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      pf.setKnowledgeBase(kb);
      assertSame(kb, ((AbstractSingletonPredicate) pf).getKnowledgeBase());
   }
}