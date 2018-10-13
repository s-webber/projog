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
package org.projog.core.udp.interpreter;

import static org.projog.core.KnowledgeBaseUtils.getSpyPoints;
import static org.projog.core.KnowledgeBaseUtils.toArrayOfConjunctions;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.SpyPoints;
import org.projog.core.SpyPoints.SpyPoint;
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

   private static SpyPoints.SpyPoint getSpyPoint(KnowledgeBase kb, TailRecursivePredicateMetaData metaData) { // TODO move to Utils and share
      if (KnowledgeBaseUtils.getProjogProperties(kb).isSpyPointsEnabled()) {
         PredicateKey key = PredicateKey.createForTerm(metaData.getFirstClause().getConsequent());
         return getSpyPoints(kb).getSpyPoint(key);
      } else {
         return null;
      }
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
   }

   @Override
   public boolean isRetryable() {
      return true; // TODO
   }
}
