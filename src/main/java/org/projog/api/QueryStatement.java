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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.ProjogException;
import org.projog.core.parser.ParserException;
import org.projog.core.parser.SentenceParser;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/**
 * Represents a query.
 * <p>
 * single use, not multi-threaded
 */
public final class QueryStatement {
   private static final Map<String, Variable> EMPTY_VARIABLES = Collections.emptyMap();

   private final PredicateFactory predicateFactory;
   private final Term parsedInput;
   private final Map<String, Variable> variables;

   /**
    * Creates a new {@code QueryStatement} representing a query specified by {@code prologQuery}.
    *
    * @param kb the {@link org.projog.core.KnowledgeBase} to query against
    * @param prologQuery prolog syntax representing a query (do not prefix with a {@code ?-})
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    * @see Projog#query(String)
    */
   QueryStatement(KnowledgeBase kb, String prologQuery) {
      try {
         SentenceParser sp = SentenceParser.getInstance(prologQuery, kb.getOperands());

         this.parsedInput = sp.parseSentence();
         this.predicateFactory = kb.getPredicates().getPredicateFactory(parsedInput);
         this.variables = sp.getParsedTermVariables();

         if (sp.parseSentence() != null) {
            throw new ProjogException("More input found after . in " + prologQuery);
         }
      } catch (ParserException pe) {
         throw pe;
      } catch (Exception e) {
         throw new ProjogException(e.getClass().getName() + " caught parsing: " + prologQuery, e);
      }
   }

   QueryStatement(PredicateFactory predicateFactory, Term prologQuery) {
      this.predicateFactory = predicateFactory;
      if (prologQuery.isImmutable()) {
         this.parsedInput = prologQuery;
         this.variables = EMPTY_VARIABLES;
      } else {
         Map<Variable, Variable> sharedVariables = new HashMap<>();
         this.parsedInput = prologQuery.copy(sharedVariables);
         this.variables = new HashMap<>(sharedVariables.size());
         for (Variable variable : sharedVariables.values()) {
            if (!variable.isAnonymous() && variables.put(variable.getId(), variable) != null) {
               throw new IllegalStateException("Duplicate variable id: " + variable.getId());
            }
         }
      }
   }

   /**
    * Attempts to unify the specified term to the variable with the specified id.
    * <p>
    * If the variable is already unified to a term then an attempt will be made to unify the specified term with the
    * term the variable is currently unified with.
    *
    * @param variableId the id of the variable
    * @param term the term to unify
    * @return {@code true} if the attempt to unify the specified term to the variable with the specified id was
    * successful
    * @throws ProjogException if no variable with the specified id exists in the query this object represents
    */
   public void setTerm(String variableId, Term term) {
      Variable v = variables.get(variableId);
      if (v == null) {
         throw new ProjogException("Do not know about variable named: " + variableId + " in query: " + parsedInput);
      }
      if (!v.unify(term)) {
         throw new ProjogException("Cannot unify: " + variableId + " with: " + term);
      }
   }

   public void setAtomName(String variableId, String atomName) {
      setTerm(variableId, new Atom(atomName));
   }

   public void setDouble(String variableId, double value) {
      setTerm(variableId, new DecimalFraction(value));
   }

   public void setLong(String variableId, long value) {
      setTerm(variableId, new IntegerNumber(value));
   }

