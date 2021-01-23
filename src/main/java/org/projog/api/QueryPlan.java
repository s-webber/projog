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

import java.util.List;
import java.util.Optional;

import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.parser.ParserException;
import org.projog.core.parser.SentenceParser;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.term.Term;

/**
 * Represents a plan for executing a Prolog query.
 * <p>
 * A single {@code QueryPlan} can be used to create multiple {@link QueryStatement} objects. If you are intending to
 * execute the same query multiple times then, for performance reasons, it is recommended to use a {@code QueryPlan}
 * rather than create multiple {@link QueryStatement} directly. When using a {@code QueryPlan} the Prolog syntax will
 * only be parsed once and the plan for executing the query will be optimised for performance.
 */
public class QueryPlan {
   private final PredicateFactory predicateFactory;
   private final Term parsedInput;

   QueryPlan(KnowledgeBase kb, String prologQuery) {
      try {
         SentenceParser sp = SentenceParser.getInstance(prologQuery, kb.getOperands());

         this.parsedInput = sp.parseSentence();
         this.predicateFactory = kb.getPredicates().getPreprocessedPredicateFactory(parsedInput);

         if (sp.parseSentence() != null) {
            throw new ProjogException("More input found after . in " + prologQuery);
         }
      } catch (ParserException pe) {
         throw pe;
      } catch (Exception e) {
         throw new ProjogException(e.getClass().getName() + " caught parsing: " + prologQuery, e);
      }
   }

   /**
    * Return a newly created {@link QueryStatement} for the query represented by this plan.
    * <p>
    * Before the query is executed, values can be assigned to variables in the query by using
    * {@link QueryStatement#setTerm(String, Term)}. The query can be executed by calling
    * {@link QueryStatement#executeQuery()}.
    * </p>
    * <p>
    * Note: If you do not intend to assign terms to variables then {@link #executeQuery()} can be called instead.
    * </p>
    *
    * @see #executeQuery()
    * @see #executeOnce()
    */
   public QueryStatement createStatement() {
      return new QueryStatement(predicateFactory, parsedInput);
   }

   /**
    * Return a newly created {@link QueryResult} for the query represented by this plan.
    * <p>
    * The {@link QueryResult#next()} and {@link QueryResult#getTerm(String)} methods can be used to evaluate the query
    * and access values unified to the variables of the query.
    *
    * @see #createStatement()
    * @see #executeOnce()
    */
   public QueryResult executeQuery() {
      return createStatement().executeQuery();
   }

   /**
    * Evaluate once the query represented by this statement.
    * <p>
    * The query will only be evaluated once, even if further solutions could of been found on backtracking.
    *
    * @throws ProjogException if no solution can be found
    * @see #createStatement()
    * @see #executeQuery()
    */
   public void executeOnce() {
      createStatement().executeOnce();
   }

   public String findFirstAsAtomName() {
      return createStatement().findFirstAsAtomName();
   }

   public double findFirstAsDouble() {
      return createStatement().findFirstAsDouble();
   }

   public long findFirstAsLong() {
      return createStatement().findFirstAsLong();
   }

   public Term findFirstAsTerm() {
      return createStatement().findFirstAsTerm();
   }

   public Optional<String> findFirstAsOptionalAtomName() {
      return createStatement().findFirstAsOptionalAtomName();
   }

   public Optional<Double> findFirstAsOptionalDouble() {
      return createStatement().findFirstAsOptionalDouble();
   }

   public Optional<Long> findFirstAsOptionalLong() {
      return createStatement().findFirstAsOptionalLong();
   }

   public Optional<Term> findFirstAsOptionalTerm() {
      return createStatement().findFirstAsOptionalTerm();
   }

   public List<String> findAllAsAtomName() {
      return createStatement().findAllAsAtomName();
   }

   public List<Double> findAllAsDouble() {
      return createStatement().findAllAsDouble();
   }

   public List<Long> findAllAsLong() {
      return createStatement().findAllAsLong();
   }

   public List<Term> findAllAsTerm() {
      return createStatement().findAllAsTerm();
   }
}
