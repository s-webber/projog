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

import junit.framework.TestCase;

import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.term.Term;

public class AbstractRetryablePredicateTest extends TestCase {
   public void testSimpleImplementation() {
      AbstractRetryablePredicate pf = new AbstractRetryablePredicate() {
         @Override
         public Predicate getPredicate(Term... args) {
            return this;
         }

         @Override
         public boolean evaluate(Term... args) {
            return false;
         }
      };

      assertTrue(pf.isRetryable());
      assertTrue(pf.couldReEvaluationSucceed());
      assertSame(pf, pf.getPredicate());

      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      pf.setKnowledgeBase(kb);
      assertSame(kb, pf.getKnowledgeBase());
   }
}