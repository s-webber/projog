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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.List;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
 %TRUE nth0(0, [a,b,c], a)
 %TRUE nth1(1, [a,b,c], a)
 %TRUE nth0(1, [a,b,c], b)
 %TRUE nth1(2, [a,b,c], b)
 %TRUE nth0(2, [a,b,c], c)
 %TRUE nth1(3, [a,b,c], c)

 %FALSE nth0(-1, [a,b,c], a)
 %FALSE nth0(1, [a,b,c], a)
 %FALSE nth0(5, [a,b,c], a)

 %QUERY nth0(0, [a,b,c], X)
 %ANSWER X=a
 %QUERY nth0(1, [a,b,c], X)
 %ANSWER X=b
 %QUERY nth0(2, [a,b,c], X)
 %ANSWER X=c

 %FALSE nth0(-1, [a,b,c], X)
 %FALSE nth0(3, [a,b,c], X)

 %QUERY nth0(X, [h,e,l,l,o], e)
 %ANSWER X=1
 %NO
 %QUERY nth0(X, [h,e,l,l,o], l)
 %ANSWER X=2
 %ANSWER X=3
 %NO
 %FALSE nth0(X, [h,e,l,l,o], z)

 %QUERY nth0(X, [h,e,l,l,o], Y)
 %ANSWER
 % X=0
 % Y=h
 %ANSWER
 %ANSWER
 % X=1
 % Y=e
 %ANSWER
 %ANSWER
 % X=2
 % Y=l
 %ANSWER
 %ANSWER
 % X=3
 % Y=l
 %ANSWER
 %ANSWER
 % X=4
 % Y=o
 %ANSWER

 %FALSE nth1(0, [a,b,c], a)
 %FALSE nth1(2, [a,b,c], a)
 %FALSE nth1(4, [a,b,c], a)

 %QUERY nth1(1, [a,b,c], X)
 %ANSWER X=a
 %QUERY nth1(2, [a,b,c], X)
 %ANSWER X=b
 %QUERY nth1(3, [a,b,c], X)
 %ANSWER X=c

 %FALSE nth1(-1, [a,b,c], X)
 %FALSE nth1(0, [a,b,c], X)
 %FALSE nth1(4, [a,b,c], X)

 %QUERY nth1(X, [h,e,l,l,o], e)
 %ANSWER X=2
 %NO
 %QUERY nth1(X, [h,e,l,l,o], l)
 %ANSWER X=3
 %ANSWER X=4
 %NO
 %FALSE nth1(X, [h,e,l,l,o], z)

 %QUERY nth1(X, [h,e,l,l,o], Y)
 %ANSWER
 % X=1
 % Y=h
 %ANSWER
 %ANSWER
 % X=2
 % Y=e
 %ANSWER
 %ANSWER
 % X=3
 % Y=l
 %ANSWER
 %ANSWER
 % X=4
 % Y=l
 %ANSWER
 %ANSWER
 % X=5
 % Y=o
 %ANSWER

 % Note: "nth" is a synonym for "nth1".
 %TRUE nth(2, [a,b,c], b)

 %FALSE nth0(1, [h,e,l,l,o|Y], l)
 %FALSE nth1(1, [h,e,l,l,o|Y], l)

 %QUERY nth0(X, [h,e,l,l,o|Y], l)
 %ANSWER
 % X = 2
 % Y = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 3
 % Y = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 5
 % Y = [l|_5]
 %ANSWER
 %ANSWER
 % X = 6
 % Y = [_6,l|_5]
 %ANSWER
 %ANSWER
 % X = 7
 % Y = [_7,_6,l|_5]
 %ANSWER
 %QUIT

 %QUERY nth1(X, [h,e,l,l,o|Y], l)
 %ANSWER
 % X = 3
 % Y = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 4
 % Y = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 6
 % Y = [l|_6]
 %ANSWER
 %ANSWER
 % X = 7
 % Y = [_7,l|_6]
 %ANSWER
 %ANSWER
 % X = 8
 % Y = [_8,_7,l|_6]
 %ANSWER
 %QUIT

 %QUERY nth0(8,[a,b,c|X],Y)
 %ANSWER
 % X = [E4,E3,E2,E1,E0,Y|T]
 % Y = UNINSTANTIATED VARIABLE
 %ANSWER

 %QUERY nth1(8,[a,b,c|X],Y)
 %ANSWER
 % X = [E3,E2,E1,E0,Y|T]
 % Y = UNINSTANTIATED VARIABLE
 %ANSWER

 %QUERY nth0(X,[a,b,c|Y],Z)
 %ANSWER
 % X = 0
 % Y = UNINSTANTIATED VARIABLE
 % Z = a
 %ANSWER
 %ANSWER
 % X = 1
 % Y = UNINSTANTIATED VARIABLE
 % Z = b
 %ANSWER
 %ANSWER
 % X = 2
 % Y = UNINSTANTIATED VARIABLE
 % Z = c
 %ANSWER
 %ANSWER
 % X = 3
 % Y = [Z|_3]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 4
 % Y = [_4,Z|_3]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 5
 % Y = [_5,_4,Z|_3]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %QUIT

 %QUERY nth1(X,[a,b,c|Y],Z)
 %ANSWER
 % X = 1
 % Y = UNINSTANTIATED VARIABLE
 % Z = a
 %ANSWER
 %ANSWER
 % X = 2
 % Y = UNINSTANTIATED VARIABLE
 % Z = b
 %ANSWER
 %ANSWER
 % X = 3
 % Y = UNINSTANTIATED VARIABLE
 % Z = c
 %ANSWER
 %ANSWER
 % X = 4
 % Y = [Z|_4]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 5
 % Y = [_5,Z|_4]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 6
 % Y = [_6,_5,Z|_4]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %QUIT

 %QUERY nth0(X,Y,Z)
 %ANSWER
 % X = 0
 % Y = [Z|_0]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 1
 % Y = [_1,Z|_0]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 2
 % Y = [_2,_1,Z|_0]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %QUIT

 %QUERY nth1(X,Y,Z)
 %ANSWER
 % X = 1
 % Y = [Z|_1]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 2
 % Y = [_2,Z|_1]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X = 3
 % Y = [_3,_2,Z|_1]
 % Z = UNINSTANTIATED VARIABLE
 %ANSWER
 %QUIT
 */
