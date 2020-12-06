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
package org.projog.core.function.list;

import static org.projog.core.function.AbstractSingletonPredicate.toPredicate;
import static org.projog.core.term.ListFactory.createListOfLength;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* TEST
 %QUERY length([],X)
 %ANSWER X=0
 %QUERY length([a],X)
 %ANSWER X=1
 %QUERY length([a,b],X)
 %ANSWER X=2
 %QUERY length([a,b,c],X)
 %ANSWER X=3

 %FALSE length([a,b|c],X)
 %FALSE length([a,b],1)
 %FALSE length([a,b],3)
 %FALSE length(abc,3)

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
   public Predicate getPredicate(final Term list, final Term expectedLength) {
      boolean firstArgIsVariable = list.getType().isVariable();
      boolean secondArgIsVariable = expectedLength.getType().isVariable();

      if (firstArgIsVariable && secondArgIsVariable) {
         return new Retryable(list, expectedLength);
      } else if (firstArgIsVariable) {
         final int length = TermUtils.toInt(expectedLength);
         return toPredicate(list.unify(createListOfLength(length)));
      } else {
         return toPredicate(checkLength(list, expectedLength));
      }
   }

   private boolean checkLength(final Term list, final Term expectedLength) {
      final java.util.List<Term> javaList = toJavaUtilList(list);
      if (javaList != null) {
         final IntegerNumber actualLength = IntegerNumberCache.valueOf(javaList.size());
         return expectedLength.unify(actualLength);
      } else {
         return false;
      }
   }

   private static class Retryable implements Predicate {
      int i = 0;
      final Term list;
      final Term length;

      private Retryable(Term list, Term length) {
         this.list = list;
         this.length = length;
      }

      @Override
      public boolean evaluate() {
         list.backtrack();
         length.backtrack();
         list.unify(createListOfLength(i));
         length.unify(IntegerNumberCache.valueOf(i));
         i++;
         return true;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return true;
      }
   }
}
