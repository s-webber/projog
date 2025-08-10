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
package org.projog.core.predicate.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.structure;

import java.util.Arrays;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.Atom;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/**
 * Tests {@link StaticUserDefinedPredicateFactory}.
 * <p>
 * See also system tests in src/test/prolog/udp/predicate-meta-data
 */
public class StaticUserDefinedPredicateFactoryTest {
   private static final String[] RECURSIVE_PREDICATE_SYNTAX = {"concatenate([],L,L).", "concatenate([X|L1],L2,[X|L3]) :- concatenate(L1,L2,L3)."};
   private static final String[] NON_RECURSIVE_PREDICATE_SYNTAX = {"p(X,Y,Z) :- repeat(3), X<Y.", "p(X,Y,Z) :- X is Y+Z.", "p(X,Y,Z) :- X=a."};

   @Test
   public void testSingleFact() {
      assertSingleRuleAlwaysTruePredicate("p.");
      assertSingleRuleAlwaysTruePredicate("p(_).");
      assertSingleRuleAlwaysTruePredicate("p(X).");
      assertSingleRuleAlwaysTruePredicate("p(A,B,C).");
      assertSingleRuleAlwaysTruePredicate("p(A,_,C).");
   }

   private void assertSingleRuleAlwaysTruePredicate(String term) {
      PredicateFactory pf = getActualPredicateFactory(toTerms(term));
      assertSame(SingleNonRetryableRulePredicateFactory.class, pf.getClass());
      Predicate p = pf.getPredicate(new Atom("p"));
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertFalse(pf.isRetryable());
   }

   @Test
   public void testSingleNonRetryableRule() {
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- fail.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- true.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- nl.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- write(A), nl.");
   }

   @Test
   public void testSingleRuleAlwaysCutsOnBacktrack() {
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- !.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- repeat, !.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- nl, !.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- !, nl.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- nl, !, nl.");
      assertSingleNonRetryableRulePredicateFactory("p(A,_,C) :- repeat, !, nl.");
   }

   private void assertSingleNonRetryableRulePredicateFactory(String term) {
      PredicateFactory pf = getActualPredicateFactory(toTerms(term));
      assertSame(SingleNonRetryableRulePredicateFactory.class, pf.getClass());
      Predicate p = pf.getPredicate(new Atom("p"));
      assertFalse(p.couldReevaluationSucceed());
      assertFalse(pf.isRetryable());
   }

   @Test
   public void testRepeatSetAmount() {
      assertRepeatSetAmount("p(_).");
      assertRepeatSetAmount("p(X).");
      assertRepeatSetAmount("p(A,B,C).");
      assertRepeatSetAmount("p(A,_,C).");
   }

