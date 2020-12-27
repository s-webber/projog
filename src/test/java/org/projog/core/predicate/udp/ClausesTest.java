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
package org.projog.core.predicate.udp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.projog.TestUtils.createClauseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.udp.ClauseAction;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.Clauses;

public class ClausesTest {
   @Test
   public void testEmpty() {
      Clauses c = Clauses.createFromModels(TestUtils.createKnowledgeBase(), Collections.<ClauseModel> emptyList());
      assertEquals(0, c.getClauseActions().length); // TODO use assertEmpty
      assertEquals(0, c.getImmutableColumns().length); // TODO use assertEmpty
   }

   @Test
   public void testSingleNoArgClause() {
      Clauses c = createClauses("p.");
      assertEmpty(c.getImmutableColumns());
   }

   @Test
   public void testSingleOneArgImmutableClause() {
      Clauses c = createClauses("p(x).");
      assertArrayEquals(new int[] {0}, c.getImmutableColumns());
   }

   @Test
   public void testSingleTwoArgImmutableClause() {
      Clauses c = createClauses("p(x,y).");
      assertArrayEquals(new int[] {0, 1}, c.getImmutableColumns());
   }

   @Test
   public void testSingleNineArgImmutableClause() {
      Clauses c = createClauses("p(a,b,c,d,e,f,g,h,i).");
      assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, c.getImmutableColumns());
   }

   @Test
   public void testSingleOneArgMutableClause() {
      Clauses c = createClauses("p(X).");
      assertEmpty(c.getImmutableColumns());
   }

   @Test
   public void testSingleTwoArgMutableClause() {
      Clauses c = createClauses("p(X,y).");
      assertArrayEquals(new int[] {1}, c.getImmutableColumns());
   }

   @Test
   public void testSingleManyArgsMutableClause() {
      Clauses c = createClauses("p(a,b,X,Y,e,f,g,h,i,Z,k,l).");
      assertArrayEquals(new int[] {0, 1, 4, 5, 6, 7, 8, 10, 11}, c.getImmutableColumns());
   }

   @Test
   public void testManyImmutableClauses() {
      Clauses c = createClauses("p(a,b,c).", "p(d,e,f).", "p(g,h,i).");
      assertArrayEquals(new int[] {0, 1, 2}, c.getImmutableColumns());
   }

   @Test
   public void testManyImmutableClausesWithAntecedant() {
      Clauses c = createClauses("p(a,b,c).", "p(d,e,f) :- x(z).", "p(g,h,i).");
      assertArrayEquals(new int[] {0, 1, 2}, c.getImmutableColumns());
   }

   @Test
   public void testManyMutableClausesWithoutIndexableArgs() {
      Clauses c = createClauses("p(a,X,c).", "p(Y,e,f).", "p(g,h,Z).");
      assertEmpty(c.getImmutableColumns());
   }

   @Test
   public void testManyMutableClausesWithIndexableArgs() {
      Clauses c = createClauses("p(a,X,c,d,e,f).", "p(Y,o,e,f,Q,r).", "p(g,h,e,u,p,Z).");
      assertArrayEquals(new int[] {2, 3}, c.getImmutableColumns());
   }

   @Test
   public void testManyMutableClausesWithoutAntecedantsWithIndexableArgs() {
      Clauses c = createClauses("p(a,X,c,d,e,f).", "p(Y,o,e,f,Q,r).", "p(g,h,e,u,p,Z).");
      assertArrayEquals(new int[] {2, 3}, c.getImmutableColumns());
   }

   @Test
   public void testSingleImmutableRule() {
      Clauses c = createClauses("p(x) :- z(a,b,c).");
      assertArrayEquals(new int[] {0}, c.getImmutableColumns());
   }

   @Test
   public void testSingleAlwaysMatchedRule() {
      Clauses c = createClauses("p(X) :- z(a,b,c).");
      assertEmpty(c.getImmutableColumns());
   }

   @Test
   public void testClauseActions() {
      Clauses c = createClauses("p(x,y).", "p(X,Y) :- a(X), b(Y).");
      ClauseAction[] actions = c.getClauseActions();
      assertEquals(2, actions.length);
      assertEquals("p(x, y)", actions[0].getModel().getOriginal().toString());
      assertEquals(":-(p(X, Y), ,(a(X), b(Y)))", actions[1].getModel().getOriginal().toString());
   }

   private Clauses createClauses(String... clauses) { // TODO move to TestUtils
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      List<ClauseModel> models = new ArrayList<>();
      for (String clause : clauses) {
         models.add(createClauseModel(clause));
      }
      return Clauses.createFromModels(kb, models);
   }

   private void assertEmpty(int[] array) { // TODO more to TestUtils
      assertEquals(0, array.length);
   }
}
