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
package org.projog.core.udp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.PreprocessablePredicateFactory;
import org.projog.core.ProjogException;
import org.projog.core.SpyPoints;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.udp.interpreter.ClauseAction;
import org.projog.core.udp.interpreter.Clauses;
import org.projog.core.udp.interpreter.InterpretedTailRecursivePredicateFactory;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * Maintains a record of the clauses that represents a "static" user defined predicate.
 * <p>
 * A "static" user defined predicate is one that can not have clauses added or removed after it is first defined.
 */
public class StaticUserDefinedPredicateFactory implements UserDefinedPredicateFactory, PreprocessablePredicateFactory {
   private final Object lock = new Object();
   private final PredicateKey predicateKey;
   private final KnowledgeBase kb;
   private final SpyPoints.SpyPoint spyPoint;
   private final List<ClauseModel> implications;
   private PredicateFactory compiledPredicateFactory;
   private int setCompiledPredicateFactoryInvocationCtr;

   public StaticUserDefinedPredicateFactory(KnowledgeBase kb, PredicateKey predicateKey) {
      this.predicateKey = predicateKey;
      this.kb = kb;
      this.spyPoint = kb.getSpyPoints().getSpyPoint(predicateKey);
      this.implications = new ArrayList<>();
   }

   /**
    * Not supported.
    * <p>
    * It is not possible to add a clause to the beginning of a <i>static</i> user defined predicate.
    *
    * @throws UnsupportedOperationException
    */
   @Override
   public void addFirst(ClauseModel clauseModel) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds new clause to list of clauses for this predicate.
    * <p>
    * Note: it is not possible to add clauses to a <i>static</i> user defined predicate once it has been compiled.
    *
    * @throws IllegalStateException if the predicate has already been compiled.
    */
   @Override
   public void addLast(ClauseModel clauseModel) {
      if (compiledPredicateFactory == null) {
         implications.add(clauseModel);
      } else {
         throw new IllegalStateException(predicateKey + " already compiled so cannot add: " + clauseModel);
      }
   }

   public void compile() {
      // make sure we only call setCompiledPredicateFactory once per instance
      if (compiledPredicateFactory == null) {
         synchronized (lock) {
            if (compiledPredicateFactory == null) {
               setCompiledPredicateFactory();
            }
         }
      }
   }

   private void setCompiledPredicateFactory() {
      setCompiledPredicateFactoryInvocationCtr++;
      // TODO always create Clauses here - can we move creation until InterpretedUserDefinedPredicatePredicateFactory
      final Clauses clauses = Clauses.createFromModels(kb, implications);
      compiledPredicateFactory = createPredicateFactoryFromClauseActions(clauses);
   }

   private PredicateFactory createPredicateFactoryFromClauseActions(Clauses clauses) {
      List<ClauseModel> clauseModels = getCopyOfImplications(); // TODO do we need to copy here?
      return createInterpretedPredicateFactoryFromClauseActions(clauses, clauseModels);
   }

   private List<ClauseModel> getCopyOfImplications() {
      List<ClauseModel> copyImplications = new ArrayList<>(implications.size());
      for (ClauseModel clauseModel : implications) {
         copyImplications.add(clauseModel.copy());
      }
      return copyImplications;
   }

   /**
    * Return {@code true} if this predicate calls a predicate that in turns calls this predicate.
    * <p>
    * For example, in the following script both {@code a} and {@code b} are cyclic, but {@code c} is not. <pre>
    * a(Z) :- b(Z).
    *
    * b(Z) :- a(Z).
    *
    * c(Z) :- b(Z).
    * </pre>
    */
   private boolean isCyclic() {
      return setCompiledPredicateFactoryInvocationCtr > 1;
   }

   private boolean areClausesSuitableForCompilation(List<ClauseModel> clauseModels) {
      for (ClauseModel cm : clauseModels) {
         Term antecedent = cm.getAntecedent();
         if (!isTermSuitableForCompilation(antecedent)) {
            return false;
         }
      }
      return true;
   }

   private boolean isTermSuitableForCompilation(Term antecedent) {
      for (Term t : KnowledgeBaseUtils.toArrayOfConjunctions(antecedent)) {
         if (t.getType().isVariable()) {
            return false;
         }
      }
      return true;
   }

   private PredicateFactory createInterpretedPredicateFactoryFromClauseActions(Clauses clauses, List<ClauseModel> clauseModels) {
      TailRecursivePredicateMetaData tailRecursiveMetaData = TailRecursivePredicateMetaData.create(kb, clauseModels);
      if (tailRecursiveMetaData != null) {
         return new InterpretedTailRecursivePredicateFactory(kb, tailRecursiveMetaData);
      } else {
         return createInterpretedPredicateFactoryFromClauses(clauses);
      }
   }

   private PredicateFactory createInterpretedPredicateFactoryFromClauses(Clauses clauses) {
      if (clauses.getClauseActions().length == 1) {
         return createSingleClausePredicateFactory(clauses.getClauseActions()[0]);
      } else if (clauses.getClauseActions().length == 0) {
         return new NeverSucceedsPredicateFactory();
      } else if (clauses.getImmutableColumns().length == 0) {
         return new NotIndexablePredicateFactory(clauses);
      } else {
         return new IndexablePredicateFactory(clauses);
      }
   }

   private PredicateFactory createSingleClausePredicateFactory(ClauseAction clause) {
      if (clause.isRetryable()) {
         return new SingleRetryableRulePredicateFactory(clause, spyPoint);
      } else {
         return new SingleNonRetryableRulePredicate(clause, spyPoint);
      }
   }

