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
package org.projog.api;

import static org.projog.core.KnowledgeBaseUtils.getArithmeticOperators;
import static org.projog.core.KnowledgeBaseUtils.getFileHandles;
import static org.projog.core.KnowledgeBaseUtils.getOperands;
import static org.projog.core.KnowledgeBaseUtils.getProjogEventsObservable;
import static org.projog.core.udp.compiler.CompiledPredicateConstants.INIT_RULE_METHOD_NAME_PREFIX;
import static org.projog.core.udp.compiler.CompiledPredicateConstants.RETRY_RULE_METHOD_NAME_PREFIX;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import org.projog.core.ArithmeticOperator;
import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.ProjogProperties;
import org.projog.core.ProjogSourceReader;
import org.projog.core.ProjogSystemProperties;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.udp.UserDefinedPredicateFactory;
import org.projog.core.udp.compiler.CompiledPredicateConstants;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * Provides an entry point for other Java code to interact with Projog.
 * <p>
 * Contains a single instance of {@link org.projog.core.KnowledgeBase}.
 * </p>
 * <h3>Example usage</h3>
 * <p>
 * Contents of {@code ProjogExample.java}:
 * </p>
 * <pre>
 * package com.example;
 *
 * import java.io.File;
 *
 * import org.projog.api.Projog;
 * import org.projog.api.QueryResult;
 * import org.projog.api.QueryStatement;
 * import org.projog.core.term.Atom;
 *
 * public class ProjogExample {
 *    public static void main(String[] args) {
 *       // Create a new Projog instance
 *       Projog p = new Projog();
 *
 *       // Read Prolog facts and rules from a file to populate the "Projog" instance created in step 1.
 *       p.consultFile(new File("src/main/resources/test.pl"));
 *
 *       // Create a query that will use the facts read in step 2.
 *       QueryStatement s1 = p.query("test(X,Y).");
 *
 *       // Execute the query created in step 3.
 *       QueryResult r1 = s1.getResult();
 *       while (r1.next()) {
 *          System.out.println("X = " + r1.getTerm("X") + " Y = " + r1.getTerm("Y"));
 *       }
 *
 *       // Execute the query created in step 3, after specifying a term for one of the variables contained in the query.
 *       QueryResult r2 = s1.getResult();
 *       r2.setTerm("X", new Atom("d"));
 *       while (r2.next()) {
 *          System.out.println("Y = " + r2.getTerm("Y"));
 *       }
 *
 *       // Create and execute a new query that will use the rule read in step 2.
 *       QueryStatement s2 = p.query("testRule(X).");
 *       QueryResult r3 = s2.getResult();
 *       while (r3.next()) {
 *          System.out.println("X = " + r3.getTerm("X"));
 *       }
 *
 *       // Create and execute a new query that uses a conjunction. See: http://www.projog.org/Conjunction.html
 *       QueryStatement s3 = p.query("test(X, Y), Y<3.");
 *       QueryResult r4 = s3.getResult();
 *       while (r4.next()) {
 *          System.out.println("X = " + r4.getTerm("X") + " Y = " + r4.getTerm("Y"));
 *       }
 *    }
 * }
 * </pre>
 * <p>
 * Contents of {@code test.pl}:
 * </p>
 * <pre>
 * test(a,1).
 * test(b,2).
 * test(c,3).
 * test(d,4).
 * test(e,5).
 * test(f,6).
 * test(g,7).
 * test(h,8).
 * test(i,9).
 *
 * testRule(X) :- test(X, Y), Y mod 2 =:= 0.
 * </pre>
 * <p>
 * Output of running {@code ProjogExample}:
 * </p>
 * <pre>
 * X = a Y = 1
 * X = b Y = 2
 * X = c Y = 3
 * X = d Y = 4
 * X = e Y = 5
 * X = f Y = 6
 * X = g Y = 7
 * X = h Y = 8
 * X = i Y = 9
 * Y = 4
 * X = b
 * X = d
 * X = f
 * X = h
 * X = a Y = 1
 * X = b Y = 2
 * </pre> <img src="doc-files/Projog.png">
 */
public final class Projog {
   private final KnowledgeBase kb;
   private final TermFormatter tf;

   /**
    * Constructs a new {@code Projog} object using {@link ProjogSystemProperties} and the specified {@code Observer}s.
    */
   public Projog(Observer... observers) {
      this(new ProjogSystemProperties(), observers);
   }

