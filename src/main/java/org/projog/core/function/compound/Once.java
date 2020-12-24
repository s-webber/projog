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
package org.projog.core.function.compound;

import java.util.Objects;

import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PreprocessablePredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

// TODO shouldn't need to wrap disjunctions in brackets. e.g. should be able to do: once(true;true;fail)
/* TEST
 %TRUE once(repeat)
 %TRUE once(true)
 %TRUE once((true,true,true))
 %TRUE once((true;true;true))
 %TRUE once((fail;true;true))
 %TRUE once((true;fail;true))
 %TRUE once((true;true;fail))
 %FALSE once(fail)
 %FALSE once((fail;fail;fail))
 %FALSE once((fail,fail,fail))
 %FALSE once((true,true,fail))
 %FALSE once((true,fail,true))
 %FALSE once((fail,true,true))
 */
/**
 * <code>once(X)</code> - calls the goal represented by a term.
 * <p>
 * The <code>once(X)</code> goal succeeds if an attempt to satisfy the goal represented by the term <code>X</code>
 * succeeds. No attempt is made to retry the goal during backtracking - it is only evaluated once.
 * </p>
 */
public final class Once extends AbstractSingletonPredicate implements PreprocessablePredicateFactory {
   @Override
   protected boolean evaluate(Term t) {
      Predicate e = KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), t);
      return e.evaluate();
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term arg = term.getArgument(0);
      if (arg.getType().isVariable()) {
         return this;
      } else {
         return new OptimisedOnce(getPredicates().getPreprocessedPredicateFactory(arg));
      }
   }

   private static final class OptimisedOnce extends AbstractSingletonPredicate {
      private final PredicateFactory pf;

      OptimisedOnce(PredicateFactory pf) {
         this.pf = Objects.requireNonNull(pf);
      }

      @Override
      protected boolean evaluate(Term arg) {
         return pf.getPredicate(arg.getArgs()).evaluate();
      }
   }
}
