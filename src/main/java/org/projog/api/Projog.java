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
package org.projog.api;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.event.ProjogListener;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseUtils;
import org.projog.core.kb.ProjogDefaultProperties;
import org.projog.core.kb.ProjogProperties;
import org.projog.core.math.ArithmeticOperator;
import org.projog.core.parser.ProjogSourceReader;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.term.Term;

/**
 * Provides an entry point for other Java code to interact with Projog.
 * <p>
 * Contains a single instance of {@link org.projog.core.kb.KnowledgeBase}.
 * </p>
 * <img src="doc-files/Projog.png">
 */
public final class Projog {
   private final KnowledgeBase kb;

   /**
    * Constructs a new {@code Projog} object using {@link ProjogDefaultProperties} and the specified
    * {@code ProjogListener}s.
    */
   public Projog(ProjogListener... listeners) {
      this(new ProjogDefaultProperties(), listeners);
   }

   /**
    * Constructs a new {@code Projog} object with the specified {@code ProjogProperties} and {@code ProjogListener}s.
    */
   public Projog(ProjogProperties projogProperties, ProjogListener... listeners) {
      this.kb = KnowledgeBaseUtils.createKnowledgeBase(projogProperties);
      for (ProjogListener listener : listeners) {
         addListener(listener);
      }
      KnowledgeBaseUtils.bootstrap(kb);
   }

   /**
    * Populates this objects {@code KnowledgeBase} with clauses read from the specified file.
    *
    * @param prologScript source of the prolog syntax defining the clauses to add
    * @throws ProjogException if there is any problem parsing the syntax or adding the new clauses
    */
   public void consultFile(File prologScript) {
      ProjogSourceReader.parseFile(kb, prologScript);
   }

   /**
    * Populates this objects {@code KnowledgeBase} with clauses read from the specified {@code Reader}.
    *
    * @param reader source of the prolog syntax defining the clauses to add
    * @throws ProjogException if there is any problem parsing the syntax or adding the new clauses
    */
   public void consultReader(Reader reader) {
      ProjogSourceReader.parseReader(kb, reader);
   }

   /**
    * Populates this objects {@code KnowledgeBase} with clauses read from the specified resource.
    * <p>
    * If {@code prologSourceResourceName} refers to an existing file on the file system then that file is used as the
    * source of the prolog syntax else {@code prologSourceResourceName} is read from the classpath.
    *
    * @param resourceName source of the prolog syntax defining clauses to add to the KnowledgeBase
    * @throws ProjogException if there is any problem parsing the syntax or adding the new clauses to the KnowledgeBase
    */
   public void consultResource(String resourceName) {
      ProjogSourceReader.parseResource(kb, resourceName);
   }

   /**
    * Reassigns the "standard" input stream.
    * <p>
    * By default the "standard" input stream will be {@code System.in}.
    */
   public void setUserInput(InputStream is) {
      kb.getFileHandles().setUserInput(is);
   }

   /**
    * Reassigns the "standard" output stream.
    * <p>
    * By default the "standard" output stream will be {@code System.out}.
    */
   public void setUserOutput(PrintStream ps) {
      kb.getFileHandles().setUserOutput(ps);
   }

   /**
    * Associates a {@link PredicateFactory} with the {@code KnowledgeBase} of this {@code Projog}.
    * <p>
    * This method provides a mechanism for "plugging in" or "injecting" implementations of {@link PredicateFactory} at
    * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including
    * adding functionality not possible to define in pure Prolog syntax.
    * </p>
    *
    * @param key The name and arity to associate the {@link PredicateFactory} with.
    * @param predicateFactory The {@link PredicateFactory} to be added.
    * @throws ProjogException if there is already a {@link PredicateFactory} associated with the {@code PredicateKey}
    */
   public void addPredicateFactory(PredicateKey key, PredicateFactory predicateFactory) {
      kb.getPredicates().addPredicateFactory(key, predicateFactory);
   }

   /**
    * Associates a {@link ArithmeticOperator} with this {@code KnowledgeBase} of this {@code Projog}.
    * <p>
    * This method provides a mechanism for "plugging in" or "injecting" implementations of {@link ArithmeticOperator} at
    * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including
    * adding functionality not possible to define in pure Prolog syntax.
    * </p>
    *
    * @param key The name and arity to associate the {@link ArithmeticOperator} with.
    * @param operator The instance of {@code ArithmeticOperator} to be associated with {@code key}.
    * @throws ProjogException if there is already a {@link ArithmeticOperator} associated with the {@code PredicateKey}
    */
   public void addArithmeticOperator(PredicateKey key, ArithmeticOperator operator) {
      kb.getArithmeticOperators().addArithmeticOperator(key, operator);
   }

