/*
 * Copyright 2013 S. Webber
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
package org.projog.core.kb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;

import java.util.List;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.event.ProjogListeners;
import org.projog.core.event.SpyPoints;
import org.projog.core.io.FileHandles;
import org.projog.core.math.ArithmeticOperators;
import org.projog.core.parser.Operands;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;

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

      PredicateKey input[] = {new PredicateKey(predicateName, 0), new PredicateKey(predicateName, 1), new PredicateKey(predicateName, 2), new PredicateKey(predicateName, 3)};

      for (PredicateKey key : input) {
         kb.getPredicates().createOrReturnUserDefinedPredicate(key);
         // add entries with a different name to the name we are calling getPredicateKeysByName with
         // to check that the method isn't just returning ALL keys
         PredicateKey keyWithDifferentName = new PredicateKey(predicateName + "X", key.getNumArgs());
         kb.getPredicates().createOrReturnUserDefinedPredicate(keyWithDifferentName);
      }

      List<PredicateKey> output = KnowledgeBaseUtils.getPredicateKeysByName(kb, predicateName);
      assertEquals(input.length, output.size());
      for (PredicateKey key : input) {
         assertTrue(output.contains(key));
      }
   }

   @Test
   public void testIsQuestionOrDirectiveFunctionCall() {
      assertTrue(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure("?-", atom())));
      assertTrue(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure("?-", structure("=", atom(), atom()))));
      assertTrue(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure(":-", atom())));
      assertTrue(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure(":-", structure("=", atom(), atom()))));

      assertFalse(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(atom("?-")));
      assertFalse(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure("?-", atom(), atom())));
      assertFalse(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(atom(":-")));
      assertFalse(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure(":-", atom(), atom())));
      assertFalse(KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall(structure(">=", atom())));
   }

   @Test
   public void testIsConjuction() {
      assertFalse(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("true.")));
      assertTrue(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("true, true.")));
      assertTrue(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("true, true, true.")));
      assertTrue(KnowledgeBaseUtils.isConjunction(TestUtils.parseSentence("repeat(3), X<1, write(V), nl, true, !, fail.")));
   }

   @Test
   public void testIsSingleAnswer_NonRetryablePredicate() {
      // test single term representing a predicate that is not repeatable
      assertTrue(KnowledgeBaseUtils.isSingleAnswer(kb, atom("true")));
   }

   @Test
   public void testIsSingleAnswer_RetryablePredicate() {
      // test single term representing a predicate that *is* repeatable
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, atom("repeat")));
   }

   @Test
   public void testIsSingleAnswer_NonRetryableConjuction() {
      // test conjunction of terms that are all not repeatable
      Term conjuctionOfNonRepeatableTerms = TestUtils.parseSentence("write(X), nl, true, X<1.");
      assertTrue(KnowledgeBaseUtils.isSingleAnswer(kb, conjuctionOfNonRepeatableTerms));
   }

   @Test
   public void testIsSingleAnswer_RetryableConjuction() {
      // test conjunction of terms where one is repeatable
      Term conjuctionIncludingRepeatableTerm = TestUtils.parseSentence("write(X), nl, repeat, true, X<1.");
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, conjuctionIncludingRepeatableTerm));
   }

   @Test
   public void testIsSingleAnswer_Disjunction() {
      // test disjunction
      // (Note that the disjunction used in the test *would* only give a single answer
      // but KnowledgeBaseUtils.isSingleAnswer is not currently smart enough to spot this)
      Term disjunctionOfTerms = TestUtils.parseSentence("true ; fail.");
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, disjunctionOfTerms));
   }

   @Test
   public void testIsSingleAnswer_Variable() {
      assertFalse(KnowledgeBaseUtils.isSingleAnswer(kb, variable()));
   }

   @Test
   public void testToArrayOfConjunctions() {
      Term t = TestUtils.parseSentence("a, b(1,2,3), c.");
      Term[] conjunctions = KnowledgeBaseUtils.toArrayOfConjunctions(t);
      assertEquals(3, conjunctions.length);
      assertSame(t.getArgument(0), conjunctions[0]);
      assertSame(t.getArgument(1).getArgument(0), conjunctions[1]);
      assertSame(t.getArgument(1).getArgument(1), conjunctions[2]);
   }

   @Test
   public void testToArrayOfConjunctionsNoneConjunctionArgument() {
      Term t = TestUtils.parseSentence("a(b(1,2,3), c).");
      Term[] conjunctions = KnowledgeBaseUtils.toArrayOfConjunctions(t);
      assertEquals(1, conjunctions.length);
      assertSame(t, conjunctions[0]);
   }

   @Test
   public void testGetProjogListeners() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      ProjogListeners o1 = kb1.getProjogListeners();
      ProjogListeners o2 = kb2.getProjogListeners();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getProjogListeners());
      assertSame(o2, kb2.getProjogListeners());
   }

   @Test
   public void testGetProjogProperties() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      ProjogProperties o1 = kb1.getProjogProperties();
      ProjogProperties o2 = kb2.getProjogProperties();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getProjogProperties());
      assertSame(o2, kb2.getProjogProperties());
   }

   @Test
   public void testGetOperands() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      Operands o1 = kb1.getOperands();
      Operands o2 = kb2.getOperands();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getOperands());
      assertSame(o2, kb2.getOperands());
   }

   @Test
   public void testGetTermFormatter() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      TermFormatter o1 = kb1.getTermFormatter();
      TermFormatter o2 = kb2.getTermFormatter();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getTermFormatter());
      assertSame(o2, kb2.getTermFormatter());
   }

   @Test
   public void testGetSpyPoints() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      SpyPoints o1 = kb1.getSpyPoints();
      SpyPoints o2 = kb2.getSpyPoints();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getSpyPoints());
      assertSame(o2, kb2.getSpyPoints());
   }

   @Test
   public void testGetFileHandles() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      FileHandles o1 = kb1.getFileHandles();
      FileHandles o2 = kb2.getFileHandles();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getFileHandles());
      assertSame(o2, kb2.getFileHandles());
   }

   @Test
   public void testArithmeticOperators() {
      KnowledgeBase kb1 = TestUtils.createKnowledgeBase();
      KnowledgeBase kb2 = TestUtils.createKnowledgeBase();
      ArithmeticOperators o1 = kb1.getArithmeticOperators();
      ArithmeticOperators o2 = kb2.getArithmeticOperators();
      assertNotNull(o1);
      assertNotNull(o2);
      assertNotSame(o1, o2);
      assertSame(o1, kb1.getArithmeticOperators());
      assertSame(o2, kb2.getArithmeticOperators());
   }
}
