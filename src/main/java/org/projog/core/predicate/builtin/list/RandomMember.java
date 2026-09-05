/*
 * Copyright 2026 S. Webber
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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%FAIL random_member(X, [])

%?- random_member(X, [a])
% X=a

%?- random_member(a, [X])
% X=a

%TRUE random_member(a, [a])
%TRUE_NO repeat, random_member(a, [a, b, c]), !
%TRUE_NO repeat, random_member(b, [a, b, c]), !
%TRUE_NO repeat, random_member(c, [a, b, c]), !
%FAIL random_member(d, [a, b, c])

%?- random_member(X, Y)
%ERROR Expected second argument to be a list but got: Y

%?- random_member(a, [b|_])
%ERROR Expected second argument to be a list but got: .(b, _)
*/
/**
 * <code>random_member(X,Y)</code> - unifies first argument with term randomly selected from list of second argument.
 * <p>
 * Succeeds if the first argument can be unified with a randomly selected element of the list represented by the second
 * argument.
 */
public final class RandomMember extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term target, Term elements) {
      if (elements.getType() == TermType.EMPTY_LIST) {
         return false;
      }

      List<Term> list = ListUtils.toJavaUtilList(elements);
      if (list == null) {
         throw new ProjogException("Expected second argument to be a list but got: " + elements);
      }

      Term randomElement = list.get(ThreadLocalRandom.current().nextInt(list.size()));
      return target.unify(randomElement);
   }
}
