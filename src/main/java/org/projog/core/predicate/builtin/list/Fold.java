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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.predicate.udp.PredicateUtils.toPredicate;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/* TEST
multiple_result_predicate(X,Y,Z) :-  Z is X+Y.
multiple_result_predicate(X,Y,Z) :-  Z is X*Y.

single_result_predicate(X,Y,Z) :-  Z is X+Y.

%?- foldl(single_result_predicate, [2,4,7], 0, X)
% X=13

%?- foldl(single_result_predicate, [2,4,7], 42, X)
% X=55

%TRUE foldl(single_result_predicate, [2,4,7], 0, 13)
%FAIL foldl(single_result_predicate, [2,4,7], 0, 12)

%?- foldl(single_result_predicate, [], 7, X)
% X=7

%?- foldl(single_result_predicate, [3], 7, X)
% X=10

%?- foldl(multiple_result_predicate, [2,4,7], 42, X)
% X=55
% X=336
% X=183
% X=1232
% X=95
% X=616
% X=343
% X=2352

%?- foldl(multiple_result_predicate, [1,2,3], 0, X)
% X=6
% X=9
% X=5
% X=6
% X=5
% X=6
% X=3
% X=0

%?- foldl(multiple_result_predicate, [1,2,3], 0, 6)
%YES
%YES
%YES
%NO

%?- foldl(multiple_result_predicate, [], 7, X)
% X=7

%?- foldl(multiple_result_predicate, [3], 7, X)
% X=10
% X=21

four_arg_predicate(1,X,Y,Z) :-  Z is X+Y.
four_arg_predicate(2,X,Y,Z) :-  Z is X-Y.
four_arg_predicate(3,X,Y,Z) :-  Z is X+Y.
four_arg_predicate(3,X,Y,Z) :-  Z is X-Y.
four_arg_predicate(a,1,2,3).
four_arg_predicate(a,x,y,3).
four_arg_predicate(a,5,3,12).
four_arg_predicate(a,999,_,99999).
four_arg_predicate(b,_,_,_).

%?- foldl(four_arg_predicate(1), [2,4,7], 0, X)
% X=13

%?- foldl(four_arg_predicate(2), [2,4,7], 0, X)
% X=5

%?- foldl(four_arg_predicate(3), [2,4,7], 0, X)
% X=13
% X=1
% X=9
% X=5
% X=13
% X=1
% X=9
% X=5

%?- foldl(four_arg_predicate(3), [2,4,7], 0, 5)
%YES
%YES

%?- foldl(four_arg_predicate(a), [B,C], A, X)
% A=2
% B=1
% C=5
% X=12
% A=2
% B=1
% C=999
% X=99999
% A=y
% B=x
% C=5
% X=12
% A=y
% B=x
% C=999
% X=99999
% A=3
% B=5
% C=999
% X=99999
% A=UNINSTANTIATED VARIABLE
% B=999
% C=999
% X=99999

%FAIL foldl(four_arg_predicate(3), [2,4,7], 0, 14)

%FAIL foldl(four_arg_predicate(4), [2,4,7], 0, X)

% Note: Unlike SWI Prolog, fails on first evaluation if the second argument is not a concrete list.
%?- foldl(single_result_predicate, [2,4,7|T], 0, X)
%ERROR Expected concrete list but got: .(2, .(4, .(7, T)))
%?- foldl(single_result_predicate, L, 0, X)
%ERROR Expected concrete list but got: L
*/
/**
 * <code>foldl(PredicateName, Values, Start, Result)</code> - combines elements of a list into a single term.
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Fold_(higher-order_function)">Wikipedia</a>.
 */
public final class Fold extends AbstractPredicateFactory implements PreprocessablePredicateFactory {
   /** The arity of the predicate represented by the first argument. */
   private static final int FIRST_ARG_ARITY = 3;

   @Override
   public PredicateFactory preprocess(Term arg) {
      Term action = arg.getArgument(0);
      if (PartialApplicationUtils.isAtomOrStructure(action)) {
         PredicateFactory pf = PartialApplicationUtils.getPreprocessedPartiallyAppliedPredicateFactory(getPredicates(), action, FIRST_ARG_ARITY);
         return new OptimisedFold(pf, action);
      } else {
         return this;
      }
   }

