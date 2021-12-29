/*
 * Copyright 2018 S. Webber
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

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.EmptyList;
import org.projog.core.term.List;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
%?- last([a,b,c], X)
% X=c

%?- last([q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,m], X)
% X=m

%?- last([a], X)
% X=a

%FAIL last([a,b,c], a)
%FAIL last([a,b,c], b)
%TRUE last([a,b,c], c)
%FAIL last([a,b,c], d)

%FAIL last([a,b|c], X)
%TRUE last([a,b|[]], b)

%FAIL last([], X)
%FAIL last(a, X)

%?- last(Y, X)
% X=UNINSTANTIATED VARIABLE
% Y=[X]
% X=UNINSTANTIATED VARIABLE
% Y=[_,X]
% X=UNINSTANTIATED VARIABLE
% Y=[_,_,X]
% X=UNINSTANTIATED VARIABLE
% Y=[_,_,_,X]
%QUIT

%?- Z=[a,b|Tail],last(Z,Last)
% Last=b
% Tail=[]
% Z=[a,b]
% Last=UNINSTANTIATED VARIABLE
% Tail=[Last]
% Z=[a,b,Last]
% Last=UNINSTANTIATED VARIABLE
% Tail=[_,Last]
% Z=[a,b,_,Last]
% Last=UNINSTANTIATED VARIABLE
% Tail=[_,_,Last]
% Z=[a,b,_,_,Last]
%QUIT

%?- Z=[a,b|Tail],last(Z,a)
% Tail=[a]
% Z=[a,b,a]
% Tail=[_,a]
% Z=[a,b,_,a]
% Tail=[_,_,a]
% Z=[a,b,_,_,a]
% Tail=[_,_,_,a]
% Z=[a,b,_,_,_,a]
%QUIT

%?- Z=[a,b|Tail],last(Z,b)
% Tail=[]
% Z=[a,b]
% Tail=[b]
% Z=[a,b,b]
% Tail=[_,b]
% Z=[a,b,_,b]
% Tail=[_,_,b]
% Z=[a,b,_,_,b]
%QUIT

%?- Z=[a,b|Tail],last(Z,z)
% Tail=[z]
% Z=[a,b,z]
% Tail=[_,z]
% Z=[a,b,_,z]
% Tail=[_,_,z]
% Z=[a,b,_,_,z]
% Tail=[_,_,_,z]
% Z=[a,b,_,_,_,z]
%QUIT
*/
/**
 * <code>last(X,Y)</code> - finds the last element of a list.
 */
public final class Last extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term list, Term termToUnifyLastElementWith) {
      Term tail = list;
      Term last = list;
      while (tail.getType() == TermType.LIST) {
         last = tail;
         tail = tail.getArgument(1);
      }

      if (list != tail && tail.getType() == TermType.EMPTY_LIST) {
         // first arg is a ground list
         return PredicateUtils.toPredicate(termToUnifyLastElementWith.unify(last.getArgument(0)));
      } else if (tail.getType().isVariable()) {
         // first arg is a variable or a list with a variable at the tail
         return new LastPredicate(last, tail, termToUnifyLastElementWith);
      } else {
         // first arg is a list whose tail is not an empty list or variable
         return PredicateUtils.FALSE;
      }
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   private static final class LastPredicate implements Predicate {
      Term last;
      Term tail;
      Term termToUnifyLastElementWith;
      Term newHead;
      boolean retry;

      LastPredicate(Term last, Term tail, Term termToUnifyLastElementWith) {
         this.last = last;
         this.tail = tail;
         this.termToUnifyLastElementWith = termToUnifyLastElementWith;
         this.newHead = termToUnifyLastElementWith;
      }

      @Override
      public boolean evaluate() {
         if (!retry) {
            if (last.getType().isVariable()) {
               last.unify(new List(termToUnifyLastElementWith, EmptyList.EMPTY_LIST));
               newHead = new Variable();
               retry = true;
               return true;
            }
            tail.unify(EmptyList.EMPTY_LIST);
            retry = true;
            if (termToUnifyLastElementWith.unify(last.getArgument(0))) {
               return true;
            }
         }

         Term newLast = new List(newHead, tail.getTerm());
         newHead = new Variable();

         termToUnifyLastElementWith.backtrack();
         tail.backtrack();
         tail.unify(newLast);

         return true;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return true;
      }
   }
}
