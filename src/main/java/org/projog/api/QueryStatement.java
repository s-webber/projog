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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.parser.ParserException;
import org.projog.core.parser.SentenceParser;
import org.projog.core.predicate.PredicateFactory;
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
 *
 * @see Projog#createStatement(String)
 * @see Projog#createPlan(String)
 */
public final class QueryStatement {
   private static final Map<String, Variable> EMPTY_VARIABLES = Collections.emptyMap();

   private final PredicateFactory predicateFactory;
   private final Term parsedInput;
   private final Map<String, Variable> variables;
   private boolean invoked;

   /**
    * Creates a new {@code QueryStatement} representing a query specified by {@code prologQuery}.
    *
    * @param kb the {@link KnowledgeBase} to query against
    * @param prologQuery prolog syntax representing a query (do not prefix with a {@code ?-})
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
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

   /**
    * Creates a new {@code QueryStatement} representing a query specified by {@code prologQuery}.
    *
    * @param PredicateFactory the {@link PredicateFactory} that will be used to execute the query
    * @param prologQuery prolog syntax representing a query (do not prefix with a {@code ?-})
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    */
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
    *
    * @param variableId the id of the variable
    * @param term the term to unify to the variable
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setAtomName(String, String)
    * @see #setDouble(String, double)
    * @see #setLong(String, long)
    * @see #setListOfAtomNames(String, List)
    * @see #setListOfAtomNames(String, String...)
    * @see #setListOfDoubles(String, List)
    * @see #setListOfDoubles(String, double...)
    * @see #setListOfLongs(String, List)
    * @see #setListOfLongs(String, long...)
    * @see #setListOfTerms(String, List)
    * @see #setListOfTerms(String, Term...)
    */
   public void setTerm(String variableId, Term term) {
      Variable v = variables.get(variableId);
      if (v == null) {
         throw new ProjogException("Do not know about variable named: " + variableId + " in query: " + parsedInput);
      }
      if (!v.getType().isVariable()) {
         throw new ProjogException("Cannot set: " + variableId + " to: " + term + " as has already been set to: " + v);
      }
      boolean unified = v.unify(term);
      if (!unified) {
         // should never get here, just checking result of unify(Term) as a sanity check
         throw new IllegalStateException();
      }
   }

