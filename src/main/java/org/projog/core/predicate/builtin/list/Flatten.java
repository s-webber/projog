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
package org.projog.core.predicate.builtin.list;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- flatten([a,[[b]],[c]], X)
% X=[a,b,c]

%?- flatten([a,b,c], X)
% X=[a,b,c]

%?- flatten([[[[a]]],[],[],[]], X)
% X=[a]

%?- flatten([a], X)
% X=[a]

%?- flatten(a, X)
% X=[a]

%?- flatten([[[[]]],[],[],[]], X)
% X=[]

%?- flatten([], X)
% X=[]

%?- flatten([a|b], X)
% X=[a,b]

%?- flatten([a|[]], X)
% X=[a]

%?- flatten([[a|b],[c,d|e],[f|[]],g|h], X)
% X=[a,b,c,d,e,f,g,h]

%?- flatten([p([[a]]),[[[p(p(x))]],[p([a,b,c])]]], X)
% X=[p([[a]]),p(p(x)),p([a,b,c])]

%FAIL flatten([a,b,c], [c,b,a])
%FAIL flatten([a,b,c], [a,[b],c])

%?- flatten([a,b,[c|X],d|Y], Z)
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
% Z=[a,b,c,X,d,Y]
*/
/**
 * <code>flatten(X,Y)</code> - flattens a nested list.
 * <p>
 * Flattens the nested list represented by <code>X</code> and attempts to unify it with <code>Y</code>.
 */
public final class Flatten extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(final Term original, final Term expected) {
      final Term flattenedVersion;
      switch (original.getType()) {
         case LIST:
            flattenedVersion = ListFactory.createList(flattenList(original));
            break;
         case EMPTY_LIST:
            flattenedVersion = original;
            break;
         default:
            flattenedVersion = ListFactory.createList(original, EmptyList.EMPTY_LIST);
      }
      return expected.unify(flattenedVersion);
   }

   private List<Term> flattenList(final Term input) {
      List<Term> result = new ArrayList<>();
      Term next = input;
      while (next.getType() == TermType.LIST) {
         Term head = next.getArgument(0);
         if (head.getType() == TermType.LIST) {
            result.addAll(flattenList(head));
         } else if (head.getType() != TermType.EMPTY_LIST) {
            result.add(head);
         }

         next = next.getArgument(1);
      }
      if (next.getType() != TermType.EMPTY_LIST) {
         result.add(next);
      }
      return result;
   }
}
