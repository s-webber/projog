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

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toSortedJavaUtilList;

import java.util.Iterator;
import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* TEST
%?- sort([q,w,e,r,t,y], X)
% X=[e,q,r,t,w,y]

%TRUE sort([q,w,e,r,t,y], [e,q,r,t,w,y])
%FAIL sort([q,w,e,r,t,y], [q,w,e,r,t,y])
%FAIL sort([q,w,e,r,t,y], [e,q,t,r,w,y])

%?- sort([q,w,e,r,t,y], [A,B,C,D,E,F])
% A=e
% B=q
% C=r
% D=t
% E=w
% F=y

%?- sort([], X)
% X=[]

%?- sort([a], X)
% X=[a]

%FAIL sort(a, X)
%FAIL sort([a,b,c|T], X)

%?- sort([h,e,l,l,o], X)
% X=[e,h,l,o]

%FAIL sort([h,e,l,l,o], [e,h,l,l,o])
*/
/**
 * <code>sort(X,Y)</code> - sorts a list and removes duplicates.
 * <p>
 * Attempts to unify <code>Y</code> with a sorted version of the list represented by <code>X</code>, with duplicates
 * removed.
 * </p>
 */
public final class SortAsSet extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term unsorted, Term sorted) {
      List<Term> elements = toSortedJavaUtilList(unsorted);
      if (elements == null) {
         return false;
      } else {
         removeDuplicates(elements);
         return sorted.unify(createList(elements));
      }
   }

   private void removeDuplicates(List<Term> elements) {
      Iterator<Term> itr = elements.iterator();
      Term previous = itr.hasNext() ? itr.next() : null;
      while (itr.hasNext()) {
         Term next = itr.next();
         if (TermUtils.termsEqual(previous, next)) {
            itr.remove();
         }
         previous = next;
      }
   }
}