   private static class OptimisedFold implements PredicateFactory {
      final PredicateFactory pf;
      final Term action;

      public OptimisedFold(PredicateFactory pf, Term action) {
         this.pf = pf;
         this.action = action;
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return getFoldPredicate(pf, action, args[1], args[2], args[3]);
      }

      @Override
      public boolean isRetryable() {
         return pf.isRetryable();
      }
   }

   @Override
   protected Predicate getPredicate(Term atom, Term values, Term start, Term result) {
      PredicateFactory pf = PartialApplicationUtils.getPartiallyAppliedPredicateFactory(getPredicates(), atom, FIRST_ARG_ARITY);
      return getFoldPredicate(pf, atom, values, start, result);
   }

   private static Predicate getFoldPredicate(PredicateFactory pf, Term action, Term values, Term start, Term result) {
      List<Term> list = toList(values);

      if (list.isEmpty()) {
         return toPredicate(result.unify(start));
      } else if (pf.isRetryable()) {
         return new Retryable(pf, action, list, start, result);
      } else {
         boolean success = evaluateFold(pf, action, list, start, result);
         return toPredicate(success);
      }
   }

   private static List<Term> toList(Term values) {
      List<Term> list = ListUtils.toJavaUtilList(values);
      if (list == null) {
         throw new ProjogException("Expected concrete list but got: " + values);
      }
      return list;
   }

   private static boolean evaluateFold(PredicateFactory pf, Term action, List<Term> values, Term start, Term result) {
      Term output = start;
      for (Term next : values) {
         Term previous = output;
         output = new Variable("FoldAccumulator");
         Predicate p = PartialApplicationUtils.getPredicate(pf, action, next, previous, output);
         if (!p.evaluate()) {
            return false;
         }
      }

      return result.unify(output);
   }

   private static class Retryable implements Predicate {
      private final PredicateFactory pf;
      private final Term action;
      private final List<Term> values;
      private final Term start;
      private final Term result;
      private final List<Predicate> predicates;
      private final List<Variable> accumulators;
      private final List<Term> backtrack1;
      private final List<Term> backtrack2;
      private int idx;

      private Retryable(PredicateFactory pf, Term action, List<Term> values, Term start, Term result) {
         this.pf = pf;
         this.action = action;
         this.values = values;
         this.start = start;
         this.result = result;
         this.accumulators = new ArrayList<>(values.size());
         this.predicates = new ArrayList<>(values.size());
         this.backtrack1 = new ArrayList<>(values.size());
         this.backtrack2 = new ArrayList<>(values.size());
      }

      @Override
      public boolean evaluate() {
         result.backtrack();

         while (idx > -1) {
            final boolean success;
            if (predicates.size() == idx) {
               Term x = values.get(idx).getTerm();
               Term y = idx == 0 ? start.getTerm() : accumulators.get(idx - 1).getTerm();
               Variable v = new Variable("FoldAccumulator" + idx);
               Predicate p = PartialApplicationUtils.getPredicate(pf, action, x, y, v);
               success = p.evaluate();

               accumulators.add(v);
               predicates.add(p);
               backtrack1.add(x);
               backtrack2.add(y);
            } else {
               Predicate p = predicates.get(idx);
               success = p.couldReevaluationSucceed() && p.evaluate();
            }

            if (success) {
               if (idx < values.size() - 1) {
                  idx++;
               } else if (result.unify(accumulators.get(idx))) {
                  return true;
               }
            } else {
               predicates.remove(idx);
               accumulators.remove(idx).backtrack();
               backtrack1.remove(idx).backtrack();
               backtrack2.remove(idx).backtrack();
               idx--;
            }
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         if (predicates.isEmpty()) { // if empty then has not been evaluated yet
            return true;
         }

         for (Predicate p : predicates) {
            if (p.couldReevaluationSucceed()) {
               return true;
            }
         }
         return false;
      }
   }
}
