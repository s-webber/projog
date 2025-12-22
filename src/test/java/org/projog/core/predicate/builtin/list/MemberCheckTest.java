/*
 * Copyright 2021 S. Webber
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
package org.projog.core.predicate.builtin.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.term.Term;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class MemberCheckTest {
   @Test
   public void testPreprocessed() {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("memberchk(X, [a,b,c]).");
      MemberCheck m = (MemberCheck) kb.getPredicates().getPredicateFactory(term);

      PredicateFactory optimised = m.preprocess(term);

      assertEquals("org.projog.core.predicate.builtin.list.MemberCheck$ImmutableListMemberCheck", optimised.getClass().getName());
   }

   @Test
   @DataProvider(splitBy = " ", value = {
               "[]",
               "[a|b]",
               "[X,Y,Z]",
               "[X,b,c]",
               "[a,X,c]",
               "[a,b,X]",
               "[[X],b,c]",
               "[[a,b,X],b,c]",
               "[a,[b|X],c]",
               "[a,b,c(X,b,c)]",
               "[a,b,c(a,X,c)]",
               "[a,b,c(a,b,X)]",})
   public void testNotPreprocessed(String list) {
      KnowledgeBase kb = createKnowledgeBase();
      Term term = parseTerm("memberchk(X, " + list + ").");
      MemberCheck m = (MemberCheck) kb.getPredicates().getPredicateFactory(term);

      PredicateFactory optimised = m.preprocess(term);

      assertSame(m, optimised);
   }
}
