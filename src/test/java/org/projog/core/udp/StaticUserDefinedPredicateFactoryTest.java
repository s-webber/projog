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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.udp.compiler.CompiledPredicate;
import org.projog.core.udp.compiler.CompiledTailRecursivePredicate;
import org.projog.core.udp.interpreter.InterpretedTailRecursivePredicateFactory;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * Tests {@link StaticUserDefinedPredicateFactory}.
 *
 * @see org.projog.TestUtils#COMPILATION_ENABLED_PROPERTIES
 */
public class StaticUserDefinedPredicateFactoryTest {
   private static final KnowledgeBase COMPILATION_ENABLED_KB = TestUtils.createKnowledgeBase(TestUtils.COMPILATION_ENABLED_PROPERTIES);
   private static final KnowledgeBase COMPILATION_DISABLED_KB = TestUtils.createKnowledgeBase(TestUtils.COMPILATION_DISABLED_PROPERTIES);
   private static final String[] RECURSIVE_PREDICATE_SYNTAX = {"concatenate([],L,L).", "concatenate([X|L1],L2,[X|L3]) :- concatenate(L1,L2,L3)."};
   private static final String[] NON_RECURSIVE_PREDICATE_SYNTAX = {"p(X,Y,Z) :- repeat(3), X<Y.", "p(X,Y,Z) :- X is Y+Z.", "p(X,Y,Z) :- X=a."};

   @Test
   public void testTrue() {
      PredicateFactory pf = getActualPredicateFactory(toTerms("p."));
      assertSame(SingleRuleAlwaysTruePredicate.class, pf.getClass());
      Predicate p = pf.getPredicate();
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
   }

   @Test
   public void testRepeatSetAmount() {
      Term[] clauses = toTerms("p.", "p.", "p.");
      int expectedSuccessfulEvaluations = clauses.length;
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(MultipleRulesAlwaysTruePredicate.class, pf.getClass());
      Predicate p = pf.getPredicate();
      assertTrue(p.couldReevaluationSucceed());
      for (int i = 0; i < expectedSuccessfulEvaluations; i++) {
         assertTrue(p.couldReevaluationSucceed());
         assertTrue(p.evaluate());
      }
      assertFalse(p.couldReevaluationSucceed());
      assertFalse(p.evaluate());
   }

   @Test
   public void testSingleRuleWithSingleImmutableArgumentPredicate() {
      Term clause = TestUtils.parseTerm("p(a)");
      PredicateFactory pf = getActualPredicateFactory(clause);
      assertSame(SingleRuleWithSingleImmutableArgumentPredicate.class, pf.getClass());
      SingleRuleWithSingleImmutableArgumentPredicate sr = (SingleRuleWithSingleImmutableArgumentPredicate) pf;
      assertStrictEquality(clause.getArgument(0), sr.data);
   }

   @Test
   public void testMultipleRulesWithSingleImmutableArgumentPredicate() {
      Term[] clauses = toTerms("p(a).", "p(b).", "p(c).");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(MultipleRulesWithSingleImmutableArgumentPredicate.class, pf.getClass());
      MultipleRulesWithSingleImmutableArgumentPredicate mr = (MultipleRulesWithSingleImmutableArgumentPredicate) pf;
      assertEquals(clauses.length, mr.data.length);
      for (int i = 0; i < clauses.length; i++) {
         assertStrictEquality(clauses[i].getArgument(0), mr.data[i]);
      }
   }

   @Test
   public void testSingleRuleWithMultipleImmutableArgumentsPredicate() {
      Term clause = TestUtils.parseTerm("p(a,b,c).");
      PredicateFactory pf = getActualPredicateFactory(clause);
      assertSame(SingleRuleWithMultipleImmutableArgumentsPredicate.class, pf.getClass());
      SingleRuleWithMultipleImmutableArgumentsPredicate sr = (SingleRuleWithMultipleImmutableArgumentsPredicate) pf;
      assertEquals(clause.getNumberOfArguments(), sr.data.length);
      for (int i = 0; i < clause.getNumberOfArguments(); i++) {
         assertStrictEquality(clause.getArgument(i), sr.data[i]);
      }
   }

