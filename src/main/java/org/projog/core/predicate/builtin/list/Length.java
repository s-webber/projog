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

import static org.projog.core.predicate.udp.PredicateUtils.toPredicate;
import static org.projog.core.term.ListFactory.createListOfLength;
import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %QUERY length([],X)
 %ANSWER X=0
 %QUERY length([a],X)
 %ANSWER X=1
 %QUERY length([a,b],X)
 %ANSWER X=2
 %QUERY length([a,b,c],X)
 %ANSWER X=3

 %FALSE length([a,b],1)
 %FALSE length([a,b],3)

 %QUERY length(X,0)
 %ANSWER X=[]

 %QUERY length(X,1)
 %ANSWER X=[E0]

 %QUERY length(X,3)
 %ANSWER X=[E0,E1,E2]

 %QUERY length(X,Y)
 %ANSWER
 % X=[]
 % Y=0
 %ANSWER
 %ANSWER
 % X=[E0]
 % Y=1
 %ANSWER
 %ANSWER
 % X=[E0,E1]
 % Y=2
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2]
 % Y=3
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3]
 % Y=4
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3,E4]
 % Y=5
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3,E4,E5]
 % Y=6
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3,E4,E5,E6]
 % Y=7
 %ANSWER
 %QUIT

 %QUERY length([a,b|X],Y)
 %ANSWER
 % X=[]
 % Y=2
 %ANSWER
 %ANSWER
 % X=[E0]
 % Y=3
 %ANSWER
 %ANSWER
 % X=[E0,E1]
 % Y=4
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2]
 % Y=5
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3]
 % Y=6
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3,E4]
 % Y=7
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3,E4,E5]
 % Y=8
 %ANSWER
 %ANSWER
 % X=[E0,E1,E2,E3,E4,E5,E6]
 % Y=9
 %ANSWER
 %QUIT

 % TODO fix documentation generator to handle QUIT

 %QUERY length([a,b|X],8)
 %ANSWER X=[E0,E1,E2,E3,E4,E5]
 %QUERY length([a,b|X],3)
 %ANSWER X=[E0]
 %QUERY length([a,b|X],2)
 %ANSWER X=[]
 %FALSE length([a,b|X],1)

 %FALSE length([a,b,c],a)

 %FALSE length(X,X)
 %FALSE length([a,b,c|X],X)

 %QUERY length(abc,X)
 %ERROR Expected list but got: ATOM with value: abc
 %QUERY length([a,b|c],X)
 %ERROR Expected list but got: LIST with value: .(a, .(b, c))
 %QUERY length([a,b|X],z)
 %ERROR Expected Numeric but got: ATOM with value: z
 */
/**
 * <code>length(X,Y)</code> - determines the length of a list.
 * <p>
 * The <code>length(X,Y)</code> goal succeeds if the number of elements in the list <code>X</code> matches the integer
 * value <code>Y</code>.
 * </p>
 */
public final class Length extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(final Term list, final Term expectedLength) {
      int actualLength = 0;
      Term tail = list;
      while (tail.getType() == TermType.LIST) {
         actualLength++;
         tail = tail.getArgument(1);
      }

      if (tail == EmptyList.EMPTY_LIST) {
         return toPredicate(expectedLength.unify(IntegerNumberCache.valueOf(actualLength)));
      } else if (!tail.getType().isVariable()) {
         throw new ProjogException("Expected list but got: " + list.getType() + " with value: " + list);
      } else if (expectedLength.getType().isVariable()) {
         return new Retryable(actualLength, tail, expectedLength);
      } else {
         int requiredLength = toInt(expectedLength) - actualLength;
         return toPredicate(requiredLength > -1 && tail.unify(createListOfLength(requiredLength)));
      }
   }

   private static class Retryable implements Predicate {
      final int startLength;
      final Term list;
      final Term length;
      int currentLength = 0;

      private Retryable(int startLength, Term list, Term length) {
         this.startLength = startLength;
         this.list = list;
         this.length = length;
      }

      @Override
      public boolean evaluate() {
         list.backtrack();
         length.backtrack();
         if (!list.unify(createListOfLength(currentLength))) {
            return false;
         }
         if (!length.unify(IntegerNumberCache.valueOf(startLength + currentLength))) {
            return false;
         }
         currentLength++;
         return true;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return true;
      }
   }
}