   /**
    * Creates a {@link QueryPlan} for querying the Projog environment.
    * <p>
    * The newly created object represents the query parsed from the specified syntax. A single {@link QueryPlan} can be
    * used to create multiple {@link QueryStatement} objects.
    *
    * @param prologQuery prolog syntax representing a query
    * @return representation of the query parsed from the specified syntax
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    * @see #createStatement(String)
    * @see #executeQuery(String)
    * @see #executeOnce(String)
    */
   public QueryPlan createPlan(String prologQuery) {
      return new QueryPlan(kb, prologQuery);
   }

   /**
    * Creates a {@link QueryStatement} for querying the Projog environment.
    * <p>
    * The newly created object represents the query parsed from the specified syntax. Before the query is executed,
    * values can be assigned to variables in the query by using {@link QueryStatement#setTerm(String, Term)}. The query
    * can be executed by calling {@link QueryStatement#executeQuery()}. The resulting {@link QueryResult} can be used to
    * access the result.
    * <p>
    * Note: If you do not intend to assign terms to variables then {@link #executeQuery(String)} can be called instead.
    * </p>
    *
    * @param prologQuery prolog syntax representing a query
    * @return representation of the query parsed from the specified syntax
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    * @see #createPlan(String)
    * @see #executeQuery(String)
    * @see #executeOnce(String)
    */
   public QueryStatement createStatement(String prologQuery) {
      return new QueryStatement(kb, prologQuery);
   }

   /**
    * Creates a {@link QueryResult} for querying the Projog environment.
    * <p>
    * The newly created object represents the query parsed from the specified syntax. The {@link QueryResult#next()} and
    * {@link QueryResult#getTerm(String)} methods can be used to evaluate the query and access values unified to the
    * variables of the query.
    * </p>
    *
    * @param prologQuery prolog syntax representing a query
    * @return representation of the query parsed from the specified syntax
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    * @see #createPlan(String)
    * @see #createStatement(String)
    * @see #executeOnce(String)
    */
   public QueryResult executeQuery(String prologQuery) {
      return createStatement(prologQuery).executeQuery();
   }

   /**
    * Evaluate once the given query.
    * <p>
    * The query will only be evaluated once, even if further solutions could of been found on backtracking.
    *
    * @param prologQuery prolog syntax representing a query
    * @throws ProjogException if an error occurs parsing {@code prologQuery} or no solution can be found for it
    * @see #createPlan(String)
    * @see #createStatement(String)
    * @see #executeQuery(String)
    */
   public void executeOnce(String prologQuery) {
      createStatement(prologQuery).executeOnce();
   }

   /**
    * Registers an {@code ProjogListener} to receive notifications of events generated during the evaluation of Prolog
    * goals.
    *
    * @param listener an listener to be added
    */
   public void addListener(ProjogListener listener) {
      kb.getProjogListeners().addListener(listener);
   }

   /**
    * Returns a string representation of the specified {@code Term}.
    *
    * @param t the {@code Term} to represent as a string
    * @return a string representation of the specified {@code Term}
    * @see org.projog.core.term.TermFormatter#formatTerm(Term)
    */
   public String formatTerm(Term t) {
      return kb.getTermFormatter().formatTerm(t);
   }

   /**
    * Returns the {@link KnowledgeBase} associated with this object.
    * <p>
    * Each {@code Projog} object is associated with its own {@link KnowledgeBase}. In normal usage it should not be
    * necessary to call this method - as the other methods of {@code Projog} provide a more convenient mechanism for
    * updating and querying the "core" inference engine.
    *
    * @return the {@link KnowledgeBase} associated with this object.
    * @see org.projog.core.kb.KnowledgeBaseUtils
    */
   public KnowledgeBase getKnowledgeBase() {
      return kb;
   }

   /**
    * Prints the all clauses contained in the specified throwable's stack trace to the standard error stream.
    */
   public void printProjogStackTrace(Throwable exception) {
      printProjogStackTrace(exception, System.err);
   }

   /**
    * Prints the all clauses contained in the specified throwable's stack trace to the specified print stream.
    */
   public void printProjogStackTrace(Throwable exception, PrintStream out) {
      ProjogStackTraceElement[] stackTrace = getStackTrace(exception);
      for (ProjogStackTraceElement e : stackTrace) {
         out.println(e.getPredicateKey() + " clause: " + formatTerm(e.getTerm()));
      }
   }

   /**
    * Provides programmatic access to the stack trace information printed by {@link #printProjogStackTrace(Throwable)}.
    */
   public ProjogStackTraceElement[] getStackTrace(Throwable exception) {
      List<ProjogStackTraceElement> result = new ArrayList<>();
      List<ClauseModel> clauses = getClauses(exception);
      for (ClauseModel clause : clauses) {
         result.add(new ProjogStackTraceElement(clause.getPredicateKey(), clause.getOriginal()));
      }
      return result.toArray(new ProjogStackTraceElement[result.size()]);
   }

   private List<ClauseModel> getClauses(Throwable e) {
      if (e instanceof ProjogException) {
         ProjogException pe = (ProjogException) e;
         return pe.getClauses();
      } else {
         return new ArrayList<>();
      }
   }
}
