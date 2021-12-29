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
import static org.projog.core.term.ListUtils.toJavaUtilList;

import java.util.Iterator;
import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%TRUE subtract([a,b,c,d,e,f], [a,s,d,f], [b,c,e])
%TRUE subtract([a,b,a,a,d,c,d,e,f], [a,s,d,f], [b,c,e])
%TRUE subtract([a,b,a,a,d,c,d,e,f], [], [a,b,a,a,d,c,d,e,f])
%TRUE subtract([], [a,s,d,f], [])
%TRUE subtract([], [], [])

%?- subtract([a,a,a,a], [X], Z)
% X=a
% Z=[]

%?- subtract([a,a,a,a,b], [X], Z)
% X=a
% Z=[b]

%?- subtract([p(A),p(B),p(C)], [p(a)],Z)
% A=a
% B=a
% C=a
% Z=[]

%?- subtract([p(a,B,c,e)], [p(A,b,C,e)], Z)
% A=a
% B=b
% C=c
% Z=[]

%?- subtract([p(a,B,c,x)], [p(A,b,C,e)], Z)
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% Z=[p(a, B, c, x)]

%?- subtract([p(a,B), p(A,b)], [p(A,B)], Z)
% A=a
% B=b
% Z=[]

%FAIL subtract(X, [], [])
%FAIL subtract([], X, [])
%FAIL subtract(X, Y, [])
*/
/**
 * <code>subtract(X,Y,Z)</code> - removes elements from a list.
 * <p>
 * <code>subtract(X,Y,Z)</code> removes the elements in the list represented by <code>Y</code> from the list represented
 * by <code>X</code> and attempts to unify the result with <code>Z</code>.
 */
public final class SubtractFromList extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(final Term original, final Term itemsToRemove, final Term result) {
      final List<Term> originalAsList = toJavaUtilList(original);
      final List<Term> itemsToRemoveAsList = toJavaUtilList(itemsToRemove);

      if (originalAsList == null || itemsToRemoveAsList == null) {
         return false;
      }

      final Iterator<Term> itr = originalAsList.iterator();
      while (itr.hasNext()) {
         final Term next = itr.next();
         if (shouldBeRemoved(next, itemsToRemoveAsList)) {
            itr.remove();
         }
      }

      return result.unify(createList(originalAsList));
   }

   private boolean shouldBeRemoved(final Term item, final List<Term> itemsToRemoveAsList) {
      for (Term itemToRemove : itemsToRemoveAsList) {
         if (isUnified(item, itemToRemove)) {
            return true;
         }
      }
      return false;
   }

   private boolean isUnified(Term item, Term itemToRemove) {
      item = item.getTerm();
      itemToRemove = itemToRemove.getTerm();

      if (item.unify(itemToRemove)) {
         return true;
      } else {
         item.backtrack();
         itemToRemove.backtrack();
         return false;
      }
   }
}
