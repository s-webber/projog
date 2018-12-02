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
package org.projog.core.function.list;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

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

 % Note: in some Prolog implementations this query would fail rather than cause an error.
 %QUERY last(a, X)
 %ERROR Expected list but got: a of type: ATOM

 % Note: in some Prolog implementations this query would cause Y to be unified with a list with X as the tail.
 %QUERY last(Y, X)
 %ERROR Expected list but got: Y of type: NAMED_VARIABLE
 */
/**
 * <code>last(X,Y)</code> - finds the last element of a list.
 */
public final class Last extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term list, Term termToUnifyLastElementWith) {
      switch (list.getType()) {
         case LIST:
            return unifyLastElementOfList(list, termToUnifyLastElementWith);
         case EMPTY_LIST:
            return false;
         default:
            throw new ProjogException("Expected list but got: " + list + " of type: " + list.getType());
      }
   }

   private boolean unifyLastElementOfList(Term list, Term termToUnifyLastElementWith) {
      Term lastElement;
      do {
         lastElement = list.getArgument(0);
         list = list.getArgument(1);
      } while (list.getType() == TermType.LIST);

      if (list.getType() == TermType.EMPTY_LIST) {
         return termToUnifyLastElementWith.unify(lastElement);
      } else {
         return false; // return false if a partial list
      }
   }
}