   private void assertRepeatSetAmount(String term) {
      Term[] clauses = toTerms(term, term, term);
      int expectedSuccessfulEvaluations = clauses.length;
      PredicateFactory pf = getActualPredicateFactory(clauses);
      // Note that use to return specialised "MultipleRulesAlwaysTruePredicate" object for predicates of this style
      // but now use generic "InterpretedUserDefinedPredicatePredicateFactory" as seemed overly complex to support
      // this special case when it is so rarely used.
      Predicate p = pf.getPredicate(Structure.createStructure("p", createArgs(clauses[0])));
      assertSame(InterpretedUserDefinedPredicate.class, p.getClass());
      assertTrue(p.couldReevaluationSucceed());
      for (int i = 0; i < expectedSuccessfulEvaluations; i++) {
         assertTrue(p.couldReevaluationSucceed());
         assertTrue(p.evaluate());
      }
      assertFalse(p.couldReevaluationSucceed());
      assertFalse(p.evaluate());
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testSingleFactWithSingleImmutableArgumentPredicate() {
      PredicateFactory pf = getActualPredicateFactory("p(a).");
      assertSame(SingleNonRetryableRulePredicateFactory.class, pf.getClass());
      assertFalse(pf.isRetryable());
   }

   @Test
   public void testMultipleFactsWithSingleImmutableArgumentPredicate() {
      PredicateFactory pf = getActualPredicateFactory("p(a).", "p(b).", "p(c).");
      assertLinkedHashMapPredicateFactory(pf);
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testMultipleFactsWithSingleImmutableArgumentPredicate_duplicates() {
      PredicateFactory pf = getActualPredicateFactory("p(a).", "p(a).", "p(a).");
      assertSingleIndexPredicateFactory(pf);
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testMultipleFactsWithSingleImmutableArgumentPredicate_differentTypes() {
      String[] clauses = {"p(a).", "p(1).", "p(1.0).", "p(x(a)).", "p([]).", "p([a,b])."};
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertLinkedHashMapPredicateFactory(pf);
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testSingleFactWithMultipleImmutableArgumentsPredicate() {
      PredicateFactory pf = getActualPredicateFactory("p(a,b,c).");
      assertSame(SingleNonRetryableRulePredicateFactory.class, pf.getClass());
      assertFalse(pf.isRetryable());
   }

   @Test
   public void testMultipleFactsWithMultipleImmutableArgumentsPredicate() {
      PredicateFactory pf = getActualPredicateFactory("p(a,b,c).", "p(1,2,3).", "p(x,y,z).");
      assertIndexablePredicateFactory(pf);
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testMultipleFactsWithNoArgumentsPredicate() {
      PredicateFactory pf = getActualPredicateFactory("p.", "p.", "p.");
      assertTrue(pf.isRetryable());
      Predicate p = pf.getPredicate(new Atom("p"));
      assertSame(InterpretedUserDefinedPredicate.class, p.getClass());
      assertTrue(p.evaluate());
      assertTrue(p.evaluate());
      assertTrue(p.evaluate());
      assertFalse(p.evaluate());
   }

   @Test
   public void testIndexablePredicate() {
      // has mutable arg so not treated as facts but some args are indexable
      Term[] clauses = toTerms("p(a,b,c).", "p(1,2,3).", "p(x,y,Z).");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertEquals("org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory", pf.getClass().getName());
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testNotIndexablePredicate() {
      // not args are indexable as none are always immutable
      PredicateFactory pf = getActualPredicateFactory("p(a,b,c).", "p(1,2,3).", "p(X,Y,Z).");
      assertEquals("org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory", pf.getClass().getName());
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testNeverSucceedsPredicateFactory() {
      PredicateFactory pf = getActualPredicateFactory("p(a,b,c).", "p(1,2,3).", "p(x,y,Z).");
      assertEquals("org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory", pf.getClass().getName());
      assertTrue(pf.isRetryable());
      Structure term = structure("p", atom("q"), atom("w"), atom("e"));
      PredicateFactory preprocessedPredicateFactory = ((PreprocessablePredicateFactory) pf).preprocess(term);
      assertSame(NeverSucceedsPredicateFactory.class, preprocessedPredicateFactory.getClass());
      assertSame(PredicateUtils.FALSE, preprocessedPredicateFactory.getPredicate(term));
      assertFalse(preprocessedPredicateFactory.isRetryable());
   }

   @Test
   public void testInterpretedTailRecursivePredicateFactory() {
      PredicateFactory pf = getActualPredicateFactory(toTerms(RECURSIVE_PREDICATE_SYNTAX));
      assertSame(InterpretedTailRecursivePredicateFactory.class, pf.getClass());
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testInterpretedUserDefinedPredicate() {
      PredicateFactory pf = getActualPredicateFactory(toTerms(NON_RECURSIVE_PREDICATE_SYNTAX));
      assertSame(InterpretedUserDefinedPredicate.class, pf.getPredicate(Structure.createStructure("p", createArgs(3))).getClass());
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testRetryableRule() {
      PredicateFactory pf = getActualPredicateFactory("x(X) :- var(X), !, repeat.");
      assertSingleRetryableRulePredicateFactory(pf);
   }

   @Test
   public void testConjunctionContainingVariables() {
      PredicateFactory pf = getActualPredicateFactory("and(X,Y) :- X, Y.");
      assertSingleRetryableRulePredicateFactory(pf);
      assertTrue(pf.isRetryable());
   }

   @Test
   public void testVariableAntecedent() {
      PredicateFactory pf = getActualPredicateFactory("true(X) :- X.");
      assertSingleRetryableRulePredicateFactory(pf);
   }

   @Test
   public void testAddFirst() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase(TestUtils.PROJOG_DEFAULT_PROPERTIES);
      Term t = TestUtils.parseSentence("test(X).");
      ClauseModel clauseModel = ClauseModel.createClauseModel(t);
      StaticUserDefinedPredicateFactory f = new StaticUserDefinedPredicateFactory(kb, PredicateKey.createForTerm(t));
      try {
         f.addFirst(clauseModel);
      } catch (ProjogException e) {
         assertEquals("Cannot add clause to already defined user defined predicate as it is not dynamic: test/1 clause: test(X)", e.getMessage());
      }
   }

   @Test
   public void testAddLast() {
      KnowledgeBase kb = TestUtils.createKnowledgeBase(TestUtils.PROJOG_DEFAULT_PROPERTIES);
      Term t = TestUtils.parseSentence("test(a).");
      StaticUserDefinedPredicateFactory f = new StaticUserDefinedPredicateFactory(kb, PredicateKey.createForTerm(t));

      // ok to add clause as predicate not yet compiled
      ClauseModel firstClause = ClauseModel.createClauseModel(t);
      f.addLast(firstClause);

      f.compile();

      // no longer ok to add clause as predicate has been compiled
      ClauseModel secondClause = ClauseModel.createClauseModel(TestUtils.parseSentence("test(z)."));
      try {
         f.addFirst(secondClause);
      } catch (ProjogException e) {
         assertEquals("Cannot add clause to already defined user defined predicate as it is not dynamic: test/1 clause: test(z)", e.getMessage());
      }
   }

   private static void assertSingleRetryableRulePredicateFactory(PredicateFactory p) {
      assertSame(SingleRetryableRulePredicateFactory.class, p.getClass());
   }

   private void assertIndexablePredicateFactory(PredicateFactory p) {
      assertEquals("org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory", p.getClass().getName());
   }

   private static void assertSingleIndexPredicateFactory(PredicateFactory p) {
      assertEquals("org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory", p.getClass().getName());
   }

   private static void assertLinkedHashMapPredicateFactory(PredicateFactory p) {
      assertEquals("org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory", p.getClass().getName());
   }

   private static Term[] createArgs(Term term) {
      return createArgs(term.getNumberOfArguments());
   }

   private static Term[] createArgs(int numArgs) {
      Term[] args = new Term[numArgs];
      Arrays.fill(args, atom());
      return args;
   }

   private static PredicateFactory getActualPredicateFactory(String... clauses) {
      return getActualPredicateFactory(toTerms(clauses));
   }

   private static PredicateFactory getActualPredicateFactory(Term[] clauses) {
      KnowledgeBase kb = TestUtils.createKnowledgeBase(TestUtils.PROJOG_DEFAULT_PROPERTIES);
      StaticUserDefinedPredicateFactory f = null;
      for (Term clause : clauses) {
         ClauseModel clauseModel = ClauseModel.createClauseModel(clause);
         if (f == null) {
            PredicateKey key = PredicateKey.createForTerm(clauseModel.getConsequent());
            f = new StaticUserDefinedPredicateFactory(kb, key);
         }
         f.addLast(clauseModel);
      }
      return f.getActualPredicateFactory();
   }

   private static Term[] toTerms(String... clausesSyntax) {
      Term[] clauses = new Term[clausesSyntax.length];
      for (int i = 0; i < clauses.length; i++) {
         clauses[i] = TestUtils.parseSentence(clausesSyntax[i]);
      }
      return clauses;
   }
}
