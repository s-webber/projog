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
package org.projog.core.predicate.builtin.compound;

import java.util.Arrays;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseConsumer;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/* TEST
%TRUE call(true)
%FAIL call(fail)
%?- X = true, call(X)
% X=true
%FAIL X = fail, call(X)

test(a).
test(b).
test(c).

%?- X = test(Y), call(X)
% X=test(a)
% Y=a
% X=test(b)
% Y=b
% X=test(c)
% Y=c

testCall(X) :- call(X).

%FAIL testCall(fail)
%TRUE testCall(true)
%?- testCall((true ; true))
%YES
%YES

% Note: "time" is a synonym for "call".
%TRUE time(true)
%FAIL time(fail)
%?- time(repeat(3))
%YES
%YES
%YES

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

%?- call(test(1,2,3,4,5,6,7,8,9))
%OUTPUT 1 2 3 4 5 6 7 8 9
%YES

%?- call(test(1,2,3,4,5,6,7,8), q)
%OUTPUT 1 2 3 4 5 6 7 8 q
%YES

%?- call(test(1,2,3,4,5,6,7), q, w)
%OUTPUT 1 2 3 4 5 6 7 q w
%YES

%?- call(test(1,2,3,4,5,6), q, w, e)
%OUTPUT 1 2 3 4 5 6 q w e
%YES

%?- call(test(1,2,3,4,5), q, w, e, r)
%OUTPUT 1 2 3 4 5 q w e r
%YES

%?- call(test(1,2,3,4), q, w, e, r, t)
%OUTPUT 1 2 3 4 q w e r t
%YES

%?- call(test(1,2,3), q, w, e, r, t, y)
%OUTPUT 1 2 3 q w e r t y
%YES

%?- call(test(1,2), q, w, e, r, t, y, u)
%OUTPUT 1 2 q w e r t y u
%YES

%?- call(test(1), q, w, e, r, t, y, u, i)
%OUTPUT 1 q w e r t y u i
%YES

%?- call(test, q, w, e, r, t, y, u, i, o)
%OUTPUT q w e r t y u i o
%YES
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
public final class Call implements PredicateFactory, KnowledgeBaseConsumer {
   private KnowledgeBase knowledgeBase;

   @Override
   public Predicate getPredicate(Term[] args) {
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

   private Predicate getPredicate(Term arg) {
      return knowledgeBase.getPredicates().getPredicate(arg);
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
   }
}