   /**
    * Constructs a new {@code Projog} object with the specified {@code ProjogProperties} and {@code Observer}s.
    */
   public Projog(ProjogProperties projogProperties, Observer... observers) {
      this.kb = KnowledgeBaseUtils.createKnowledgeBase(projogProperties);
      for (Observer o : observers) {
         addObserver(o);
      }
      KnowledgeBaseUtils.bootstrap(kb);
      this.tf = new TermFormatter(getOperands(kb));
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
   void setUserInput(InputStream is) {
      getFileHandles(kb).setUserInput(is);
   }

   /**
    * Reassigns the "standard" output stream.
    * <p>
    * By default the "standard" output stream will be {@code System.out}.
    */
   public void setUserOutput(PrintStream ps) {
      getFileHandles(kb).setUserOutput(ps);
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
      kb.addPredicateFactory(key, predicateFactory);
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
      getArithmeticOperators(kb).addArithmeticOperator(key, operator);
   }

   public QueryPlan createPlan(String prologQuery) {
      return new QueryPlan(kb, prologQuery);
   }

   /**
    * Creates a {@link QueryStatement} for querying the Projog environment.
    * <p>
    * The newly created object represents the query parsed from the specified syntax.
    *
    * @param prologQuery prolog syntax representing a query
    * @return representation of the query parsed from the specified syntax
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    */
   public QueryStatement createStatement(String prologQuery) {
      return new QueryStatement(kb, prologQuery);
   }

   /**
    * Registers an {@code Observer} to receive {@link org.projog.core.event.ProjogEvent}s generated during the
    * evaluation of Prolog goals.
    *
    * @param observer an observer to be added
    */
   public void addObserver(Observer observer) {
      getProjogEventsObservable(kb).addObserver(observer);
   }

   /**
    * Returns a string representation of the specified {@code Term}.
    *
    * @param t the {@code Term} to represent as a string
    * @return a string representation of the specified {@code Term}
    * @see org.projog.core.term.TermFormatter#toString(Term)
    */
   public String toString(Term t) {
      return tf.toString(t);
   }

   /**
    * Returns the {@link KnowledgeBase} associated with this object.
    * <p>
    * Each {@code Projog} object is associated with its own {@link KnowledgeBase}. In normal usage it should not be
    * necessary to call this method - as the other methods of {@code Projog} provide a more convenient mechanism for
    * updating and querying the "core" inference engine.
    *
    * @return the {@link KnowledgeBase} associated with this object.
    * @see org.projog.core.KnowledgeBaseUtils
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
         out.println(e.getPredicateKey() + " clause: " + toString(e.getTerm()));
      }
   }

   /**
    * Provides programmatic access to the stack trace information printed by {@link #printProjogStackTrace(Throwable)}.
    */
   public ProjogStackTraceElement[] getStackTrace(Throwable exception) {
      List<ProjogStackTraceElement> result = new ArrayList<>();
      StackTraceElement[] elements = exception.getStackTrace();
      List<ClauseModel> clauses = getClauses(exception);
      for (StackTraceElement element : elements) {
         ProjogStackTraceElement pste = createProjogStackTraceElement(element, clauses);
         if (pste != null) {
            result.add(pste);
         }
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

   private ProjogStackTraceElement createProjogStackTraceElement(StackTraceElement element, List<ClauseModel> clauses) {
      String className = element.getClassName();
      String methodName = element.getMethodName();
      if (isInterpretedMethod(className, methodName)) {
         ClauseModel m = clauses.remove(0);
         return createProjogStackTraceElementFromClauseModel(m);
      } else if (isCompiledPredicate(className) && isRuleEvaluationMethod(methodName)) {
         return createProjogStackTraceElementFromCompiledPredicate(className, methodName);
      } else {
         return null;
      }
   }

   private boolean isInterpretedMethod(String className, String methodName) {
      if (InterpretedUserDefinedPredicate.class.getName().equals(className) && "evaluate".equals(methodName)) {
         return true;
      } else if ("org.projog.core.udp.SingleRetryableRulePredicateFactory$RetryableRulePredicate".equals(className) && "evaluate".equals(methodName)) {
         return true;
      } else if ("org.projog.core.udp.SingleNonRetryableRulePredicate".equals(className) && "evaluateClause".equals(methodName)) {
         return true;
      } else {
         return false;
      }
   }

   private ProjogStackTraceElement createProjogStackTraceElementFromClauseModel(ClauseModel m) {
      return new ProjogStackTraceElement(m.getPredicateKey(), m.getOriginal());
   }

   private boolean isCompiledPredicate(String className) {
      return className.startsWith(CompiledPredicateConstants.COMPILED_PREDICATES_PACKAGE);
   }

   private boolean isRuleEvaluationMethod(String methodName) {
      return methodName.startsWith(INIT_RULE_METHOD_NAME_PREFIX) || methodName.startsWith(RETRY_RULE_METHOD_NAME_PREFIX);
   }

   private ProjogStackTraceElement createProjogStackTraceElementFromCompiledPredicate(String className, String methodName) {
      int ruleIdx = getRuleIndex(methodName);
      StaticUserDefinedPredicateFactory compiledPredicate = getCompiledPredicateForClass(className);
      ClauseModel clauseModel = compiledPredicate.getClauseModel(ruleIdx);
      PredicateKey key = compiledPredicate.getPredicateKey();
      Term term = clauseModel.getOriginal();
      return new ProjogStackTraceElement(key, term);
   }

   private int getRuleIndex(String methodName) {
      int beginIndex;
      if (methodName.startsWith(INIT_RULE_METHOD_NAME_PREFIX)) {
         beginIndex = INIT_RULE_METHOD_NAME_PREFIX.length();
      } else {
         beginIndex = RETRY_RULE_METHOD_NAME_PREFIX.length();
      }
      String suffix = methodName.substring(beginIndex);
      return Integer.parseInt(suffix);
   }

   private StaticUserDefinedPredicateFactory getCompiledPredicateForClass(String className) {
      for (Map.Entry<PredicateKey, UserDefinedPredicateFactory> e : kb.getUserDefinedPredicates().entrySet()) {
         UserDefinedPredicateFactory udp = e.getValue();
         if (isCompiledPredicateForClass(className, udp)) {
            return (StaticUserDefinedPredicateFactory) udp;
         }
      }
      return null;
   }

   private boolean isCompiledPredicateForClass(String className, UserDefinedPredicateFactory udp) {
      if (udp instanceof StaticUserDefinedPredicateFactory) {
         StaticUserDefinedPredicateFactory compiledPredicate = (StaticUserDefinedPredicateFactory) udp;
         PredicateFactory ef = compiledPredicate.getActualPredicateFactory();
         return className.equals(ef.getClass().getName());
      }
      return false;
   }
}
