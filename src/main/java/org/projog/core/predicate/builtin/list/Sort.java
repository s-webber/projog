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

import java.util.List;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%?- msort([q,w,e,r,t,y], X)
% X=[e,q,r,t,w,y]

%?- msort([q,q,w,y,e,r,r,t,r,y], X)
% X=[e,q,q,r,r,r,t,w,y,y]

%TRUE msort([q,w,e,r,t,y], [e,q,r,t,w,y])
%FAIL msort([q,w,e,r,t,y], [q,w,e,r,t,y])
%FAIL msort([q,w,e,r,t,y], [e,q,t,r,w,y])

%?- msort([q,w,e,r,t,y], [A,B,C,D,E,F])
% A=e
% B=q
% C=r
% D=t
% E=w
% F=y

%?- msort([], X)
% X=[]

%?- msort([a], X)
% X=[a]

%FAIL msort(a, X)
%FAIL msort([a,b,c|T], X)

%?- msort([h,e,l,l,o], X)
% X=[e,h,l,l,o]

%FAIL msort([h,e,l,l,o], [e,h,l,o])

% Note: unlike SWI Prolog, the following 3 queries will fail rather than cause an error.
%FAIL msort(a, X)
%FAIL msort(X, [h,e,l,l,o])
%FAIL msort([h,e,l,l,o|X], Y)
*/
/**
 * <code>msort(X,Y)</code> - sorts a list.
 * <p>
 * Attempts to unify <code>Y</code> with a sorted version of the list represented by <code>X</code>.
 * </p>
 * <p>
 * Note that, unlike <code>sort/2</code>, duplicates are <i>not</i> removed.
 * </p>
 */
public final class Sort extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term unsorted, Term sorted) {
      List<Term> elements = toSortedJavaUtilList(unsorted);
      if (elements == null) {
         return false;
      } else {
         return sorted.unify(createList(elements));
      }
   }
}
