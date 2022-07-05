/*
 * Copyright 2022 S. Webber
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

import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.isKeyValuePair;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- pairs_keys([a-y, c-x, b-z], L)
% L = [a,c,b]

%?- pairs_keys([a-y, a-x, a-z], L)
% L = [a,a,a]

%?- pairs_values([a-y, c-x, b-z], L)
% L = [y,x,z]

%?- pairs_values([a-y, c-y, b-y], L)
% L = [y,y,y]
 */
/**
 * <code>pairs_keys(Pairs,Keys)</code> / <code>pairs_values(Pairs,Values)</code> - get keys or values from list of Key-Value pairs.
 */
public final class PairsElements extends AbstractSingleResultPredicate {
   public static PairsElements keys() {
      return new PairsElements(0);
   }

   public static PairsElements values() {
      return new PairsElements(1);
   }

   private final int argumentIdx;

   private PairsElements(int argumentIdx) {
      this.argumentIdx = argumentIdx;
   }

   @Override
   public boolean evaluate(Term pairs, Term values) {
      Term tail = pairs;
      List<Term> selected = new ArrayList<>();
      while (tail.getType() == TermType.LIST) {
         Term head = tail.getArgument(0);
         if (!isKeyValuePair(head)) {
            throw new ProjogException("Expected every element of list to be a compound term with a functor of - and two arguments but got: " + head);
         }
         selected.add(head.getArgument(argumentIdx));

         tail = tail.getArgument(1);
      }

      if (tail.getType() != TermType.EMPTY_LIST) {
         throw new ProjogException("Expected first element to be a ground list but got: " + pairs);
      }

      return values.unify(ListFactory.createList(selected));
   }
}