   @Test
   public void testMultipleRulesWithMultipleImmutableArgumentsPredicate() {
      Term[] clauses = toTerms("p(a,b,c).", "p(1,2,3).", "p(x,y,z).");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(MultipleRulesWithMultipleImmutableArgumentsPredicate.class, pf.getClass());
      MultipleRulesWithMultipleImmutableArgumentsPredicate mr = (MultipleRulesWithMultipleImmutableArgumentsPredicate) pf;
      assertEquals(clauses.length, mr.data.length);
      for (int c = 0; c < clauses.length; c++) {
         assertEquals(clauses[c].getNumberOfArguments(), mr.data[c].length);
         for (int a = 0; a < clauses[c].getNumberOfArguments(); a++) {
            assertStrictEquality(clauses[c].getArgument(a), mr.data[c][a]);
         }
      }
   }

   @Test
   public void testInterpretedTailRecursivePredicateFactory() {
      PredicateFactory pf = getActualPredicateFactory(toTerms(RECURSIVE_PREDICATE_SYNTAX));
      assertSame(InterpretedTailRecursivePredicateFactory.class, pf.getClass());
   }

   @Test
   public void testCompiledTailRecursivePredicate() {
      PredicateFactory pf = getActualPredicateFactory(COMPILATION_ENABLED_KB, toTerms(RECURSIVE_PREDICATE_SYNTAX));
      assertTrue(pf instanceof CompiledPredicate);
      assertTrue(pf instanceof CompiledTailRecursivePredicate);
   }

   @Test
   public void testInterpretedUserDefinedPredicate() {
      PredicateFactory pf = getActualPredicateFactory(toTerms(NON_RECURSIVE_PREDICATE_SYNTAX));
      assertSame(InterpretedUserDefinedPredicate.class, pf.getPredicate().getClass());
   }

   @Test
   public void testCompiledPredicate() {
      PredicateFactory pf = getActualPredicateFactory(COMPILATION_ENABLED_KB, toTerms(NON_RECURSIVE_PREDICATE_SYNTAX));
      assertTrue(pf instanceof CompiledPredicate);
      assertFalse(pf instanceof CompiledTailRecursivePredicate);
   }

   @Test
   public void testConjunctionContainingVariables() {
      Term[] clauses = toTerms("and(X,Y) :- X, Y.");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(InterpretedUserDefinedPredicate.class, pf.getPredicate().getClass());
   }

   @Test
   public void testVariableAntecedant() {
      Term[] clauses = toTerms("true(X) :- X.");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(InterpretedUserDefinedPredicate.class, pf.getPredicate().getClass());
   }

   private PredicateFactory getActualPredicateFactory(Term... clauses) {
      return getActualPredicateFactory(COMPILATION_DISABLED_KB, clauses);
   }

   private PredicateFactory getActualPredicateFactory(KnowledgeBase kb, Term... clauses) {
      StaticUserDefinedPredicateFactory f = null;
      for (Term clause : clauses) {
         if (f == null) {
            PredicateKey key = PredicateKey.createForTerm(clause);
            f = new StaticUserDefinedPredicateFactory(key);
            f.setKnowledgeBase(kb);
         }
         ClauseModel clauseModel = ClauseModel.createClauseModel(clause);
         f.addLast(clauseModel);
      }
      return f.getActualPredicateFactory();
   }

   private Term[] toTerms(String... clausesSyntax) {
      Term[] clauses = new Term[clausesSyntax.length];
      for (int i = 0; i < clauses.length; i++) {
         clauses[i] = TestUtils.parseSentence(clausesSyntax[i]);
      }
      return clauses;
   }

   private void assertStrictEquality(Term t1, Term t2) {
      assertTrue("Term: " + t1 + " is not strictly equal to term: " + t2, t1.strictEquality(t2));
   }
}
