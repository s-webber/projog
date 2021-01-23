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
package org.projog.api;

/**
 * Tests various methods of both {@link QueryStatement} and {@link QueryResult} against the same Prolog query.
 */
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.projog.core.ProjogException;
import org.projog.core.term.Term;

abstract class AbstractQueryTest {
   private int nextMethodId = 1;
   private final Projog projog;
   private final String query;
   private final StatementMethod<Term> findFirstAsTerm = new StatementMethod<>(QueryStatement::findFirstAsTerm, QueryPlan::findFirstAsTerm);
   private final StatementMethod<Optional<Term>> findFirstAsOptionalTerm = new StatementMethod<>(QueryStatement::findFirstAsOptionalTerm, QueryPlan::findFirstAsOptionalTerm);
   private final StatementMethod<List<Term>> findAllAsTerm = new StatementMethod<>(QueryStatement::findAllAsTerm, QueryPlan::findAllAsTerm);
   private final StatementMethod<String> findFirstAsAtomName = new StatementMethod<>(QueryStatement::findFirstAsAtomName, QueryPlan::findFirstAsAtomName);
   private final StatementMethod<Optional<String>> findFirstAsOptionalAtomName = new StatementMethod<>(QueryStatement::findFirstAsOptionalAtomName,
               QueryPlan::findFirstAsOptionalAtomName);
   private final StatementMethod<List<String>> findAllAsAtomName = new StatementMethod<>(QueryStatement::findAllAsAtomName, QueryPlan::findAllAsAtomName);
   private final StatementMethod<Double> findFirstAsDouble = new StatementMethod<>(QueryStatement::findFirstAsDouble, QueryPlan::findFirstAsDouble);
   private final StatementMethod<Optional<Double>> findFirstAsOptionalDouble = new StatementMethod<>(QueryStatement::findFirstAsOptionalDouble,
               QueryPlan::findFirstAsOptionalDouble);
   private final StatementMethod<List<Double>> findAllAsDouble = new StatementMethod<>(QueryStatement::findAllAsDouble, QueryPlan::findAllAsDouble);
   private final StatementMethod<Long> findFirstAsLong = new StatementMethod<>(QueryStatement::findFirstAsLong, QueryPlan::findFirstAsLong);
   private final StatementMethod<Optional<Long>> findFirstAsOptionalLong = new StatementMethod<>(QueryStatement::findFirstAsOptionalLong, QueryPlan::findFirstAsOptionalLong);
   private final StatementMethod<List<Long>> findAllAsLong = new StatementMethod<>(QueryStatement::findAllAsLong, QueryPlan::findAllAsLong);

   private static int METHOD_INVOCATIONS_CTR;

   public AbstractQueryTest(String query) {
      this.projog = new Projog();
      this.query = query;
   }

   public AbstractQueryTest(String query, String clauses) {
      this(query);
      projog.consultReader(new StringReader(clauses));
   }

   @BeforeClass
   public static void beforeClass() {
      METHOD_INVOCATIONS_CTR = 0;
   }

   @AfterClass
   public static void afterClass() {
      Assert.assertEquals("not all methods have been asserted", 0b111111111111, METHOD_INVOCATIONS_CTR);
   }

   @Test
   public abstract void testFindFirstAsTerm();

   protected StatementMethod<Term> findFirstAsTerm() {
      return findFirstAsTerm;
   }

   @Test
   public abstract void testFindFirstAsOptionalTerm();

   protected StatementMethod<Optional<Term>> findFirstAsOptionalTerm() {
      return findFirstAsOptionalTerm;
   }

   @Test
   public abstract void testFindAllAsTerm();

   protected StatementMethod<List<Term>> findAllAsTerm() {
      return findAllAsTerm;
   }

   @Test
   public abstract void testFindFirstAsAtomName();

   protected StatementMethod<String> findFirstAsAtomName() {
      return findFirstAsAtomName;
   }

   @Test
   public abstract void testFindFirstAsOptionalAtomName();

   protected StatementMethod<Optional<String>> findFirstAsOptionalAtomName() {
      return findFirstAsOptionalAtomName;
   }

   @Test
   public abstract void testFindAllAsAtomName();

   protected StatementMethod<List<String>> findAllAsAtomName() {
      return findAllAsAtomName;
   }

   @Test
   public abstract void testFindFirstAsDouble();

   protected StatementMethod<Double> findFirstAsDouble() {
      return findFirstAsDouble;
   }

   @Test
   public abstract void testFindFirstAsOptionalDouble();

   protected StatementMethod<Optional<Double>> findFirstAsOptionalDouble() {
      return findFirstAsOptionalDouble;
   }

   @Test
   public abstract void testFindAllAsDouble();

   protected StatementMethod<List<Double>> findAllAsDouble() {
      return findAllAsDouble;
   }

   @Test
   public abstract void testFindFirstAsLong();

   protected StatementMethod<Long> findFirstAsLong() {
      return findFirstAsLong;
   }

   @Test
   public abstract void testFindFirstAsOptionalLong();

   protected StatementMethod<Optional<Long>> findFirstAsOptionalLong() {
      return findFirstAsOptionalLong;
   }

   @Test
   public abstract void testFindAllAsLong();

   protected StatementMethod<List<Long>> findAllAsLong() {
      return findAllAsLong;
   }

   class StatementMethod<T> {
      final Function<QueryStatement, ?> statementMethod;
      final Function<QueryPlan, ?> planMethod;
      final int id;

      private StatementMethod(Function<QueryStatement, T> statementMethod, Function<QueryPlan, T> planMethod) {
         this.statementMethod = statementMethod;
         this.planMethod = planMethod;
         this.id = nextMethodId;
         nextMethodId *= 2;
      }

      @SuppressWarnings("unchecked")
      T get() {
         return (T) statementMethod.apply(createStatement());
      }

      void assertEquals(T expected) {
         QueryStatement s = createStatement();
         Assert.assertEquals(expected, statementMethod.apply(s));

         // run twice to confirm QueryPlan is reusable
         QueryPlan p = projog.createPlan(query);
         Assert.assertEquals(expected, planMethod.apply(p));
         Assert.assertEquals(expected, planMethod.apply(p));
      }

      void assertException(String expectedMessage) {
         QueryStatement s = createStatement();
         try {
            statementMethod.apply(s);
            fail();
         } catch (ProjogException e) {
            Assert.assertEquals(expectedMessage, e.getMessage());
         }

         QueryPlan p = projog.createPlan(query);
         try {
            planMethod.apply(p);
            fail();
         } catch (ProjogException e) {
            Assert.assertEquals(expectedMessage, e.getMessage());
         }
      }

      private QueryStatement createStatement() {
         QueryStatement s = projog.createStatement(query);
         if ((METHOD_INVOCATIONS_CTR & id) != 0) {
            throw new IllegalStateException(id + " has been invoked twice by the same test");
         }
         METHOD_INVOCATIONS_CTR += id;
         return s;
      }
   }
}
