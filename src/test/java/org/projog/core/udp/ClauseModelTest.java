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
package org.projog.core.udp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.term.Term;

public class ClauseModelTest {
   @Test
   public void testSingleTerm() {
      assertClauseModel("a.", "a", "true");
   }

   @Test
   public void testSimpleImplication() {
      assertClauseModel("a :- true.", "a", "true");
   }

   @Test
   public void testConjunctionImplication() {
      assertClauseModel("a :- b, c, d.", "a", ",(,(b, c), d)");
   }

   @Test
   public void testDefinteClauseGrammer() {
      assertClauseModel("a --> b, c.", "a(A2, A0)", ",(b(A2, A1), c(A1, A0))");
   }

   private void assertClauseModel(String inputSyntax, String consequentSyntax, String antecedantSyntax) {
      ClauseModel ci = TestUtils.createClauseModel(inputSyntax);
      assertToString(consequentSyntax, ci.getConsequent());
      assertToString(antecedantSyntax, ci.getAntecedant());
   }

   private void assertToString(String syntax, Term t1) {
      assertEquals(syntax, t1.toString());
   }
}
