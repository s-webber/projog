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
package org.projog.core.function.compare;

import static org.projog.core.term.TermUtils.getAtomName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/* TEST
 %QUERY predsort(compare, [s,d,f,a,a,a,z], X)
 %ANSWER X=[a,a,a,d,f,s,z]

 %TRUE predsort(compare, [s,d,f,a,a,a,z], [a,a,a,d,f,s,z])
 %FALSE predsort(compare, [s,d,f,a,a,a,z], [s,d,f,a,a,a,z])

 %TRUE predsort(compare, [], [])
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
public final class PredSort extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term predicateName, Term input, Term sorted) {
      List<Term> list = ListUtils.toJavaUtilList(input);
      if (list == null) {
         return false;
      }

      PredicateFactory pf = getPredicateFactory(predicateName);

      Collections.sort(list, new PredSortComparator(pf));

      return sorted.unify(ListFactory.createList(list));
   }

   private PredicateFactory getPredicateFactory(Term predicateName) {
      PredicateKey key = new PredicateKey(getAtomName(predicateName), 3);
      return getKnowledgeBase().getPredicateFactory(key);
   }

   private static final class PredSortComparator implements Comparator<Term> {
      private final PredicateFactory pf;

      private PredSortComparator(PredicateFactory pf) {
         this.pf = pf;
      }

      @Override
      public int compare(Term o1, Term o2) {
         Variable result = new Variable("PredSortResult");
         Term[] args = new Term[] {result, o1, o2};
         if (pf.getPredicate(args).evaluate()) {
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
            throw new IllegalStateException();
         }
      }
   }
}
