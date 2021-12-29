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
package org.projog.core.predicate.builtin.compare;

import static org.projog.core.term.TermUtils.getAtomName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.builtin.list.PartialApplicationUtils;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.ListFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
%?- predsort(compare, [s,d,f,a,a,a,z], X)
% X=[a,a,a,d,f,s,z]

%TRUE predsort(compare, [s,d,f,a,a,a,z], [a,a,a,d,f,s,z])
%FAIL predsort(compare, [s,d,f,a,a,a,z], [s,d,f,a,a,a,z])

%TRUE predsort(compare, [], [])

compare_desc(X,Y,Z) :- Y@<Z, X='>'.
compare_desc(X,Y,Z) :- Y@>Z, X='<'.
compare_desc(X,Y,Z) :- Y==Z, X='='.

compare_desc(asc,X,Y,Z) :- Y@<Z, X='>'.
compare_desc(asc,X,Y,Z) :- Y@>Z, X='<'.
compare_desc(asc,X,Y,Z) :- Y==Z, X='='.
compare_desc(desc,X,Y,Z) :- Y@<Z, X='<'.
compare_desc(desc,X,Y,Z) :- Y@>Z, X='>'.
compare_desc(desc,X,Y,Z) :- Y==Z, X='='.

%?- predsort(compare_desc, [s,d,f,a,a,a,z], X)
% X=[z,s,f,d,a,a,a]

% Note: This behaviour is different than the SWI version. SWI version removes duplicates.
%?- predsort(compare_desc(asc), [s,d,f,a,a,a,z], X)
% X=[z,s,f,d,a,a,a]

compare_retryable('>',_,_).
compare_retryable('<',_,_).
compare_retryable('=',_,_).
% Note: This behaviour is different than the SWI version. SWI version backtracks to find alternative solutions.
%?- predsort(compare_retryable, [s,z], X)
% X=[s,z]
*/
/**
 * <code>predsort(X,Y,Z)</code> - sorts a list using the specified predicate.
 * <p>
 * Sorts the list represented by <code>Y</code> using the predicate represented by <code>X</code> - and attempts to
 * unify the result with <code>Z</code>. The predicate represented by <code>X</code> must indicate whether the second
 * argument is equal, less than or greater than the third argument - by unifying the first argument with an atom which
 * has the value <code>=</code>, <code>&lt;</code> or <code>&gt;</code>.
 * </p>
 */
public final class PredSort extends AbstractSingleResultPredicate implements PreprocessablePredicateFactory {
   // The SWI version of this predicate removes duplicates and backtracks to find alternative solutions.
   // TODO Either change this version to behave the same or update documentation to make it clear how the behaviour of this version differs from SWI.

   /** The arity of the predicate represented by the first argument. */
   private static final int FIRST_ARG_ARITY = 3;

   @Override
   protected boolean evaluate(Term predicateName, Term input, Term sorted) {
      PredicateFactory pf = PartialApplicationUtils.getPartiallyAppliedPredicateFactory(getPredicates(), predicateName, FIRST_ARG_ARITY);
      return evaluatePredSort(pf, predicateName, input, sorted);
   }

   private static boolean evaluatePredSort(PredicateFactory pf, Term predicateName, Term input, Term sorted) {
      List<Term> list = ListUtils.toJavaUtilList(input);
      if (list == null) {
         return false;
      }

      Collections.sort(list, new PredSortComparator(pf, predicateName));

      return sorted.unify(ListFactory.createList(list));
   }

   private static final class PredSortComparator implements Comparator<Term> {
      private final PredicateFactory pf;
      private final Term predicateName;

      private PredSortComparator(PredicateFactory pf, Term predicateName) {
         this.pf = pf;
         this.predicateName = predicateName;
      }

      @Override
      public int compare(Term o1, Term o2) {
         Variable result = new Variable("PredSortResult");
         Predicate p = PartialApplicationUtils.getPredicate(pf, predicateName, result, o1, o2);
         if (p.evaluate()) {
            String delta = getAtomName(result);
            switch (delta) {
               case "<":
                  return -1;
               case ">":
                  return 1;
               case "=":
                  return 0;
               default:
                  throw new IllegalArgumentException(delta);
            }
         } else {
            throw new IllegalStateException(predicateName + " " + result + " " + o1 + " " + o2); // TODO
         }
      }
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term goal = term.getArgument(1);
      if (goal.getType() == TermType.ATOM) {
         return new PreprocessedPredSort(PartialApplicationUtils.getPreprocessedPartiallyAppliedPredicateFactory(getPredicates(), goal, FIRST_ARG_ARITY), goal);
      } else {
         return this;
      }
   }

   private static class PreprocessedPredSort implements PredicateFactory {
      private final PredicateFactory pf;
      private final Term predicateName;

      private PreprocessedPredSort(PredicateFactory pf, Term predicateName) {
         this.pf = pf;
         this.predicateName = predicateName;
      }


      @Override
      public Predicate getPredicate(Term[] args) {
         boolean result = evaluatePredSort(pf, predicateName, args[1], args[2]);
         return PredicateUtils.toPredicate(result);
      }

      @Override
      public boolean isRetryable() {
         return false;
      }
   }
}
