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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.event.SpyPoints;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

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
    * @throws ProjogException
    */
   @Override
   public void addFirst(ClauseModel clauseModel) {
      throw new ProjogException("Cannot add clause to already defined user defined predicate as it is not dynamic: " + predicateKey + " clause: " + clauseModel.getOriginal());
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
         throw new ProjogException("Cannot add clause to already defined user defined predicate as it is not dynamic: " + predicateKey + " clause: " + clauseModel.getOriginal());
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
      Clauses clauses = Clauses.createFromModels(kb, implications);
      List<ClauseModel> clauseModels = getCopyOfImplications(); // TODO do we need to copy here?
      compiledPredicateFactory = createInterpretedPredicateFactoryFromClauseActions(clauses, clauseModels);
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
         return new NeverSucceedsPredicateFactory(spyPoint);
      } else if (clauses.getImmutableColumns().length == 0) {
         return new NotIndexablePredicateFactory(clauses);
      } else if (clauses.getImmutableColumns().length == 1) {
         Index index = new Indexes(clauses).getOrCreateIndex(1);
         ClauseAction[] actions = clauses.getClauseActions();
         if (index.getKeyCount() == actions.length) {
            return new LinkedHashMapPredicateFactory(clauses);
         } else {
            return new SingleIndexPredicateFactory(clauses);
         }
      } else {
         return new IndexablePredicateFactory(clauses);
      }
   }

   private PredicateFactory createSingleClausePredicateFactory(ClauseAction clause) {
      if (clause.isRetryable() && !clause.isAlwaysCutOnBacktrack()) {
         return new SingleRetryableRulePredicateFactory(clause, spyPoint);
      } else {
         return new SingleNonRetryableRulePredicateFactory(clause, spyPoint);
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

   /**
    * Returns true if clauses could return more than one result.
    * <p>
    * Returns false if use of cut means once a result has been found a subsequent attempt at backtracking will
    * immediately fail. e.g.: <pre>
    * p(X) :- var(X), !.
    * p(1) :- !.
    * p(7). % OK for last rule not to contain a cut, as long as it is not retryable.
    * </pre>
    */
   private static boolean isClausesRetryable(ClauseAction[] clauses) {
      int lastIdx = clauses.length - 1;
      ClauseAction last = clauses[lastIdx];
      if (last.isRetryable() && !last.isAlwaysCutOnBacktrack()) {
         return true;
      }

      for (int i = lastIdx - 1; i > -1; i--) {
         if (!clauses[i].isAlwaysCutOnBacktrack()) {
            return true;
         }
      }

      return false;
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
   }

   private final class LinkedHashMapPredicateFactory implements PreprocessablePredicateFactory {
      private final int argIdx;
      private final ClauseAction[] actions;
      private final LinkedHashMap<Term, ClauseAction> map;
      private final boolean retryable;

      private LinkedHashMapPredicateFactory(Clauses clauses) {
         this.argIdx = clauses.getImmutableColumns()[0];
         this.actions = clauses.getClauseActions();
         this.map = new LinkedHashMap<>(actions.length);
         for (ClauseAction a : actions) {
            map.put(a.getModel().getConsequent().getArgument(argIdx), a);
         }
         this.retryable = isClausesRetryable(actions);
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         if (args[argIdx].isImmutable()) {
            ClauseAction action = map.get(args[argIdx]);
            if (action == null) {
               return PredicateUtils.createFailurePredicate(spyPoint, args);
            } else {
               return PredicateUtils.createSingleClausePredicate(action, spyPoint, args);
            }
         } else {
            return createPredicate(args, actions);
         }
      }

      @Override
      public boolean isRetryable() {
         return retryable;
      }

      @Override
      public PredicateFactory preprocess(Term arg) {
         if (arg.getArgument(argIdx).isImmutable()) {
            ClauseAction action = map.get(arg.getArgument(argIdx));
            if (action == null) {
               return new NeverSucceedsPredicateFactory(spyPoint);
            } else {
               return createSingleClausePredicateFactory(action);
            }
         } else {
            List<ClauseAction> result = optimisePredicateFactory(kb, actions, arg);
            if (result.size() < actions.length) {
               final Clauses clauses = Clauses.createFromActions(kb, result, arg);
               return createInterpretedPredicateFactoryFromClauses(clauses);
            } else {
               return this;
            }
         }
      }
   }

   private final class SingleIndexPredicateFactory implements PreprocessablePredicateFactory {
      private final int argIdx;
      private final Index index;
      private final ClauseAction[] actions;
      private final boolean retryable;

      private SingleIndexPredicateFactory(Clauses clauses) {
         this.argIdx = clauses.getImmutableColumns()[0];
         this.index = new Indexes(clauses).getOrCreateIndex(1);
         this.actions = clauses.getClauseActions();
         this.retryable = isClausesRetryable(actions);
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         ClauseAction[] data;
         if (args[argIdx].isImmutable()) {
            data = index.getMatches(args);
         } else {
            data = actions;
         }

         return createPredicate(args, data);
      }

      @Override
      public boolean isRetryable() {
         return retryable;
      }

      @Override
      public PredicateFactory preprocess(Term arg) {
         ClauseAction[] data;
         if (arg.getArgument(argIdx).isImmutable()) {
            data = index.getMatches(arg.getArgs());
         } else {
            data = actions;
         }

         List<ClauseAction> result = optimisePredicateFactory(kb, data, arg);
         if (result.size() < actions.length) {
            final Clauses clauses = Clauses.createFromActions(kb, result, arg);
            return createInterpretedPredicateFactoryFromClauses(clauses);
         } else {
            return this;
         }
      }
   }

   private final class IndexablePredicateFactory implements PreprocessablePredicateFactory {
      private final Indexes index;
      private final boolean retryable;

      private IndexablePredicateFactory(Clauses clauses) {
         this.index = new Indexes(clauses);
         this.retryable = isClausesRetryable(clauses.getClauseActions());
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return createPredicate(args, index.index(args));
      }

      @Override
      public boolean isRetryable() {
         return retryable;
      }

      @Override
      public PredicateFactory preprocess(Term arg) {
         ClauseAction[] data = index.index(arg.getArgs());
         List<ClauseAction> result = optimisePredicateFactory(kb, data, arg);
         if (result.size() < index.getClauseCount()) {
            final Clauses clauses = Clauses.createFromActions(kb, result, arg);
            return createInterpretedPredicateFactoryFromClauses(clauses);
         } else {
            return this;
         }
      }
   }

   private final class NotIndexablePredicateFactory implements PreprocessablePredicateFactory {
      private final ClauseAction[] data;
      private final boolean retryable;

      private NotIndexablePredicateFactory(Clauses clauses) {
         this.data = clauses.getClauseActions();
         this.retryable = isClausesRetryable(data);
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         // TODO or do: return createPredicate(args, data);
         return new InterpretedUserDefinedPredicate(new ActionIterator(data), spyPoint, args);
      }

      @Override
      public boolean isRetryable() {
         return retryable;
      }

      @Override
      public PredicateFactory preprocess(Term arg) {
         List<ClauseAction> result = optimisePredicateFactory(kb, data, arg);
         if (result.size() < data.length) {
            final Clauses clauses = Clauses.createFromActions(kb, result, arg);
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
         if (ClauseActionFactory.isMatch(action, queryArgs)) {
            result.add(action);
         }
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
