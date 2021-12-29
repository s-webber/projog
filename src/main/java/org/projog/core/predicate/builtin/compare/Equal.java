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
package org.projog.core.predicate.builtin.compare;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;

/* TEST
%TRUE a=a
%FAIL a=b
%?- a=X
% X=a
%FAIL 2=1+1
%FAIL p(b,c)=p(b,d)
%FAIL p(b,c)=p(c,b)
%?- p(b,c)=p(b,X)
% X=c
%?- p(Y,c)=p(b,X)
% Y=b
% X=c
%TRUE [a,b,c]=[a,b,c]
%FAIL [a,b,c]=[a,b,d]

%?- [a,b,c]=[X|Y]
% X=a
% Y=[b,c]
%?- [X|[b]]=[a,b]
% X=a
%?- [a,b,c|X]=[a,b,c,d,e,f,g]
% X=[d,e,f,g]
%TRUE [a,b,c]=[a,b,c|[]]
%FAIL [a,b,c]=[X|[]]
*/
/**
 * <code>X=Y</code> - an equality test.
 * <p>
 * If <code>X</code> can be matched with <code>Y</code> the goal succeeds else the goal fails. A <code>X=Y</code> goal
 * will consider an uninstantiated variable to be equal to anything. A <code>X=Y</code> goal will always succeed if
 * either argument is uninstantiated.
 * </p>
 */
public final class Equal extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      return arg1.unify(arg2);
   }
}
