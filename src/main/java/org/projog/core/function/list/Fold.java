/*
 * Copyright 2018 S. Webber
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
package org.projog.core.function.list;

import static org.projog.core.function.AbstractSingletonPredicate.toPredicate;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/* TEST
multiple_result_predicate(X,Y,Z) :-  Z is X+Y.
multiple_result_predicate(X,Y,Z) :-  Z is X*Y.

single_result_predicate(X,Y,Z) :-  Z is X+Y.

%QUERY foldl(single_result_predicate, [2,4,7], 0, X)
%ANSWER X = 13

%QUERY foldl(single_result_predicate, [2,4,7], 42, X)
%ANSWER X = 55

%TRUE foldl(single_result_predicate, [2,4,7], 0, 13)
%FALSE foldl(single_result_predicate, [2,4,7], 0, 12)

%QUERY foldl(multiple_result_predicate, [2,4,7], 42, X)
%ANSWER X=55
%ANSWER X=336
%ANSWER X=183
%ANSWER X=1232
%ANSWER X=95
%ANSWER X=616
%ANSWER X=343
%ANSWER X=2352
%NO

%QUERY foldl(multiple_result_predicate, [1,2,3], 0, X)
%ANSWER X=6
%ANSWER X=9
%ANSWER X=5
%ANSWER X=6
%ANSWER X=5
%ANSWER X=6
%ANSWER X=3
%ANSWER X=0
%NO

%QUERY foldl(multiple_result_predicate, [1,2,3], 0, 6)
%ANSWER/
%ANSWER/
%ANSWER/
%NO
*/
/**
 * <code>foldl(PredicateName, Values, Start, Result)</code> - combines elements of a list into a single term.
 *
 * @see https://en.wikipedia.org/wiki/Fold_(higher-order_function)
 */
public final class Fold extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term atom, Term values, Term start, Term result) {
      PredicateFactory pf = getPredicateFactory(atom);

      List<Term> list = toList(values);

      if (list.isEmpty()) {
         return toPredicate(result.unify(start));
      } else if (pf.isRetryable()) {
         return new Retryable(pf, list, start, result);
      } else {
         boolean success = evaluate(pf, list, start, result);
         return toPredicate(success);
      }
   }

   private PredicateFactory getPredicateFactory(Term atom) {
      String predicateName = TermUtils.getAtomName(atom);
      PredicateKey predicateKey = new PredicateKey(predicateName, 3);
      return getKnowledgeBase().getPredicateFactory(predicateKey);
   }

   private List<Term> toList(Term values) {
      List<Term> list = ListUtils.toJavaUtilList(values);
      if (list == null) {
         throw new ProjogException("Expected concrete list but got: " + values);
      }
      return list;
   }

   private static boolean evaluate(PredicateFactory pf, List<Term> values, Term start, Term result) {
      Term output = start;
      for (Term t : values) {
         Term previous = output;
         output = new Variable("FoldAccumulator");
         Predicate p = pf.getPredicate(previous, t, output);
         if (!p.evaluate()) {
            return false;
         }
      }

      return result.unify(output);
   }

   private static class Retryable implements Predicate {
      private final PredicateFactory pf;
      private final List<Term> values;
      private final Term result;
      private final List<Predicate> predicates;
      private final List<Variable> accumulators;
      private int idx;

      private Retryable(PredicateFactory pf, List<Term> values, Term start, Term result) {
         this.pf = pf;
         this.values = values;
         this.result = result;
         this.accumulators = new ArrayList<>(values.size());
         this.predicates = new ArrayList<>(values.size());

         addNext(start, values.get(0));
      }

      @Override
      public boolean evaluate() {
         if (idx > 0) {
            // If retrying after a previous successful evaluation then backtrack the result
            // so it can be unified against the solution found during this reevaluation.
            result.backtrack();
         }

         while (true) {
            boolean success = predicates.get(idx).evaluate();
            if (success) {
               Term accumulator = accumulators.get(idx);
               if (idx < values.size() - 1) {
                  idx++;
                  addNext(accumulator.getTerm(), values.get(idx));
               } else if (result.unify(accumulator)) {
                  return true;
               }
            } else { // failed evaluation
               if (idx == 0) {
                  return false;
               } else {
                  predicates.remove(idx);
                  accumulators.remove(idx);
                  idx--;
               }
            }
         }
      }

      private void addNext(Term firstArg, Term rightArg) {
         Variable v = new Variable("FoldAccumulator" + idx);
         accumulators.add(v);
         predicates.add(pf.getPredicate(firstArg, rightArg, v));
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return true;
      }
   }
}
