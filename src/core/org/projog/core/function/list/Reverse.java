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
import static org.projog.core.term.ListUtils.toJavaUtilList;
import static org.projog.core.term.TermType.EMPTY_LIST;
import static org.projog.core.term.TermType.LIST;

import java.util.Collections;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE reverse([a],[a])
 %TRUE reverse([a,b,c],[c,b,a])
 %QUERY reverse([a,b,c],X)
 %ANSWER X=[c,b,a]
 %QUERY reverse(X,[a,b,c])
 %ANSWER X=[c,b,a]

 %TRUE reverse([],[])
 %QUERY reverse([],X)
 %ANSWER X=[]
 %QUERY reverse([],X)
 %ANSWER X=[]

 %FALSE reverse([a,b,c],[])
 %FALSE reverse([a,b,c],[a,b,c])
 %FALSE reverse([a,b,c],[d,c,b,a])
 %FALSE reverse([a,b,c,d],[c,b,a])
 %FALSE reverse([a,b,c],'abc')
 %FALSE reverse('abc',X)
 %FALSE reverse([a,b|c],X)
 */
/**
 * <code>reverse(X,Y)</code> - reverses the order of elements in a list.
 * <p>
 * The <code>reverse(X,Y)</code> goal succeeds if the elements of list <code>X</code> are in reverse order of the
 * elements in list <code>Y</code>.
 * </p>
 */
public final class Reverse extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(final Term list1, final Term list2) {
      if (list1.getType() == LIST) {
         return evaluate(list2, reverse(list1));
      } else if (list2.getType() == LIST) {
         return evaluate(list1, reverse(list2));
      } else if (list1.getType() == EMPTY_LIST || list2.getType() == EMPTY_LIST) {
         return list1.unify(list2);
      } else {
         return false;
      }
   }

   private boolean evaluate(final Term term, final java.util.List<Term> javaList) {
      return javaList != null && term.unify(createList(javaList));
   }

   private java.util.List<Term> reverse(Term list) {
      final java.util.List<Term> javaList = toJavaUtilList(list);
      if (javaList != null) {
         Collections.reverse(javaList);
      }
      return javaList;
   }
}
