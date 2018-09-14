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

import static org.projog.core.term.TermComparator.TERM_COMPARATOR;

import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;

/* TEST
 z(r).
 z(t).
 z(y).

 x(a,b,c).
 x(q,X,e) :- z(X).
 x(1,2,3).
 x(w,b,c).
 x(d,b,c).
 x(a,b,c).

 %QUERY setof(X,x(X,Y,Z),L)
 %ANSWER
 % L=[a,d,w]
 % X=UNINSTANTIATED VARIABLE
 % Y=b
 % Z=c
 %ANSWER
 %ANSWER
 % L=[q]
 % X=UNINSTANTIATED VARIABLE
 % Y=r
 % Z=e
 %ANSWER
 %ANSWER
 % L=[q]
 % X=UNINSTANTIATED VARIABLE
 % Y=t
 % Z=e
 %ANSWER
 %ANSWER
 % L=[q]
 % X=UNINSTANTIATED VARIABLE
 % Y=y
 % Z=e
 %ANSWER
 %ANSWER
 % L=[1]
 % X=UNINSTANTIATED VARIABLE
 % Y=2
 % Z=3
 %ANSWER

 %FALSE setof(X,x(X,y,z),L)

 %QUERY setof(Y, (member(X,[6,3,7,2,5,4,3]), X<4, Y is X*X), L)
 %ANSWER
 % L=[9]
 % X=3
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % L=[4]
 % X=2
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 */
/**
 * <code>setof(X,P,L)</code> - find all solutions that satisfy the goal.
 * <p>
 * <code>setof(X,P,L)</code> produces a list (<code>L</code>) of <code>X</code> for each possible solution of the goal
 * <code>P</code>. If <code>P</code> contains uninstantiated variables, other than <code>X</code>, it is possible that
 * <code>setof</code> can be successfully evaluated multiple times - for each possible values of the uninstantiated
 * variables. The elements in <code>L</code> will appear in sorted order and will not include duplicates. Fails if
 * <code>P</code> has no solutions.
 */
public final class SetOf extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term template, Term goal, Term bag) {
      return new SetOfPredicate(template, goal, bag, getKnowledgeBase());
   }

   private final class SetOfPredicate extends AbstractCollectionOf {
      private SetOfPredicate(Term template, Term goal, Term bag, KnowledgeBase kb) {
         super(template, goal, bag, kb);
      }

      /** "setof" excludes duplicates and orders elements using {@link TermComparator}. */
      @Override
      protected void add(List<Term> list, Term newTerm) {
         final int numberOfElements = list.size();
         for (int i = 0; i < numberOfElements; i++) {
            final Term next = list.get(i);
            final int comparison = TERM_COMPARATOR.compare(newTerm, next);
            if (comparison < 0) {
               // found correct position - so add
               list.add(i, newTerm);
               return;
            } else if (comparison == 0 && newTerm.strictEquality(next)) {
               // duplicate - so ignore
               return;
            }
         }
         list.add(newTerm);
      }
   }
}