   public void setListOfAtomNames(String variableId, String... atomNames) {
      Term[] terms = new Term[atomNames.length];
      for (int i = 0; i < atomNames.length; i++) {
         terms[i] = new Atom(atomNames[i]);
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfAtomNames(String variableId, List<String> atomNames) {
      Term[] terms = new Term[atomNames.size()];
      for (int i = 0; i < atomNames.size(); i++) {
         terms[i] = new Atom(atomNames.get(i));
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfDoubles(String variableId, double... doubles) {
      Term[] terms = new Term[doubles.length];
      for (int i = 0; i < doubles.length; i++) {
         terms[i] = new DecimalFraction(doubles[i]);
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfDoubles(String variableId, List<Double> doubles) {
      Term[] terms = new Term[doubles.size()];
      for (int i = 0; i < doubles.size(); i++) {
         terms[i] = new DecimalFraction(doubles.get(i));
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfLongs(String variableId, long... longs) {
      Term[] terms = new Term[longs.length];
      for (int i = 0; i < longs.length; i++) {
         terms[i] = new IntegerNumber(longs[i]);
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfLongs(String variableId, List<Long> longs) {
      Term[] terms = new Term[longs.size()];
      for (int i = 0; i < longs.size(); i++) {
         terms[i] = new IntegerNumber(longs.get(i));
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfTerms(String variableId, Term... terms) {
      setTerm(variableId, ListFactory.createList(terms));
   }

   public void setListOfTerms(String variableId, List<? extends Term> terms) {
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Returns a new {@link QueryResult} for the query represented by this object.
    * <p>
    * Note that the query is not evaluated as part of a call to {@code executeQuery()}. It is on the first call of
    * {@link QueryResult#next()} that the first attempt to evaluate the query will be made.
    *
    * @return a new {@link QueryResult} for the query represented by this object.
    */
   public QueryResult executeQuery() {
      // TODO throw exception if already called and not immutable
      return new QueryResult(predicateFactory, parsedInput, variables);
   }

   public String findFirstAsAtomName() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getAtomName(variableId);
      } else {
         throw new RuntimeException("No results returned");
      }
   }

   public double findFirstAsDouble() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getDouble(variableId);
      } else {
         throw new RuntimeException("No results returned");
      }
   }

   public long findFirstAsLong() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getLong(variableId);
      } else {
         throw new RuntimeException("No results returned");
      }
   }

   public Term findFirstAsTerm() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getTerm(variableId);
      } else {
         throw new RuntimeException("No results returned");
      }
   }

   public Optional<String> findFirstAsOptionalAtomName() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         String atomName = result.getAtomName(variableId);
         return Optional.of(atomName);
      } else {
         return Optional.empty();
      }
   }

   public Optional<Double> findFirstAsOptionalDouble() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         double value = result.getDouble(variableId);
         return Optional.of(value);
      } else {
         return Optional.empty();
      }
   }

   public Optional<Long> findFirstAsOptionalLong() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         long value = result.getLong(variableId);
         return Optional.of(value);
      } else {
         return Optional.empty();
      }
   }

   public Optional<Term> findFirstAsOptionalTerm() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         Term term = result.getTerm(variableId);
         return Optional.of(term);
      } else {
         return Optional.empty();
      }
   }

   public List<String> findAllAsAtomName() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<String> values = new ArrayList<>();
      while (result.next()) {
         values.add(result.getAtomName(variableId));
      }
      return values;
   }

   public List<Double> findAllAsDouble() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<Double> values = new ArrayList<>();
      while (result.next()) {
         values.add(result.getDouble(variableId));
      }
      return values;
   }

   public List<Long> findAllAsLong() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<Long> atomNames = new ArrayList<>();
      while (result.next()) {
         atomNames.add(result.getLong(variableId));
      }
      return atomNames;
   }

   public List<Term> findAllAsTerm() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<Term> terms = new ArrayList<>();
      while (result.next()) {
         terms.add(result.getTerm(variableId));
      }
      return terms;
   }

   private String getSingleVariableId() {
      String id = null;

      for (Map.Entry<String, Variable> e : variables.entrySet()) {
         if (e.getValue().getType().isVariable()) {
            if (id != null) {
               throw new IllegalStateException("Expected exactly one variable but found " + id + " and " + e.getKey());
            }
            id = e.getKey();
         }
      }

      if (id == null) {
         throw new IllegalStateException("Expected exactly one variable but found none in " + variables);
      }

      return id;
   }
}
