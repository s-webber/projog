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
package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.structure;

import java.util.List;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.bool.True;
import org.projog.core.term.Term;

public class KnowledgeBaseUtilsTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testConjunctionPredicateName() {
      assertEquals(",", KnowledgeBaseUtils.CONJUNCTION_PREDICATE_NAME);
   }

   @Test
   public void testImplicationPredicateName() {
      assertEquals(":-", KnowledgeBaseUtils.IMPLICATION_PREDICATE_NAME);
   }

   @Test
   public void testQuestionPredicateName() {
      assertEquals("?-", KnowledgeBaseUtils.QUESTION_PREDICATE_NAME);
   }

   @Test
   public void testGetPredicateKeysByName() {
      String predicateName = "testGetPredicateKeysByName";

      assertTrue(KnowledgeBaseUtils.getPredicateKeysByName(kb, predicateName).isEmpty());

      PredicateKey input[] = {new PredicateKey(predicateName, -1), new PredicateKey(predicateName, 0), new PredicateKey(predicateName, 1), new PredicateKey(predicateName, 2), new PredicateKey(predicateName, 3)};

      for (PredicateKey key : input) {
         kb.createOrReturnUserDefinedPredicate(key);
         // add entries with a different name to the name we are calling getPredicateKeysByName with
         // to check that the method isn't just returning ALL keys
         PredicateKey keyWithDifferentName = new PredicateKey(predicateName + "X", key.getNumArgs());
         kb.createOrReturnUserDefinedPredicate(keyWithDifferentName);
      }

      List<PredicateKey> output = KnowledgeBaseUtils.getPredicateKeysByName(kb, predicateName);
      assertEquals(input.length, output.size());
      for (PredicateKey key : input) {
         assertTrue(output.contains(key));
      }
   }

   @Test
   public void testGetPredicate() {
      assertGetPredicate(atom("true"), True.class);
      assertGetPredicate(atom("does_not_exist"), UnknownPredicate.class);
   }

   private void assertGetPredicate(Term input, Class<?> expected) {
      Predicate e = KnowledgeBaseUtils.getPredicate(kb, input);
      assertSame(expected, e.getClass());
   }

   @Test
   public void testIsQuestionFunctionCall() {
      assertTrue(KnowledgeBaseUtils.isQuestionFunctionCall(structure("?-", atom())));
      assertTrue(KnowledgeBaseUtils.isQuestionFunctionCall(structure("?-", structure("=", atom(), atom()))));

      assertFalse(KnowledgeBaseUtils.isQuestionFunctionCall(atom("?-")));
      assertFalse(KnowledgeBaseUtils.isQuestionFunctionCall(structure("?-")));
      assertFalse(KnowledgeBaseUtils.isQuestionFunctionCall(structure("?-", atom(), atom())));
      assertFalse(KnowledgeBaseUtils.isQuestionFunctionCall(structure(":-", atom())));
   }

   @Test
   public void testIsDynamicFunctionCall() {
      assertTrue(KnowledgeBaseUtils.isDynamicFunctionCall(structure("dynamic", atom())));
      assertTrue(KnowledgeBaseUtils.isDynamicFunctionCall(structure("dynamic", structure("=", atom(), atom()))));

      assertFalse(KnowledgeBaseUtils.isDynamicFunctionCall(atom("dynamic")));
      assertFalse(KnowledgeBaseUtils.isDynamicFunctionCall(structure("dynamic")));
      assertFalse(KnowledgeBaseUtils.isDynamicFunctionCall(structure("dynamic", atom(), atom())));
      assertFalse(KnowledgeBaseUtils.isDynamicFunctionCall(structure(":-", atom())));
   }

   @Test
   public void testIsConjuction() {
      assertFalse(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("true.")));
      assertTrue(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("true, true.")));
      assertTrue(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("true, true, true.")));
      assertTrue(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("repeat(3), X<1, write(V), nl, true, !, fail.")));
   }

   @Test
   public void testIsSingleAnswer() {
      // test single term representing a predicate that is not repeatable
      assertTrue(KnowledgeBaseUtils.isSingleAnswer(kb, atom("true")));

      // test single term representing a predicate that *is* repeatable
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, atom("repeat")));

      // test conjunction of terms that are all not repeatable
      Term conjuctionOfNonRepeatableTerms = TestUtils.parseSentence("write(X), nl, true, X<1.");
      assertTrue(KnowledgeBaseUtils.isSingleAnswer(kb, conjuctionOfNonRepeatableTerms));

      // test conjunction of terms where one is repeatable
      Term conjuctionIncludingRepeatableTerm = TestUtils.parseSentence("write(X), nl, repeat, true, X<1.");
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, conjuctionIncludingRepeatableTerm));

      // test disjunction
      // (Note that the disjunction used in the test *would* only give a single answer 
      // but KnowledgeBaseUtils.isSingleAnswer is not currently smart enough to spot this)
      Term disjunctionOfTerms = TestUtils.parseSentence("true ; fail.");
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, disjunctionOfTerms));
   }

   @Test
   public void testToArrayOfConjunctions() {
      Term t = TestUtils.parseSentence("a, b(1,2,3), c.");
      Term[] conjunctions = KnowledgeBaseUtils.toArrayOfConjunctions(t);
      assertEquals(3, conjunctions.length);
      assertSame(t.getArgument(0).getArgument(0), conjunctions[0]);
      assertSame(t.getArgument(0).getArgument(1), conjunctions[1]);
      assertSame(t.getArgument(1), conjunctions[2]);
   }

   @Test
   public void testToArrayOfConjunctionsNoneConjunctionArgument() {
      Term t = TestUtils.parseSentence("a(b(1,2,3), c).");
      Term[] conjunctions = KnowledgeBaseUtils.toArrayOfConjunctions(t);
      assertEquals(1, conjunctions.length);
      assertSame(t, conjunctions[0]);
   }
}