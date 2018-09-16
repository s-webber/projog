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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.term.Term;

public class DefiniteClauseGrammerConvertorTest {
   @Test
   public void testIsDCG() {
      assertTrue(isDCG("a --> b."));
      assertTrue(isDCG("a --> b, c, d."));

      assertFalse(isDCG("a :- b."));
   }

   private boolean isDCG(String inputSyntax) {
      Term input = TestUtils.parseSentence(inputSyntax);
      return DefiniteClauseGrammerConvertor.isDCG(input);
   }

   @Test
   public void testSingleAtomAntecedant() {
      performConversion("a --> b.", "a(A1, A0) :- b(A1, A0)");
   }

   @Test
   public void testTwoAtomAntecedant() {
      performConversion("a --> b, c.", "a(A2, A0) :- b(A2, A1) , c(A1, A0)");
   }

   @Test
   public void testFiveAtomAntecedant() {
      performConversion("a --> b, c, d, e, f.", "a(A5, A0) :- b(A5, A4) , c(A4, A3) , d(A3, A2) , e(A2, A1) , f(A1, A0)");
   }

   @Test
   public void testSingleElementListAntecedant() {
      performConversion("a --> [xyz].", "a([xyz|A], A)");
   }

   @Test
   public void testConjunctionOfSingleElementListsAntecedant() {
      performConversion("test1 --> [a], [b], [c].", "test1([a,b,c|A0], A0)");
   }

   @Test
   public void testMixtureOfAtomsAndSingleElementLists() {
      performConversion("test1 --> p1, p2, p3, [a,x,y], p4, [c], p5.",
                  "test1(A7, A0) :- p1(A7, A6) , p2(A6, A5) , p3(A5, A4) , A4 = [a,x,y|A3] , p4(A3, A2) , A2 = [c|A1] , p5(A1, A0)");
      performConversion("test1 --> p1, p2, p3, [a,x,y], [b], p4, [c], p5.",
                  "test1(A7, A0) :- " + "p1(A7, A6) , " + "p2(A6, A5) , " + "p3(A5, A4) , " + "A4 = [a,x,y,b|A3] , " + "p4(A3, A2) , " + "A2 = [c|A1] , " + "p5(A1, A0)");

      performConversion("a --> b, [test].", "a(A2, A0) :- b(A2, A1) , A1 = [test|A0]");

      performConversion("a --> [test], b.", "a([test|A1], A0) :- b(A1, A0)");
   }

   @Test
   public void testPredicateAsAntecedantAndConsequent() {
      performConversion("a(Y) --> b(X).", "a(Y, A1, A0) :- b(X, A1, A0)");
      performConversion("a(1,Y,X) --> b(2,X,Y).", "a(1, Y, X, A1, A0) :- b(2, X, Y, A1, A0)");
   }

   @Test
   public void testCurlyBrackets() {
      performConversion("test1(X) --> [X], {atom(X)}.", "test1(X, [X|A0], A0) :- atom(X)");
      performConversion("test2(X) --> {Y is X+1}, xyz(Y).", "test2(X, A1, A0) :- Y is X + 1 , xyz(Y, A1, A0)");
      performConversion("test1(X) --> p, {atom(X)}.", "test1(X, A1, A0) :- p(A1, A0) , atom(X)");
   }

   @Test
   public void testSingleListAntecedant() {
      performConversion("test(qwerty) --> [qwerty].", "test(qwerty, [qwerty|A], A)");
      performConversion("test(qwerty) --> [x].", "test(qwerty, [x|A], A)");
   }

   private void performConversion(String inputSyntax, String expectedOutputSyntax) {
      Term input = TestUtils.parseSentence(inputSyntax);
      Term output = DefiniteClauseGrammerConvertor.convert(input);
      assertEquals(expectedOutputSyntax, TestUtils.write(output));
   }
}