   private Predicate createPredicate(Term[] args, ClauseAction[] clauses) {
      switch (clauses.length) {
         case 0:
            return PredicateUtils.createFailurePredicate(spyPoint, args);
         case 1:
            return PredicateUtils.createSingleClausePredicate(clauses[0], spyPoint, args);
         default:
            return new InterpretedUserDefinedPredicate(new ActionIterator(clauses), spyPoint, args);
      }
   }

   @Override
   public Predicate getPredicate(Term[] args) {
      if (args.length != predicateKey.getNumArgs()) {
         throw new ProjogException("User defined predicate: " + predicateKey + " is being called with the wrong number of arguments: " + args.length + " " + Arrays.toString(args));
      }
      compile();
      return compiledPredicateFactory.getPredicate(args);
   }

   @Override
   public PredicateKey getPredicateKey() {
      return predicateKey;
   }

   public PredicateFactory getActualPredicateFactory() { // TODO make package level access
      compile();
      return compiledPredicateFactory;
   }

   /**
    * Returns an iterator over the clauses of this user defined predicate.
    * <p>
    * The iterator returned will have the following characteristics which prevent the underlying structure of the user
    * defined predicate being altered:
    * <ul>
    * <li>Calls to {@link java.util.Iterator#next()} return a <i>new copy</i> of the {@link ClauseModel}.</li>
    * <li>Calls to {@link java.util.Iterator#remove()} cause a {@code UnsupportedOperationException}</li>
    * <li>
    * </ul>
    */
   @Override
   public Iterator<ClauseModel> getImplications() {
      return new ImplicationsIterator(implications);
   }

   @Override
   public boolean isDynamic() {
      return false;
   }

   @Override
   public ClauseModel getClauseModel(int index) {
      if (index >= implications.size()) {
         return null;
      }
      return implications.get(index).copy();
   }

   @Override
   public boolean isRetryable() {
      if (compiledPredicateFactory == null && !isCyclic()) {
         compile();
      }

      return compiledPredicateFactory == null ? true : compiledPredicateFactory.isRetryable();
   }

   @Override
   public PredicateFactory preprocess(Term arg) {
      if (compiledPredicateFactory == null && !isCyclic()) {
         compile();
      }

      if (compiledPredicateFactory instanceof PreprocessablePredicateFactory) {
         return ((PreprocessablePredicateFactory) compiledPredicateFactory).preprocess(arg);
      } else if (compiledPredicateFactory != null) {
         return compiledPredicateFactory;
      } else {
         return this;
      }
   }

   /**
    * @see StaticUserDefinedPredicateFactory#getImplications
    */
   private static final class ImplicationsIterator implements Iterator<ClauseModel> {
      private final Iterator<ClauseModel> iterator;

      ImplicationsIterator(List<ClauseModel> implications) {
         iterator = implications.iterator();
      }

      @Override
      public boolean hasNext() {
         return iterator.hasNext();
      }

      /**
       * Returns a <i>new copy</i> to avoid the original being altered.
       */
      @Override
      public ClauseModel next() {
         ClauseModel clauseModel = iterator.next();
         return clauseModel.copy();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private final class NeverSucceedsPredicateFactory implements PredicateFactory {
      @Override
      public Predicate getPredicate(Term[] args) {
         return PredicateUtils.createFailurePredicate(spyPoint, args);
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }

   private final class IndexablePredicateFactory implements PreprocessablePredicateFactory {
      private final Indexes index;

      private IndexablePredicateFactory(Clauses clauses) {
         this.index = new Indexes(clauses);
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return createPredicate(args, index.index(args));
      }

      @Override
      public boolean isRetryable() {
         return true;
      }

      @Override
      public PredicateFactory preprocess(Term arg) {
         ClauseAction[] data = index.index(arg.getArgs());
         List<ClauseAction> result = optimisePredicateFactory(kb, data, arg);
         if (result.size() < index.getClauseCount()) {
            final Clauses clauses = new Clauses(kb, result);
            return createInterpretedPredicateFactoryFromClauses(clauses);
         } else {
            return this;
         }
      }
   }

   private final class NotIndexablePredicateFactory implements PreprocessablePredicateFactory {
      private final ClauseAction[] data;

      private NotIndexablePredicateFactory(Clauses clauses) {
         this.data = clauses.getClauseActions();
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return new InterpretedUserDefinedPredicate(new ActionIterator(data), spyPoint, args);
      }

      @Override
      public boolean isRetryable() {
         return true;
      }

      @Override
      public PredicateFactory preprocess(Term arg) {
         List<ClauseAction> result = optimisePredicateFactory(kb, data, arg);
         if (result.size() < data.length) {
            final Clauses clauses = new Clauses(kb, result);
            return createInterpretedPredicateFactoryFromClauses(clauses);
         } else {
            return this;
         }
      }
   }

   private static List<ClauseAction> optimisePredicateFactory(KnowledgeBase kb, ClauseAction[] data, Term arg) {
      List<ClauseAction> result = new ArrayList<>();
      Term[] queryArgs = TermUtils.copy(arg.getArgs());
      for (ClauseAction action : data) {
         Term[] clauseArgs = TermUtils.copy(action.getModel().getConsequent().getArgs());
         if (TermUtils.unify(queryArgs, clauseArgs)) {
            result.add(action);
         }
         TermUtils.backtrack(queryArgs);
      }
      if (result.isEmpty()) {
         kb.getProjogListeners().notifyWarn(arg + " will never succeed");
      }
      return result;
   }

   private static final class ActionIterator implements Iterator<ClauseAction> {
      private final ClauseAction[] clauses;
      private int pos = 0;

      ActionIterator(ClauseAction[] clauses) {
         this.clauses = clauses;
      }

      @Override
      public boolean hasNext() {
         return clauses.length > pos;
      }

      @Override
      public ClauseAction next() {
         return clauses[pos++];
      }
   }
}
