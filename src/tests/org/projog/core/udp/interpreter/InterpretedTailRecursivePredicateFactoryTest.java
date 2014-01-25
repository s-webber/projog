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
package org.projog.core.udp.interpreter;

import static org.projog.TestUtils.createClauseModel;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseTerm;
import static org.projog.TestUtils.write;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.projog.core.KnowledgeBase;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.TailRecursivePredicateMetaData;

public class InterpretedTailRecursivePredicateFactoryTest extends TestCase {
   private final InterpretedTailRecursivePredicateFactory FACTORY = createFactory("prefix([],Ys).", "prefix([X|Xs],[X|Ys]) :- prefix(Xs,Ys).");

   public void testSingleResultQuery() {
      Term arg1 = parseTerm("[a]");
      Term arg2 = parseTerm("[a,b,c]");
      InterpretedTailRecursivePredicate singleResultPredicate = FACTORY.getPredicate(arg1, arg2);

      assertFalse(singleResultPredicate.isRetryable());
      assertFalse(singleResultPredicate.couldReEvaluationSucceed());
      assertTrue(singleResultPredicate.evaluate(arg1, arg2));
   }

   public void testMultiResultQuery() {
      Term arg1 = parseTerm("X");
      Term arg2 = parseTerm("[a,b,c]");
      InterpretedTailRecursivePredicate multiResultPredicate = FACTORY.getPredicate(arg1, arg2);

      assertTrue(multiResultPredicate.isRetryable());
      assertTrue(multiResultPredicate.couldReEvaluationSucceed());
      assertTrue(multiResultPredicate.evaluate(arg1, arg2));
      assertEquals("[]", write(arg1));
      assertTrue(multiResultPredicate.evaluate(arg1, arg2));
      assertEquals("[a]", write(arg1));
      assertTrue(multiResultPredicate.evaluate(arg1, arg2));
      assertEquals("[a,b]", write(arg1));
      assertTrue(multiResultPredicate.evaluate(arg1, arg2));
      assertEquals("[a,b,c]", write(arg1));
      assertFalse(multiResultPredicate.evaluate(arg1, arg2));
   }

   private InterpretedTailRecursivePredicateFactory createFactory(String firstClauseSyntax, String secondClauseSyntax) {
      KnowledgeBase kb = createKnowledgeBase();
      List<ClauseModel> clauses = createClauseModels(firstClauseSyntax, secondClauseSyntax);
      TailRecursivePredicateMetaData metaData = TailRecursivePredicateMetaData.create(kb, clauses);
      return new InterpretedTailRecursivePredicateFactory(kb, metaData);
   }

   private List<ClauseModel> createClauseModels(String firstClauseSyntax, String secondClauseSyntax) {
      List<ClauseModel> clauses = new ArrayList<>();
      clauses.add(createClauseModel(firstClauseSyntax));
      clauses.add(createClauseModel(secondClauseSyntax));
      return clauses;
   }
}