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

import java.util.Arrays;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Structure;
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

 test(V1,V2,V3,V4,V5,V6,V7,V8,V9) :-
   write(V1), write(' '),
   write(V2), write(' '),
   write(V3), write(' '),
   write(V4), write(' '),
   write(V5), write(' '),
   write(V6), write(' '),
   write(V7), write(' '),
   write(V8), write(' '),
   write(V9).

%QUERY call(test(1,2,3,4,5,6,7,8,9))
%OUTPUT 1 2 3 4 5 6 7 8 9
%ANSWER/

%QUERY call(test(1,2,3,4,5,6,7,8), q)
%OUTPUT 1 2 3 4 5 6 7 8 q
%ANSWER/

%QUERY call(test(1,2,3,4,5,6,7), q, w)
%OUTPUT 1 2 3 4 5 6 7 q w
%ANSWER/

%QUERY call(test(1,2,3,4,5,6), q, w, e)
%OUTPUT 1 2 3 4 5 6 q w e
%ANSWER/

%QUERY call(test(1,2,3,4,5), q, w, e, r)
%OUTPUT 1 2 3 4 5 q w e r
%ANSWER/

%QUERY call(test(1,2,3,4), q, w, e, r, t)
%OUTPUT 1 2 3 4 q w e r t
%ANSWER/

%QUERY call(test(1,2,3), q, w, e, r, t, y)
%OUTPUT 1 2 3 q w e r t y
%ANSWER/

%QUERY call(test(1,2), q, w, e, r, t, y, u)
%OUTPUT 1 2 q w e r t y u
%ANSWER/

%QUERY call(test(1), q, w, e, r, t, y, u, i)
%OUTPUT 1 q w e r t y u i
%ANSWER/

%QUERY call(test, q, w, e, r, t, y, u, i, o)
%OUTPUT q w e r t y u i o
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
 * <p>
 * Projog also supports overloaded versions of <code>call</code> for which subsequent arguments are appended to the
 * argument list of the goal represented by the first argument.
 * </p>
 */
public final class Call implements PredicateFactory {
   private KnowledgeBase knowledgeBase;

   @Override
   public Predicate getPredicate(Term... args) {
      Term goal = args[0];
      if (args.length == 1) {
         return getPredicate(goal);
      } else {
         Term[] goalArgs = goal.getArgs();
         Term[] callArgs = Arrays.copyOf(goalArgs, goalArgs.length + args.length - 1);
         System.arraycopy(args, 1, callArgs, goalArgs.length, args.length - 1);
         Term target = Structure.createStructure(goal.getName(), callArgs);
         return getPredicate(target);
      }
   }

   public Predicate getPredicate(Term arg) {
      return KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), arg);
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
   }

   protected final KnowledgeBase getKnowledgeBase() {
      return knowledgeBase;
   }
}
