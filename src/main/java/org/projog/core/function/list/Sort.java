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
package org.projog.core.function.list;

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toSortedJavaUtilList;

import java.util.List;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY msort([q,w,e,r,t,y], X)
 %ANSWER X = [e,q,r,t,w,y]
 
 %TRUE msort([q,w,e,r,t,y], [e,q,r,t,w,y])
 %FALSE msort([q,w,e,r,t,y], [q,w,e,r,t,y])
 %FALSE msort([q,w,e,r,t,y], [e,q,t,r,w,y])
 
 %QUERY msort([q,w,e,r,t,y], [A,B,C,D,E,F])
 %ANSWER
 % A=e
 % B=q
 % C=r
 % D=t
 % E=w
 % F=y
 %ANSWER

 %QUERY msort([], X)
 %ANSWER X=[]

 %QUERY msort([a], X)
 %ANSWER X=[a]

 %FALSE msort(a, X)
 %FALSE msort([a,b,c|T], X)
 
 %QUERY msort([h,e,l,l,o], X)
 %ANSWER X=[e,h,l,l,o]
 
 %FALSE msort([h,e,l,l,o], [e,h,l,o])
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
public final class Sort extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term unsorted, Term sorted) {
      List<Term> elements = toSortedJavaUtilList(unsorted);
      if (elements == null) {
         return false;
      } else {
         return sorted.unify(createList(elements));
      }
   }
}
