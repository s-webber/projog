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

import static org.projog.core.KnowledgeBaseUtils.getProjogProperties;
import static org.projog.core.KnowledgeBaseUtils.getSpyPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseServiceLocator;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.SpyPoints;
import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventType;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.compiler.CompiledPredicateClassGenerator;
import org.projog.core.udp.interpreter.ClauseAction;
import org.projog.core.udp.interpreter.Clauses;
import org.projog.core.udp.interpreter.InterpretedTailRecursivePredicateFactory;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * Maintains a record of the clauses that represents a "static" user defined predicate.
 * <p>
 * A "static" user defined predicate is one that can not have clauses added or removed after it is first defined.
 */
public class StaticUserDefinedPredicateFactory implements UserDefinedPredicateFactory {
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
      this.spyPoint = getSpyPoints(kb).getSpyPoint(predicateKey);
      this.implications = new ArrayList<>();
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      throw new UnsupportedOperationException();
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
      final Clauses clauses = new Clauses(kb, implications);
      compiledPredicateFactory = createPredicateFactoryFromClauseActions(clauses);
   }

   private PredicateFactory createPredicateFactoryFromClauseActions(Clauses clauses) {
      List<ClauseModel> clauseModels = getCopyOfImplications(); // TODO do we need to copy here?

      if (isPredicateSuitableForCompilation(clauseModels)) {
         try {
            return createCompiledPredicateFactoryFromClauseActions(clauseModels);
         } catch (Exception e) {
            // If fail to compile a user-defined predicate to Java - possibly because the predicate is too large -
            // then revert to evaluating the predicate in interpreted mode rather than throwing an exception.
            ProjogEvent warning = new ProjogEvent(ProjogEventType.WARN,
                        "Caught exception while compiling " + predicateKey + " to Java so will revert to operating in interpreted mode for this predicate.", this);
            KnowledgeBaseUtils.getProjogEventsObservable(kb).notifyObservers(warning);
         }
      }

      return createInterpretedPredicateFactoryFromClauseActions(clauses, clauseModels);
   }

   private List<ClauseModel> getCopyOfImplications() {
      List<ClauseModel> copyImplications = new ArrayList<>(implications.size());
      for (ClauseModel clauseModel : implications) {
         copyImplications.add(clauseModel.copy());
      }
      return copyImplications;
   }

   private boolean isPredicateSuitableForCompilation(List<ClauseModel> clauseModels) {
      if (isInterpretedMode()) {
         return false;
      } else if (isCyclic()) {
         return false;
      } else {
         return areClausesSuitableForCompilation(clauseModels);
      }
   }

   private boolean isInterpretedMode() {
      return !getProjogProperties(kb).isRuntimeCompilationEnabled();
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

   private PredicateFactory createCompiledPredicateFactoryFromClauseActions(List<ClauseModel> clauseModels) {
      CompiledPredicateClassGenerator g = KnowledgeBaseServiceLocator.getServiceLocator(kb).getInstance(CompiledPredicateClassGenerator.class);
      return g.generateCompiledPredicate(kb, clauseModels);
   }

   private PredicateFactory createInterpretedPredicateFactoryFromClauseActions(Clauses clauses, List<ClauseModel> clauseModels) {
      TailRecursivePredicateMetaData tailRecursiveMetaData = TailRecursivePredicateMetaData.create(kb, clauseModels);
      if (tailRecursiveMetaData != null) {
         return new InterpretedTailRecursivePredicateFactory(kb, tailRecursiveMetaData);
      } else if (clauses.getClauseActions().length == 1) {
         return createSingleClausePredicateFactory(clauses.getClauseActions()[0]);
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

   private Predicate createSingleClausePredicate(ClauseAction clause, Term[] args) {
      if (clause.isRetryable()) {
         return new SingleRetryableRulePredicateFactory.RetryableRulePredicate(clause, spyPoint, args);
      } else {
         return SingleNonRetryableRulePredicate.evaluateClause(clause, spyPoint, args);
      }
   }

   private Predicate createPredicate(Term[] args, ClauseAction[] clauses) {
      switch (clauses.length) {
         case 0:
            if (spyPoint.isEnabled()) {
               spyPoint.logCall(this, args);
               spyPoint.logFail(this, args);
            }
            return AbstractSingletonPredicate.FAIL;
         case 1:
            return createSingleClausePredicate(clauses[0], args);
         default:
            return new InterpretedUserDefinedPredicate(new ActionIterator(clauses), spyPoint, args);
      }
   }

   @Override
   public Predicate getPredicate(Term... args) {
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

   public PredicateFactory getActualPredicateFactory() {
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
      return getActualPredicateFactory().isRetryable();
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

   private final class IndexablePredicateFactory implements PredicateFactory {
      private final Indexes index;

      private IndexablePredicateFactory(Clauses clauses) {
         this.index = new Indexes(clauses);
      }

      @Override
      public Predicate getPredicate(Term... args) {
         return createPredicate(args, index.index(args));
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase kb) {
      }

      @Override
      public boolean isRetryable() {
         return true;
      }
   }

   private final class NotIndexablePredicateFactory implements PredicateFactory {
      private final ClauseAction[] data;

      private NotIndexablePredicateFactory(Clauses clauses) {
         this.data = clauses.getClauseActions();
      }

      @Override
      public Predicate getPredicate(Term... args) {
         return new InterpretedUserDefinedPredicate(new ActionIterator(data), spyPoint, args);
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase kb) {
      }

      @Override
      public boolean isRetryable() {
         return true;
      }
   }

   private static final class ActionIterator implements Iterator<ClauseAction> {
      private final ClauseAction[] clauses;
      private int pos=0;

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
