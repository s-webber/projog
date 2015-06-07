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

import static org.projog.core.term.ListUtils.isMember;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE memberchk(a, [a,b,c])
 %TRUE memberchk(b, [a,b,c])
 %TRUE memberchk(c, [a,b,c])

 %FALSE memberchk(d, [a,b,c])
 %FALSE memberchk(d, [])
 %FALSE memberchk([], [])
 
 %QUERY memberchk(X, [a,b,c])
 %ANSWER X=a
 
 %QUERY memberchk(p(X,b), [p(a,b), p(z,Y), p(x(Y), Y)])
 %ANSWER 
 % X=a
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
*/
/**
 * <code>memberchk(E, L)</code> - checks is a term is a member of a list.
 * <p>
 * <code>memberchk(E, L)</code> succeeds if <code>E</code> is a member of the list <code>L</code>. No attempt is made to
 * retry the goal during backtracking - so if <code>E</code> appears multiple times in <code>L</code> only the first
 * occurrence will be matched.
 * </p>
 */
public final class MemberCheck extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term element, Term list) {
      return isMember(element, list);
   }
}
