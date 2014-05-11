package org.projog.core.function.kb;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/**
 * Extended by {@code Predicate}s that add new clauses to a dynamic user defined predicate.
 */
abstract class AbstractAssertFunction extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term consequent) {
      PredicateKey key = PredicateKey.createForTerm(consequent);
      KnowledgeBase kb = getKnowledgeBase();
      UserDefinedPredicateFactory userDefinedPredicate = kb.createOrReturnUserDefinedPredicate(key);
      ClauseModel clauseModel = ClauseModel.createClauseModel(consequent);
      add(userDefinedPredicate, clauseModel);
      return true;
   }

   /**
    * Adds the specified {@code ClauseModel} to the specified {@code UserDefinedPredicateFactory}.
    */
   protected abstract void add(UserDefinedPredicateFactory userDefinedPredicate, ClauseModel clauseModel);
}