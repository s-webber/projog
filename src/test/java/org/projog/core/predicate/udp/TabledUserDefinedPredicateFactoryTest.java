/*
 * Copyright 2025 S. Webber
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
import static org.junit.Assert.fail;
import static org.projog.TestUtils.createClauseModel;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseSentence;
import static org.projog.TestUtils.write;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class TabledUserDefinedPredicateFactoryTest {
   @Test
   public void testGetters() {
      KnowledgeBase kb = createKnowledgeBase();
      PredicateKey key = new PredicateKey("test", 1);
      TabledUserDefinedPredicateFactory f = new TabledUserDefinedPredicateFactory(kb, key);
      kb.getPredicates().addUserDefinedPredicate(f);
      assertSame(f, kb.getPredicates().createOrReturnUserDefinedPredicate(key));
      assertSame(key, f.getPredicateKey());
      assertTrue(f.isRetryable());
      assertTrue(f.isDynamic());
      try {
         f.addFirst(createClauseModel("test(X)."));
         fail();
      } catch (ProjogException p) {
         assertEquals("Cannot add clause to already defined user defined predicate as it is not dynamic: test/1 clause: test(X)", p.getMessage());
      }

      f.addLast(createClauseModel("test(X) :- write(1)."));
      f.addLast(createClauseModel("test(X) :- write(2)."));
      f.addLast(createClauseModel("test(X) :- write(3)."));

      ImplicationsIterator implications = f.getImplications();
      ClauseModel next = implications.next();
      assertEquals("test(X) :- write(1)", write(next.getOriginal()));
      next = implications.next();
      assertEquals("test(X) :- write(2)", write(next.getOriginal()));
      next = implications.next();
      assertEquals("test(X) :- write(3)", write(next.getOriginal()));
      assertFalse(implications.hasNext());
   }

   @Test
   public void testMultipleThreads() throws Exception {
      KnowledgeBase kb = createKnowledgeBase();
      PredicateKey key = new PredicateKey("fib", 2);
      TabledUserDefinedPredicateFactory f = new TabledUserDefinedPredicateFactory(kb, key);
      kb.getPredicates().addUserDefinedPredicate(f);

      // add "sleep/1" build-in predicate to ensure multiple threads try to evaluate the same query at the same time
      AtomicInteger counter = new AtomicInteger();
      kb.getPredicates().addPredicateFactory(new PredicateKey("sleep", 0), new PredicateFactory() {
         @Override
         public boolean isRetryable() {
            return false;
         }

         @Override
         public Predicate getPredicate(Term term) {
            counter.incrementAndGet();
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return PredicateUtils.TRUE;
         }
      });

      // add rules for fibonacci sequence, with "sleep" invoked for "fib(1, 1)"
      f.addLast(createClauseModel("fib(0, 1) :- !."));
      f.addLast(createClauseModel("fib(1, 1) :- sleep, !."));
      f.addLast(createClauseModel("fib(N, F) :- N > 1,  N1 is N-1,  N2 is N-2, fib(N1, F1), fib(N2, F2), F is F1+F2."));

      // have 10 queries evaluated in parallel
      List<Callable<Term>> tasks = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
         final Term input = parseSentence("fib(91, X).");
         Predicate p = f.getPredicate(input);
         tasks.add(new Callable<Term>() {
            @Override
            public Term call() throws Exception {
               if (!p.evaluate() || p.couldReevaluationSucceed()) {
                  throw new IllegalStateException();
               }
               return input;
            }
         });
      }
      ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
      List<Future<Term>> results = executor.invokeAll(tasks);
      executor.shutdown();

      // confirm each query had the correct result
      IntegerNumber expected = new IntegerNumber(7540113804746346429L);
      for (Future<Term> future : results) {
         Term result = future.get();
         assertSame(Variable.class, result.secondArgument().getClass());
         assertEquals(expected, result.secondArgument().getTerm());
         assertEquals(StructureFactory.createStructure("fib", new IntegerNumber(91), expected), result.getTerm());
      }

      // although 10 queries evaluated, confirm "sleep" - and therefore "fib(1, 1") - was only evaluated once
      assertEquals(1, counter.get());
   }

   @Test
   public void testVariablesNotSharedBetweenQueries() {
      KnowledgeBase kb = createKnowledgeBase();
      PredicateKey key = new PredicateKey("test", 2);
      TabledUserDefinedPredicateFactory f = new TabledUserDefinedPredicateFactory(kb, key);
      kb.getPredicates().addUserDefinedPredicate(f);

      f.addLast(createClauseModel("test(X, X)."));

      Term input = parseSentence("test(9, A).");
      Predicate p = f.getPredicate(input);
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertEquals("test(9, 9)", input.toString());

      input = parseSentence("test(A, B).");
      p = f.getPredicate(input);
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertEquals("test(X, X)", input.toString());
      input.firstArgument().unify(new IntegerNumber(42));
      assertEquals("test(42, 42)", input.toString());

      input = parseSentence("test(A, B).");
      p = f.getPredicate(input);
      assertTrue(p.evaluate());
      assertFalse(p.couldReevaluationSucceed());
      assertEquals("test(X, X)", input.toString());
      input.firstArgument().unify(new IntegerNumber(180));
      assertEquals("test(180, 180)", input.toString());
   }
}