   /**
    * Attempts to unify the specified {@code String} value as an {@link Atom} to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param atomName the value to use as the name of the {@code Atom} that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setAtomName(String variableId, String atomName) {
      setTerm(variableId, new Atom(atomName));
   }

   /**
    * Attempts to unify the specified {@code double} as a {@link DecimalFraction} to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param value the value to use as the name of the {@code DecimalFraction} that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setDouble(String variableId, double value) {
      setTerm(variableId, new DecimalFraction(value));
   }

   /**
    * Attempts to unify the specified {@code long} as a {@link IntegerNumber} to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param value the value to use as the name of the {@code IntegerNumber} that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setLong(String variableId, long value) {
      setTerm(variableId, new IntegerNumber(value));
   }

   /**
    * Attempts to unify the specified {@code String} values as a Prolog list of atoms to the variable with the specified
    * id.
    *
    * @param variableId the id of the variable
    * @param atomNames the values to use as atom elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfAtomNames(String variableId, String... atomNames) {
      Term[] terms = new Term[atomNames.length];
      for (int i = 0; i < atomNames.length; i++) {
         terms[i] = new Atom(atomNames[i]);
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code String} values as a Prolog list of atoms to the variable with the specified
    * id.
    *
    * @param variableId the id of the variable
    * @param atomNames the values to use as atom elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfAtomNames(String variableId, List<String> atomNames) {
      Term[] terms = new Term[atomNames.size()];
      for (int i = 0; i < atomNames.size(); i++) {
         terms[i] = new Atom(atomNames.get(i));
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code double} values as a Prolog list to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param doubles the values to use as elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfDoubles(String variableId, double... doubles) {
      Term[] terms = new Term[doubles.length];
      for (int i = 0; i < doubles.length; i++) {
         terms[i] = new DecimalFraction(doubles[i]);
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code Double} values as a Prolog list to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param doubles the values to use as elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfDoubles(String variableId, List<Double> doubles) {
      Term[] terms = new Term[doubles.size()];
      for (int i = 0; i < doubles.size(); i++) {
         terms[i] = new DecimalFraction(doubles.get(i));
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code long} values as a Prolog list to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param longs the values to use as elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfLongs(String variableId, long... longs) {
      Term[] terms = new Term[longs.length];
      for (int i = 0; i < longs.length; i++) {
         terms[i] = new IntegerNumber(longs[i]);
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code Long} values as a Prolog list to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param longs the values to use as elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfLongs(String variableId, List<Long> longs) {
      Term[] terms = new Term[longs.size()];
      for (int i = 0; i < longs.size(); i++) {
         terms[i] = new IntegerNumber(longs.get(i));
      }
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code Term} values as a Prolog list to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param terms the values to use as elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
   public void setListOfTerms(String variableId, Term... terms) {
      setTerm(variableId, ListFactory.createList(terms));
   }

   /**
    * Attempts to unify the specified {@code Term} values as a Prolog list to the variable with the specified id.
    *
    * @param variableId the id of the variable
    * @param terms the values to use as elements in the list that the variable will be unified with
    * @throws ProjogException if no variable with the specified id exists in the query this object represents, or the
    * given term cannot be unified with the variable
    * @see #setTerm(String, Term)
    */
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
      if (invoked) {
         throw new ProjogException("This QueryStatement has already been evaluated. "
                     + "If you want to reuse the same query then consider using a QueryPlan. See: Projog.createPlan(String)");
      }
      invoked = true;
      return new QueryResult(predicateFactory, parsedInput, variables);
   }

   /**
    * Evaluate once the query represented by this statement.
    * <p>
    * The query will only be evaluated once, even if further solutions could of been found on backtracking.
    *
    * @throws ProjogException if no solution can be found
    * @see #executeQuery()
    */
   public void executeOnce() {
      if (!executeQuery().next()) {
         throw new ProjogException("Failed to find a solution for: " + parsedInput);
      }
   }

   /**
    * Execute the query once and return a String representation of the atom the single query variable was unified with.
    *
    * @return the name of the atom the query variable has been unified with as a result of executing the query
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public String findFirstAsAtomName() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getAtomName(variableId);
      } else {
         throw noSolutionFound();
      }
   }

   /**
    * Execute the query once and return a {@code double} representation of the term the single query variable was
    * unified with.
    *
    * @return the value the query variable has been unified with as a result of executing the query
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public double findFirstAsDouble() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getDouble(variableId);
      } else {
         throw noSolutionFound();
      }
   }

   /**
    * Execute the query once and return a {@code long} representation of the term the single query variable was unified
    * with.
    *
    * @return the value query variable has been unified with as a result of executing the query
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public long findFirstAsLong() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getLong(variableId);
      } else {
         throw noSolutionFound();
      }
   }

   /**
    * Execute the query once and return the {@code Term} the single query variable was unified with.
    *
    * @return the value query variable has been unified with as a result of executing the query
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public Term findFirstAsTerm() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      if (result.next()) {
         return result.getTerm(variableId);
      } else {
         throw noSolutionFound();
      }
   }

   private ProjogException noSolutionFound() {
      return new ProjogException("No solution found.");
   }

   /**
    * Attempt to execute the query once and return a String representation of the atom the single query variable was
    * unified with.
    *
    * @return the name of the atom the query variable has been unified with, or an empty optional if the query was not
    * successfully evaluated
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
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

   /**
    * Attempt to execute the query once and return a {@code Double} representation of the term the single query variable
    * was unified with.
    *
    * @return the value the query variable has been unified with, or an empty optional if the query was not successfully
    * evaluated
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
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

   /**
    * Attempt to execute the query once and return a {@code Long} representation of the term the single query variable
    * was unified with.
    *
    * @return the value the query variable has been unified with, or an empty optional if the query was not successfully
    * evaluated
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
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

   /**
    * Attempt to execute the query once and return a {@code Term} representation of the term the single query variable
    * was unified with.
    *
    * @return the value the query variable has been unified with, or an empty optional if the query was not successfully
    * evaluated
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
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

   /**
    * Find all solutions generated by the query and return String representations of the atoms the single query variable
    * was unified with.
    *
    * @return list of atom names the query variable was been unified with as a result of executing the query until no
    * more solutions were found
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public List<String> findAllAsAtomName() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<String> values = new ArrayList<>();
      while (result.next()) {
         values.add(result.getAtomName(variableId));
      }
      return values;
   }

   /**
    * Find all solutions generated by the query and return the {@code double} values the single query variable was
    * unified with.
    *
    * @return list of values the query variable was been unified with as a result of executing the query until no more
    * solutions were found
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public List<Double> findAllAsDouble() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<Double> values = new ArrayList<>();
      while (result.next()) {
         values.add(result.getDouble(variableId));
      }
      return values;
   }

   /**
    * Find all solutions generated by the query and return the {@code long} values the single query variable was unified
    * with.
    *
    * @return list of values the query variable was been unified with as a result of executing the query until no more
    * solutions were found
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public List<Long> findAllAsLong() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<Long> atomNames = new ArrayList<>();
      while (result.next()) {
         atomNames.add(result.getLong(variableId));
      }
      return atomNames;
   }

   /**
    * Find all solutions generated by the query and return the {@code Term} values the single query variable was unified
    * with.
    *
    * @return list of values the query variable was been unified with as a result of executing the query until no more
    * solutions were found
    * @throws ProjogException if the query could not be evaluated successfully
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   public List<Term> findAllAsTerm() {
      String variableId = getSingleVariableId();
      QueryResult result = executeQuery();
      List<Term> terms = new ArrayList<>();
      while (result.next()) {
         terms.add(result.getTerm(variableId));
      }
      return terms;
   }

   /**
    * Returns the ID of the single variable contained in the query this statement represents.
    *
    * @return variable ID
    * @throws ProjogException of there is not exactly one named variable in the query this statement represents
    */
   private String getSingleVariableId() {
      String id = null;

      for (Map.Entry<String, Variable> e : variables.entrySet()) {
         if (e.getValue().getType().isVariable()) {
            if (id != null) {
               throw new ProjogException("Expected exactly one uninstantiated variable but found " + id + " and " + e.getKey());
            }
            id = e.getKey();
         }
      }

      if (id == null) {
         throw new ProjogException("Expected exactly one uninstantiated variable but found none in: " + parsedInput + " " + variables);
      }

      return id;
   }
}
