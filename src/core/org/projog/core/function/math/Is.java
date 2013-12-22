/*
 * Copyright 2013 S Webber
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
package org.projog.core.function.math;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %QUERY% X is 3
 % %ANSWER% X=3
 % %QUERY% X is 3+2
 % %ANSWER% X=5
 % %QUERY% X is 3.5+2.25
 % %ANSWER% X=5.75
 % %TRUE% 5 is 5
 % %FALSE% 5 is 6
 % %TRUE% 5 is 4+1
 % %FALSE% 5 is 4+2
 
 % %QUERY% X is Y
 % %EXCEPTION% Can't get Numeric for term: Y of type: NAMED_VARIABLE

 % %QUERY% Z=1+1, Y=9-Z, X is Y
 % %ANSWER%
 % X=7
 % Y=9 - (1 + 1)
 % Z = 1 + 1
 % %ANSWER%

 % %QUERY% X is _
 % %EXCEPTION% Can't get Numeric for term: _ of type: ANONYMOUS_VARIABLE

 % %QUERY% X is sum(1,2)
 % %EXCEPTION% Cannot find calculatable: sum

 % %QUERY% X is ten
 % %EXCEPTION% Cannot find calculatable: ten

 % %QUERY% X is []
 % %EXCEPTION% Can't get Numeric for term: [] of type: EMPTY_LIST

 % %QUERY% X is [1,2,3]
 % %EXCEPTION% Can't get Numeric for term: .(1, .(2, .(3, []))) of type: LIST
 */
/**
 * <code>X is Y</code> - evaluate arithmetic expression.
 * <p>
 * Firstly structure <code>Y</code> is evaluated as an arithmetic expression to give a number. Secondly an attempt is
 * made to match the number to <code>X</code>. The goal succeeds or fails based on the match.
 * </p>
 */
public final class Is extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      Numeric n = getKnowledgeBase().getNumeric(arg2);
      return arg1.unify(n);
   }
}