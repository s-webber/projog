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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.CutException;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

public final class TabledUserDefinedPredicateFactory implements UserDefinedPredicateFactory {
   private final PredicateKey predicateKey;
   private final KnowledgeBase kb;
   private final List<ClauseModel> implications;
   private final Map<Term, Processor> processing = new HashMap<>();
   private final Map<Term, Term[]> cache = new HashMap<>();
   private Clauses clauses;

   public TabledUserDefinedPredicateFactory(KnowledgeBase kb, PredicateKey predicateKey) {
      this.predicateKey = predicateKey;
      this.kb = kb;
      this.implications = new ArrayList<>();
   }

   @Override
   public void addFirst(ClauseModel clauseModel) {
      throw new ProjogException("Cannot add clause to already defined user defined predicate as it is not dynamic: " + predicateKey + " clause: " + clauseModel.getOriginal());
   }

   @Override
   public void addLast(ClauseModel clauseModel) {
      if (clauses == null) {
         implications.add(clauseModel);
      } else {
         throw new ProjogException("Cannot add clause to already defined user defined predicate as it is not dynamic: " + predicateKey + " clause: " + clauseModel.getOriginal());
      }
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public PredicateKey getPredicateKey() {
      return predicateKey;
   }

   @Override
   public ImplicationsIterator getImplications() {
      return new ImplicationsIterator(implications);
   }

   @Override
   public boolean isDynamic() {
      return true;
   }

   @Override
   public ClauseModel getClauseModel(int index) {
      if (index >= implications.size()) {
         return null;
      }
      return implications.get(index).copy();
   }

   @Override
   public Predicate getPredicate(Term term) {
      compile();

      Term[] cachedResult = generateAllSolutions(term);

      if (cachedResult.length == 0) {
         return PredicateUtils.FALSE;
      }

      return new TabledUserDefinedPredicate(term, cachedResult);
   }

   @Override
   public void compile() {
      // make sure we only call clauses once per instance
      if (clauses == null) {
         synchronized (cache) {
            if (clauses == null) {
               clauses = Clauses.createFromModels(kb, implications);
            }
         }
      }
   }

   private Term[] generateAllSolutions(Term term) {
      Term queryKey = createKey(term);

      Term[] cachedResult = cache.get(queryKey);
      if (cachedResult != null) {
         return cachedResult;
      }

      Processor processor;
      final boolean newProcessor;
      synchronized (processing) {
         // if already have a processed result for this query then return it now rather than processing a new version
         Term[] previouslyCachedResult = cache.get(queryKey);
         if (previouslyCachedResult != null) {
            return previouslyCachedResult;
         }

         // see if this query is already being processed
         processor = processing.get(queryKey);
         newProcessor = processor == null;

         // if this query is not already being processed then create a Processor for it
         if (newProcessor) {
            processor = new Processor(term, clauses, Thread.currentThread());
            processing.put(queryKey, processor);
         }
      }

      // if query is being processed by another thread then wait for it to finish and then reuse the cached result
      if (Thread.currentThread() != processor.originalThread) {
         kb.getProjogListeners().notifyInfo("Waiting for other thread to complete tabling of: " + predicateKey);
         synchronized (processor) {
            try {
               processor.wait();
            } catch (InterruptedException e) {
            }
            return cache.get(queryKey);
         }
      }

      processor.process();
      if (processor.exception != null) {
         if (processor.exception instanceof RuntimeException) {
            throw (RuntimeException) processor.exception;
         } else {
            throw new RuntimeException(processor.exception);
         }
      }
      Term[] result = processor.result.toArray(new Term[processor.result.size()]);

      // if the Processor was created as part of this method call then store the result in the cache so it can be reused
      if (newProcessor) {
         synchronized (processing) {
            cache.put(queryKey, result);
            processing.remove(queryKey);
         }
         // notify any threads that are waiting for the result
         synchronized (processor) {
            processor.notifyAll();
         }
      }

      return result;
   }

   private static Term createKey(Term term) {
      return term.copy(new VariableReplacementMap());
   }

   private static class Processor {
      private final Term originalTerm;
      private final ClauseAction[] clauses;
      private final Thread originalThread;
      private final List<Term> result = new ArrayList<>();
      private final Set<Term> alreadyFound = new HashSet<>();
      private int clauseIdx;
      private Throwable exception;

      Processor(Term originalTerm, Clauses clauses, Thread curentThread) {
         this.originalTerm = originalTerm;
         this.clauses = clauses.getClauseActions();
         this.originalThread = curentThread;
      }

      void process() {
         try {
            while (clauseIdx < clauses.length) {
               ClauseAction clause = clauses[clauseIdx++];
               Term copiedTerm = originalTerm.copy();
               Predicate p = clause.getPredicate(copiedTerm);
               boolean keepGoing = true;
               while (keepGoing && p.evaluate()) {
                  Term copy = copiedTerm.copy();
                  if (alreadyFound.add(createKey(copy))) {
                     result.add(copy);
                  }
                  keepGoing = p.couldReevaluationSucceed();
               }
            }
         } catch (CutException e) {
            clauseIdx = clauses.length;
         } catch (Throwable e) {
            clauseIdx = clauses.length;
            exception = e;
         }
      }
   }

   private static class TabledUserDefinedPredicate implements Predicate {
      private final Term input;
      private final Term[] results;
      private int resultIdx;

      TabledUserDefinedPredicate(Term input, Term[] results) {
         this.input = input;
         this.results = results;
      }

      @Override
      public boolean evaluate() {
         input.backtrack();
         if (!input.unify(results[resultIdx++].copy())) {
            throw new IllegalStateException(); // should never get here
         }
         return true;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return resultIdx < results.length;
      }
   }

   /**
    * Used as argument to {@link Term#copy(Map)} to replace occurrences of {@code Variable} with
    * {@code PlaceholderTerm}.
    * <p>
    * This is required so the returned value can be used as a key in the map of cached solutions. Means that
    * {@code x(X)} and {@code x(Y)} are considered equal.
    */
   @SuppressWarnings("serial")
   private static final class VariableReplacementMap extends HashMap<Variable, Term> {
      @Override
      public Term get(Object k) {
         Term t = super.get(k);
         if (t == null) {
            t = new PlaceholderTerm(size()); // NOTE could cache PlaceholderTerm, rather than creating new each time
            super.put((Variable) k, t);
         }
         return t;
      }
   }

   private static class PlaceholderTerm implements Term {
      final int id;

      PlaceholderTerm(int id) {
         this.id = id;
      }

      @Override
      public String getName() {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getNumberOfArguments() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Term getArgument(int index) {
         throw new UnsupportedOperationException();
      }

      @Override
      public TermType getType() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Term copy(Map<Variable, Term> sharedVariables) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Term getTerm() {
         return this;
      }

      @Override
      public boolean unify(Term t) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void backtrack() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isImmutable() {
         return true;
      }

      @Override
      public int hashCode() {
         return id;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof PlaceholderTerm && id == ((PlaceholderTerm) o).id;
      }
   }
}
