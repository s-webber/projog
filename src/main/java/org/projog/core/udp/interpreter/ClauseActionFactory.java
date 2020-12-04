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
package org.projog.core.udp.interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;

/**
 * Constructs new {@link ClauseAction} instances.
 */
public final class ClauseActionFactory {
   /**
    * Returns a new {@link ClauseAction} based on the specified {@link ClauseModel}.
    */
   public static ClauseAction createClauseAction(KnowledgeBase kb, ClauseModel model) {
      Term antecedent = model.getAntecedent();
      if (antecedent.getType().isVariable()) {
         return new VariableAntecedantClauseAction(model, kb);
      }

      boolean isFact = model.isFact();

      Term consequent = model.getConsequent();
      if (consequent.getNumberOfArguments() == 0) {
         // have zero arg rule
         return isFact ? new AlwaysMatchedFact(model) : new ZeroArgConsequentRule(model, kb.getPreprocessedPredicateFactory(antecedent));
      }

      // if all non-shared variables then always true
      // if all concrete terms (no variables) then reusable
      boolean hasVariables = false;
      boolean hasConcreteTerms = false;
      boolean hasSharedVariables = false;
      Set<Term> variables = new HashSet<>();
      for (Term t : consequent.getArgs()) {
         if (t.getType() == TermType.VARIABLE) {
            hasVariables = true;
            if (!variables.add(t)) {
               hasSharedVariables = true;
            }
         } else {
            hasConcreteTerms = true;
            if (t.isImmutable() == false) {
               hasVariables = true;
            }
         }
      }

      if (!hasSharedVariables && !hasConcreteTerms) {
         return isFact ? new AlwaysMatchedFact(model) : new MutableRule(model, kb.getPreprocessedPredicateFactory(antecedent));
      } else if (hasConcreteTerms && !hasVariables) {
         return isFact ? new ImmutableFact(model) : new ImmutableConsequentRule(model, kb.getPreprocessedPredicateFactory(antecedent));
      } else {
         return isFact ? new MutableFact(model) : new MutableRule(model, kb.getPreprocessedPredicateFactory(antecedent));
      }
   }

   static final class VariableAntecedantClauseAction implements ClauseAction {
      private final ClauseModel model;
      private final KnowledgeBase kb;

      private VariableAntecedantClauseAction(ClauseModel model, KnowledgeBase kb) {
         this.model = model;
         this.kb = kb;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         Term[] consequentArgs = model.getConsequent().getArgs();
         Map<Variable, Variable> sharedVariables = new HashMap<Variable, Variable>();
         for (int i = 0; i < input.length; i++) {
            if (!input[i].unify(consequentArgs[i].copy(sharedVariables))) {
               return AbstractSingletonPredicate.FAIL;
            }
         }

         Term antecedant = model.getAntecedent().copy(sharedVariables);
         return kb.getPredicateFactory(antecedant).getPredicate(antecedant.getArgs());
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return true;
      }
   }

   static final class AlwaysMatchedFact implements ClauseAction {
      private final ClauseModel model;

      private AlwaysMatchedFact(ClauseModel model) {
         this.model = model;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         return AbstractSingletonPredicate.TRUE;
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }

   static final class ZeroArgConsequentRule implements ClauseAction {
      private final ClauseModel model;
      private final PredicateFactory pf;

      private ZeroArgConsequentRule(ClauseModel model, PredicateFactory pf) {
         this.model = model;
         this.pf = pf;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         Term antecedent = model.getAntecedent();
         if (antecedent.isImmutable()) {
            return pf.getPredicate(antecedent.getArgs());
         } else {
            return pf.getPredicate(TermUtils.copy(antecedent.getArgs()));
         }
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return pf.isRetryable();
      }
   }

   static final class ImmutableFact implements ClauseAction {
      private final ClauseModel model;

      private ImmutableFact(ClauseModel model) {
         this.model = model;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         Term[] consequentArgs = model.getConsequent().getArgs();
         for (int i = 0; i < input.length; i++) {
            if (!input[i].unify(consequentArgs[i])) {
               return AbstractSingletonPredicate.FAIL;
            }
         }

         return AbstractSingletonPredicate.TRUE;
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }

   static final class ImmutableConsequentRule implements ClauseAction {
      private final ClauseModel model;
      private final PredicateFactory pf;

      private ImmutableConsequentRule(ClauseModel model, PredicateFactory pf) {
         this.model = model;
         this.pf = pf;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         Term[] consequentArgs = model.getConsequent().getArgs();
         for (int i = 0; i < input.length; i++) {
            if (!input[i].unify(consequentArgs[i])) {
               return AbstractSingletonPredicate.FAIL;
            }
         }

         Term antecedent = model.getAntecedent();
         if (antecedent.isImmutable()) {
            return pf.getPredicate(antecedent.getArgs());
         } else {
            return pf.getPredicate(TermUtils.copy(antecedent.getArgs()));
         }
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return pf.isRetryable();
      }
   }

   static final class MutableFact implements ClauseAction {
      private final ClauseModel model;

      private MutableFact(ClauseModel model) {
         this.model = model;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         // TODO would be a performance improvement if no clause variable is created unless is a shared variable
         Term[] consequentArgs = model.getConsequent().getArgs();
         Map<Variable, Variable> sharedVariables = new HashMap<>();
         for (int i = 0; i < input.length; i++) {
            if (!input[i].unify(consequentArgs[i].copy(sharedVariables))) {
               return AbstractSingletonPredicate.FAIL;
            }
         }

         return AbstractSingletonPredicate.TRUE;
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }

   static final class MutableRule implements ClauseAction {
      private final ClauseModel model;
      private final PredicateFactory pf;

      private MutableRule(ClauseModel model, PredicateFactory pf) {
         this.model = model;
         this.pf = pf;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         Term[] consequentArgs = model.getConsequent().getArgs();
         Map<Variable, Variable> sharedVariables = new HashMap<>();
         for (int i = 0; i < input.length; i++) {
            if (!input[i].unify(consequentArgs[i].copy(sharedVariables))) {
               return AbstractSingletonPredicate.FAIL;
            }
         }

         Term antecedent = model.getAntecedent();
         if (antecedent.isImmutable()) {
            return pf.getPredicate(antecedent.getArgs());
         } else {
            Term[] originalAntecedentArgs = antecedent.getArgs();
            Term[] copyAntecedentArgs = new Term[originalAntecedentArgs.length];
            for (int i = 0; i < originalAntecedentArgs.length; i++) {
               copyAntecedentArgs[i] = originalAntecedentArgs[i].copy(sharedVariables);
            }
            return pf.getPredicate(copyAntecedentArgs);
         }
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return pf.isRetryable();
      }
   }

   // TODO add variation for where antecedent is conjuction of non-retryable predicates
}
