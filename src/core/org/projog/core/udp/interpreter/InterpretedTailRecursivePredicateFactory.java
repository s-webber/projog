package org.projog.core.udp.interpreter;

import static org.projog.core.KnowledgeBaseUtils.toArrayOfConjunctions;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.TailRecursivePredicate;
import org.projog.core.udp.TailRecursivePredicateMetaData;

/**
 * Creates "tail recursion optimised" versions of user defined predicates.
 * <p>
 * Each instance of {@code InterpretedTailRecursivePredicateFactory} creates new instances of
 * {@link InterpretedTailRecursivePredicate} for a specific user defined predicate. The user defined predicate must be
 * judged as eligible for <i>tail recursion optimisation</i> using the criteria used by
 * {@link TailRecursivePredicateMetaData}.
 * </p>
 * <img src="doc-files/InterpretedTailRecursivePredicateFactory.png">
 * 
 * @see TailRecursivePredicate
 * @see TailRecursivePredicateMetaData
 */
public final class InterpretedTailRecursivePredicateFactory implements PredicateFactory {
   private final TailRecursivePredicateMetaData metaData;
   private final PredicateFactory[] firstClausePredicateFactories;
   private final Term[] firstClauseConsequentArgs;
   private final Term[] firstClauseOriginalTerms;
   private final PredicateFactory[] secondClausePredicateFactories;
   private final Term[] secondClauseConsequentArgs;
   private final Term[] secondClauseOriginalTerms;

   public InterpretedTailRecursivePredicateFactory(KnowledgeBase kb, TailRecursivePredicateMetaData metaData) {
      this.metaData = metaData;
      ClauseModel firstClause = metaData.getFirstClause();
      ClauseModel secondClause = metaData.getSecondClause();

      this.firstClauseConsequentArgs = firstClause.getConsequent().getArgs();
      this.secondClauseConsequentArgs = secondClause.getConsequent().getArgs();

      this.firstClauseOriginalTerms = toArrayOfConjunctions(firstClause.getAntecedant());
      this.secondClauseOriginalTerms = toArrayOfConjunctions(secondClause.getAntecedant());

      this.firstClausePredicateFactories = new PredicateFactory[firstClauseOriginalTerms.length];
      for (int i = 0; i < firstClauseOriginalTerms.length; i++) {
         firstClausePredicateFactories[i] = kb.getPredicateFactory(firstClauseOriginalTerms[i]);
      }

      this.secondClausePredicateFactories = new PredicateFactory[secondClauseOriginalTerms.length - 1];
      for (int i = 0; i < secondClausePredicateFactories.length; i++) {
         secondClausePredicateFactories[i] = kb.getPredicateFactory(secondClauseOriginalTerms[i]);
      }
   }

   @Override
   public InterpretedTailRecursivePredicate getPredicate(Term... args) {
      return new InterpretedTailRecursivePredicate(args, firstClausePredicateFactories, firstClauseConsequentArgs, firstClauseOriginalTerms, secondClausePredicateFactories, secondClauseConsequentArgs, secondClauseOriginalTerms, isRetryable(args));
   }

   private boolean isRetryable(Term[] args) {
      for (int i = 0; i < args.length; i++) {
         if (args[i].isImmutable() && metaData.isSingleResultIfArgumentImmutable(i)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }
}