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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/**
 * Constructs new {@link ClauseAction} instances.
 */
final class ClauseActionFactory {
   /**
    * Returns true if the arguments unify with the consequent of the clause.
    * <p>
    * TODO move to another class, e.g. ClauseAction
    */
   static boolean isMatch(ClauseAction clause, Term[] queryArgs) {
      Term[] clauseArgs = TermUtils.copy(clause.getModel().getConsequent().getArgs());
      boolean match = TermUtils.unify(queryArgs, clauseArgs);
      TermUtils.backtrack(queryArgs);
      return match;
   }

   /**
    * Returns a new {@link ClauseAction} based on the specified {@link ClauseModel}.
    */
   static ClauseAction createClauseAction(KnowledgeBase kb, ClauseModel model) {
      Term antecedent = model.getAntecedent();
      if (antecedent.getType().isVariable()) {
         return new VariableAntecedantClauseAction(model, kb);
      }

      boolean isFact = model.isFact();

      Term consequent = model.getConsequent();
      if (consequent.getNumberOfArguments() == 0) {
         // have zero arg rule
         return isFact ? new AlwaysMatchedFact(model) : new ZeroArgConsequentRule(model, kb.getPredicates().getPreprocessedPredicateFactory(antecedent));
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

      PredicateFactory preprocessedPredicateFactory = kb.getPredicates().getPreprocessedPredicateFactory(antecedent);
      if (!hasSharedVariables && !hasConcreteTerms) {
         return isFact ? new AlwaysMatchedFact(model) : new MutableRule(model, preprocessedPredicateFactory);
      } else if (hasConcreteTerms && !hasVariables) {
         return isFact ? new ImmutableFact(model) : new ImmutableConsequentRule(model, preprocessedPredicateFactory);
      } else {
         return isFact ? new MutableFact(model) : new MutableRule(model, preprocessedPredicateFactory);
      }
   }

   /**
    * Clause where the antecedent is a variable.
    * <p>
    * When the antecedent is a variable then the associated predicate factory can only be determined at runtime.
    * </p>
    * <p>
    * e.g. "p(X) :- X."
    */
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
         Map<Variable, Variable> sharedVariables = new HashMap<>();
         for (int i = 0; i < input.length; i++) {
            if (!input[i].unify(consequentArgs[i].copy(sharedVariables))) {
               return PredicateUtils.FALSE;
            }
         }

         Term antecedant = model.getAntecedent().copy(sharedVariables);
         return kb.getPredicates().getPredicateFactory(antecedant).getPredicate(antecedant.getArgs());
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return true;
      }

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return false;
      }
   }

   /**
    * Clause where all consequent args are distinctly different variables and antecedent is true.
    * <p>
    * e.g. "p." or "p(X,Y,Z)."
    */
   static final class AlwaysMatchedFact implements ClauseAction {
      private final ClauseModel model;

      private AlwaysMatchedFact(ClauseModel model) {
         this.model = model;
      }

      @Override
      public Predicate getPredicate(Term[] input) {
         return PredicateUtils.TRUE;
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return false;
      }

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return false;
      }
   }

   /**
    * Clause where the consequent has no args.
    * <p>
    * e.g. "p :- test." or "p :- test(_)."
    */
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

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return pf.isAlwaysCutOnBacktrack();
      }
   }

   /**
    * Clause where the consequent args are all immutable and the antecedent is true.
    * <p>
    * e.g. "p(a,b,c)."
    */
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
               return PredicateUtils.FALSE;
            }
         }

         return PredicateUtils.TRUE;
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return false;
      }

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return false;
      }
   }

   /**
    * Clause where the consequent args are all immutable and the antecedent is not true.
    * <p>
    * e.g. "p(a,b,c) :- test." or "p(a,b,c) :- test(_)."
    */
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
               return PredicateUtils.FALSE;
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

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return pf.isAlwaysCutOnBacktrack();
      }
   }

   /**
    * Clause where at least one consequent arg is mutable and the antecedent is true.
    * <p>
    * e.g. "p(a,_,c)." or "p(X,X)."
    */
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
               return PredicateUtils.FALSE;
            }
         }

         return PredicateUtils.TRUE;
      }

      @Override
      public ClauseModel getModel() {
         return model;
      }

      @Override
      public boolean isRetryable() {
         return false;
      }

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return false;
      }
   }

   /**
    * Clause where at least one consequent arg is mutable and the antecedent is not true.
    * <p>
    * e.g. "p(a,_,c) :- test." or ""p(X,X) :- test."
    */
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
               return PredicateUtils.FALSE;
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

      @Override
      public boolean isAlwaysCutOnBacktrack() {
         return pf.isAlwaysCutOnBacktrack();
      }
   }

   // TODO add variation for where antecedent is conjuction of non-retryable predicates
}
