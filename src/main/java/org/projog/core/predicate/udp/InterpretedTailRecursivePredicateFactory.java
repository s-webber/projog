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

import static org.projog.core.kb.KnowledgeBaseUtils.toArrayOfConjunctions;

import org.projog.core.event.SpyPoints;
import org.projog.core.event.SpyPoints.SpyPoint;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

/**
 * Creates "tail recursion optimised" versions of user defined predicates.
 * <p>
 * Each instance of {@code InterpretedTailRecursivePredicateFactory} creates new instances of
 * {@link InterpretedTailRecursivePredicate} for a specific user defined predicate. The user defined predicate must be
 * judged as eligible for <i>tail recursion optimisation</i> using the criteria used by
 * {@link TailRecursivePredicateMetaData}.
 * </p>
 *
 * @see TailRecursivePredicate
 * @see TailRecursivePredicateMetaData
 */
public final class InterpretedTailRecursivePredicateFactory implements PredicateFactory {
   private final SpyPoint spyPoint;
   private final TailRecursivePredicateMetaData metaData;
   private final PredicateFactory[] firstClausePredicateFactories;
   private final Term[] firstClauseConsequentArgs;
   private final Term[] firstClauseOriginalTerms;
   private final PredicateFactory[] secondClausePredicateFactories;
   private final Term[] secondClauseConsequentArgs;
   private final Term[] secondClauseOriginalTerms;

   public InterpretedTailRecursivePredicateFactory(KnowledgeBase kb, TailRecursivePredicateMetaData metaData) {
      this.spyPoint = getSpyPoint(kb, metaData);
      this.metaData = metaData;
      ClauseModel firstClause = metaData.getFirstClause();
      ClauseModel secondClause = metaData.getSecondClause();

      this.firstClauseConsequentArgs = firstClause.getConsequent().getArgs();
      this.secondClauseConsequentArgs = secondClause.getConsequent().getArgs();

      this.firstClauseOriginalTerms = toArrayOfConjunctions(firstClause.getAntecedent());
      this.secondClauseOriginalTerms = toArrayOfConjunctions(secondClause.getAntecedent());

      this.firstClausePredicateFactories = new PredicateFactory[firstClauseOriginalTerms.length];
      for (int i = 0; i < firstClauseOriginalTerms.length; i++) {
         firstClausePredicateFactories[i] = kb.getPredicates().getPredicateFactory(firstClauseOriginalTerms[i]);
      }

      this.secondClausePredicateFactories = new PredicateFactory[secondClauseOriginalTerms.length - 1];
      for (int i = 0; i < secondClausePredicateFactories.length; i++) {
         secondClausePredicateFactories[i] = kb.getPredicates().getPredicateFactory(secondClauseOriginalTerms[i]);
      }
   }

   @Override
   public InterpretedTailRecursivePredicate getPredicate(Term[] args) {
      return new InterpretedTailRecursivePredicate(spyPoint, args, firstClausePredicateFactories, firstClauseConsequentArgs, firstClauseOriginalTerms,
                  secondClausePredicateFactories, secondClauseConsequentArgs, secondClauseOriginalTerms, isRetryable(args));
   }

   private boolean isRetryable(Term[] args) {
      for (int i = 0; i < args.length; i++) {
         if (args[i].isImmutable() && metaData.isSingleResultIfArgumentImmutable(i)) {
            return false;
         }
      }
      return true;
   }

   private static SpyPoints.SpyPoint getSpyPoint(KnowledgeBase kb, TailRecursivePredicateMetaData metaData) {
      PredicateKey key = PredicateKey.createForTerm(metaData.getFirstClause().getConsequent());
      return kb.getSpyPoints().getSpyPoint(key);
   }

   @Override
   public boolean isRetryable() {
      return true; // TODO
   }
}
