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
package org.projog.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.projog.core.PredicateKey;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/**
 * Simply tests get methods of {@link ProjogStackTraceElement} (as that is the only functionality the class provides).
 * <p>
 * For a more thorough test, including how it is used by {@link Projog#getStackTrace(Throwable)}, see
 * {@link ProjogTest#testIOExceptionWhileEvaluatingQueries()}.
 */
public class ProjogStackTraceElementTest {
   @Test
   public void test() {
      final PredicateKey key = new PredicateKey("test", 1);
      final int clauseIdx = 9;
      final Term term = new Atom("test");
      final ProjogStackTraceElement e = new ProjogStackTraceElement(key, clauseIdx, term);
      assertSame(key, e.getPredicateKey());
      assertSame(term, e.getTerm());
      assertEquals(clauseIdx, e.getClauseIdx());
   }
}
