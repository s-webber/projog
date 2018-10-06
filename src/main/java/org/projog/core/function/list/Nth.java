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
package org.projog.core.function.list;

import static org.projog.core.term.ListUtils.toJavaUtilList;
import static org.projog.core.term.TermUtils.toInt;

import java.util.Collections;
import java.util.List;

import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

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
   public Predicate getPredicate(Term index, Term list, Term element) {
      if (index.getType().isVariable()) {
         return new Retryable(index, list, element, toJavaUtilList(list));
      } else {
         boolean result = evaluate(index, list, element);
         return AbstractSingletonPredicate.toPredicate(result);
      }
   }

   private boolean evaluate(Term index, Term list, Term element) {
      List<Term> l = toJavaUtilList(list);
      if (l == null) {
         return false;
      }

      int i = toInt(index);
      int idx = i - startingIdx;
      if (isValidIndex(l, idx)) {
         return element.unify(l.get(idx));
      } else {
         return false;
      }
   }

   private boolean isValidIndex(List<Term> l, int idx) {
      return idx > -1 && idx < l.size();
   }

   private class Retryable implements Predicate {
      final Term index;
      final Term list;
      final Term element;
      final List<Term> javaUtilList;
      int ctr;

      @SuppressWarnings("unchecked")
      Retryable(Term index, Term list, Term element, List<Term> javaUtilList) {
         this.index = index;
         this.list = list;
         this.element = element;
         this.javaUtilList = javaUtilList == null ? Collections.EMPTY_LIST : javaUtilList;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            backtrack(index, list, element);
            Term t = javaUtilList.get(ctr);
            IntegerNumber n = new IntegerNumber(ctr + startingIdx);
            ctr++;
            if (index.unify(n) && element.unify(t)) {
               return true;
            }
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
         return ctr < javaUtilList.size();
      }
   }
}
