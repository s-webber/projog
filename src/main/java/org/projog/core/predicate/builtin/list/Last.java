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
 %QUERY last([a,b,c], X)
 %ANSWER X=c

 %QUERY last([q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,m], X)
 %ANSWER X=m

 %QUERY last([a], X)
 %ANSWER X=a

 %FALSE last([a,b,c], a)
 %FALSE last([a,b,c], b)
 %TRUE last([a,b,c], c)
 %FALSE last([a,b,c], d)

 %FALSE last([a,b|c], X)
 %TRUE last([a,b|[]], b)

 %FALSE last([], X)
 %FALSE last(a, X)

 %QUERY last(Y, X)
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [X]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [_,X]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [_,_,X]
 %ANSWER
 %ANSWER
 % X = UNINSTANTIATED VARIABLE
 % Y = [_,_,_,X]
 %ANSWER
 %QUIT

 %QUERY Z=[a,b|Tail],last(Z,Last)
 %ANSWER
 % Last = b
 % Tail = []
 % Z = [a,b]
 %ANSWER
 %ANSWER
 % Last = UNINSTANTIATED VARIABLE
 % Tail = [Last]
 % Z = [a,b,Last]
 %ANSWER
 %ANSWER
 % Last = UNINSTANTIATED VARIABLE
 % Tail = [_,Last]
 % Z = [a,b,_,Last]
 %ANSWER
 %ANSWER
 % Last = UNINSTANTIATED VARIABLE
 % Tail = [_,_,Last]
 % Z = [a,b,_,_,Last]
 %ANSWER
 %QUIT

 %QUERY Z=[a,b|Tail],last(Z,a)
 %ANSWER
 % Tail = [a]
 % Z = [a,b,a]
 %ANSWER
 %ANSWER
 % Tail = [_,a]
 % Z = [a,b,_,a]
 %ANSWER
 %ANSWER
 % Tail = [_,_,a]
 % Z = [a,b,_,_,a]
 %ANSWER
 %ANSWER
 % Tail = [_,_,_,a]
 % Z = [a,b,_,_,_,a]
 %ANSWER
 %QUIT

 %QUERY Z=[a,b|Tail],last(Z,b)
 %ANSWER
 % Tail = []
 % Z = [a,b]
 %ANSWER
 %ANSWER
 % Tail = [b]
 % Z = [a,b,b]
 %ANSWER
 %ANSWER
 % Tail = [_,b]
 % Z = [a,b,_,b]
 %ANSWER
 %ANSWER
 % Tail = [_,_,b]
 % Z = [a,b,_,_,b]
 %ANSWER
 %QUIT

 %QUERY Z=[a,b|Tail],last(Z,z)
 %ANSWER
 % Tail = [z]
 % Z = [a,b,z]
 %ANSWER
 %ANSWER
 % Tail = [_,z]
 % Z = [a,b,_,z]
 %ANSWER
 %ANSWER
 % Tail = [_,_,z]
 % Z = [a,b,_,_,z]
 %ANSWER
 %ANSWER
 % Tail = [_,_,_,z]
 % Z = [a,b,_,_,_,z]
 %ANSWER
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
               newHead = new Variable("_");
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
         newHead = new Variable("_");

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
