/*
 * Copyright 2025 S. Webber
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

import java.util.Collections;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;

/* TEST
%?- random_permutation([], X)
% X=[]

%?- random_permutation([a], X)
% X=[a]

%?- random_permutation([a], [X|_])
% X=a

%FAIL random_permutation([a, b, c], [a, b, c, d])

%?- repeat, random_permutation([a, b, c], X), X = [a, b, c], !
% X=[a,b,c]
%NO
%?- repeat, random_permutation([a, b, c], X), X = [a, c, b], !
% X=[a,c,b]
%NO
%?- repeat, random_permutation([a, b, c], X), X = [b, a, c], !
% X=[b,a,c]
%NO
%?- repeat, random_permutation([a, b, c], X), X = [b, c, a], !
% X=[b,c,a]
%NO
%?- repeat, random_permutation([a, b, c], X), X = [c, a, b], !
% X=[c,a,b]
%NO
%?- repeat, random_permutation([a, b, c], X), X = [c, b, a], !
% X=[c,b,a]
%NO

%?- repeat, random_permutation(X, [a, b, c]), X = [b, c, a], !
% X=[b,c,a]
%NO

%?- repeat, random_permutation([a, b, X],[c, Y, b]), !
% X=c
% Y=a
%NO

%?- random_permutation(X, Y)
%ERROR Expected at least on argument to be a list but got: X and: Y

%?- random_permutation([a|_], [b|_])
%ERROR Expected at least on argument to be a list but got: .(a, _) and: .(b, _)
*/
/**
 * <code>random_permutation(X,Y)</code> - produces a random permutation of a list.
 * <p>
 * Succeeds if the second argument can be unified with a random permutation of the list represented by the first
 * argument.
 */
public final class RandomPermutation extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term a, Term b) {
      List<Term> list = ListUtils.toJavaUtilList(a);
      if (list != null) {
         return shuffleAndUnify(b, list);
      }

      list = ListUtils.toJavaUtilList(b);
      if (list != null) {
         return shuffleAndUnify(a, list);
      }

      throw new ProjogException("Expected at least on argument to be a list but got: " + a + " and: " + b);
   }

   private boolean shuffleAndUnify(Term term, List<Term> list) {
      Collections.shuffle(list);
      return term.unify(ListFactory.createList(list));
   }
}
