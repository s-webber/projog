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
package org.projog.core.function.compound;

import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;

/* TEST
 %TRUE call(true)
 %FALSE call(fail)
 %QUERY X = true, call(X)
 %ANSWER X = true
 %FALSE X = fail, call(X)

 test(a).
 test(b).
 test(c).

 %QUERY X = test(Y), call(X)
 %ANSWER
 % X = test(a)
 % Y = a
 %ANSWER
 %ANSWER
 % X = test(b)
 % Y = b
 %ANSWER
 %ANSWER
 % X = test(c)
 % Y = c
 %ANSWER

 testCall(X) :- call(X).

 %FALSE testCall(fail)
 %TRUE testCall(true)
 %QUERY testCall((true ; true))
 %ANSWER/
 %ANSWER/

 % Note: "time" is a synonym for "call".
 %TRUE time(true)
 %FALSE time(fail)
 %QUERY time(repeat(3))
 %ANSWER/
 %ANSWER/
 %ANSWER/
 */
/**
 * <code>call(X)</code> - calls the goal represented by a term.
 * <p>
 * The predicate <code>call</code> makes it possible to call goals that are determined at runtime rather than when a
 * program is written. <code>call(X)</code> succeeds if the goal represented by the term <code>X</code> succeeds.
 * <code>call(X)</code> fails if the goal represented by the term <code>X</code> fails. An attempt is made to retry the
 * goal during backtracking.
 * </p>
 */
public final class Call extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term arg) {
      return KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), arg);
   }
}