/**
 * <code>nth0(X,Y,Z)</code> / <code>nth1(X,Y,Z)</code> - examines an element of a list.
 * <p>
 * Indexing starts at 0 when using <code>nth0</code>. Indexing starts at 1 when using <code>nth1</code>.
 * </p>
 */
public final class Nth extends AbstractPredicateFactory {
   public static Nth nth0() {
      return new Nth(0);
   }

   public static Nth nth1() {
      return new Nth(1);
   }

   private final int startingIdx;

   private Nth(int startingIdx) {
      this.startingIdx = startingIdx;
   }

   @Override
   protected Predicate getPredicate(Term index, Term list, Term element) {
      if (index.getType().isVariable()) {
         return new Retryable(index, list, element);
      } else {
         boolean result = evaluate(toInt(index), list, element);
         return PredicateUtils.toPredicate(result);
      }
   }

   private boolean evaluate(int index, Term list, Term element) {
      Term current = list;
      int requiredIdx = index - startingIdx;
      int currentIdx = 0;
      while (current.getType() == TermType.LIST) {
         if (currentIdx == requiredIdx) {
            return element.unify(current.getArgument(0));
         }
         current = current.getArgument(1);
         currentIdx++;
      }

      if (current == EmptyList.EMPTY_LIST) {
         return false;
      } else if (current.getType().isVariable()) {
         int requiredLength = requiredIdx - currentIdx;
         if (requiredLength > 0) {
            Term t = new List(element, new Variable("T"));
            for (int i = 0; i < requiredLength; i++) {
               t = new List(new Variable("E" + i), t);
            }
            current.unify(t);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private class Retryable implements Predicate {
      final Term index;
      Term list;
      final Term element;
      int ctr;

      Retryable(Term index, Term list, Term element) {
         this.index = index;
         this.list = list;
         this.element = element;
         this.ctr = startingIdx;
      }

      @Override
      public boolean evaluate() {
         while (list.getType() == TermType.LIST) {
            Term oldList = list.getTerm();
            backtrack(index, list, element);
            if (list.getType().isVariable()) {
               Term newList = new List(new Variable("_" + ctr), oldList);
               list.unify(newList);
               index.unify(IntegerNumberCache.valueOf(ctr++));
               return true;
            }

            Term head = list.getArgument(0);
            list = list.getArgument(1);

            if (element.unify(head)) {
               index.unify(IntegerNumberCache.valueOf(ctr++));
               return true;
            } else {
               ctr++;
            }
         }

         if (list.getType().isVariable()) {
            backtrack(index, list, element);

            Variable tail = new Variable("_" + ctr);
            Term newList = new List(element, tail);
            list.unify(newList);
            index.unify(IntegerNumberCache.valueOf(ctr++));
            return true;
         }

         return false;
      }

      //TODO add to TermUtils (plus 1 and 2 args versions)
      private void backtrack(Term index, Term list, Term element) {
         index.backtrack();
         list.backtrack();
         element.backtrack();
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return list.getType() == TermType.LIST || list.getType().isVariable();
      }
   }
}
