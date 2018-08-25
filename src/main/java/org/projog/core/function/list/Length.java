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

import static org.projog.core.term.ListFactory.createListOfLength;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* TEST
 %QUERY length([],X)
 %ANSWER X=0
 %QUERY length([a],X)
 %ANSWER X=1
 %QUERY length([a,b],X)
 %ANSWER X=2
 %QUERY length([a,b,c],X)
 %ANSWER X=3
 
 %FALSE length([a,b|c],X)
 %FALSE length([a,b],1)
 %FALSE length([a,b],3)
 %FALSE length(abc,3)
 
 %QUERY length(X,0)
 %ANSWER X=[]
 
 %QUERY length(X,1)
 %ANSWER X=[E0]
 
 %QUERY length(X,3)
 %ANSWER X=[E0,E1,E2]
 */
/**
 * <code>length(X,Y)</code> - determines the length of a list.
 * <p>
 * The <code>length(X,Y)</code> goal succeeds if the number of elements in the list <code>X</code> matches the integer
 * value <code>Y</code>.
 * </p>
 */
public final class Length extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(final Term list, final Term expectedLength) {
      if (list.getType().isVariable()) {
         final int length = TermUtils.toInt(expectedLength);
         return list.unify(createListOfLength(length));
      } else {
         return checkLength(list, expectedLength);
      }
   }

   private boolean checkLength(final Term list, final Term expectedLength) {
      final java.util.List<Term> javaList = toJavaUtilList(list);
      if (javaList != null) {
         final IntegerNumber actualLength = new IntegerNumber(javaList.size());
         return expectedLength.unify(actualLength);
      } else {
         return false;
      }
   }
}
