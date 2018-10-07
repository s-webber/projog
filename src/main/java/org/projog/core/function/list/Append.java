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

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.projog.core.Predicate;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 % Examples of when all three terms are lists:
 %TRUE append([a,b,c], [d,e,f], [a,b,c,d,e,f])
 %TRUE append([a], [b,c,d,e,f], [a,b,c,d,e,f])
 %TRUE append([a,b,c,d,e], [f], [a,b,c,d,e,f])
 %TRUE append([a,b,c,d,e,f], [], [a,b,c,d,e,f])
 %TRUE append([], [a,b,c,d,e,f], [a,b,c,d,e,f])
 %TRUE append([], [], [])
 %FALSE append([a,b], [d,e,f], [a,b,c,d,e,f])
 %FALSE append([a,b,c], [e,f], [a,b,c,d,e,f])
 %QUERY append([W,b,c], [d,Y,f], [a,X,c,d,e,Z])
 %ANSWER
 % W=a
 % X=b
 % Y=e
 % Z=f
 %ANSWER

 % Examples of when first term is a variable:
 %QUERY append([a,b,c], X, [a,b,c,d,e,f])
 %ANSWER X=[d,e,f]
 %QUERY append([a,b,c,d,e], X, [a,b,c,d,e,f])
 %ANSWER X=[f]
 %QUERY append([a], X, [a,b,c,d,e,f])
 %ANSWER X=[b,c,d,e,f]
 %QUERY append([], X, [a,b,c,d,e,f])
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a,b,c,d,e,f], X, [a,b,c,d,e,f])
 %ANSWER X=[]

 % Examples of when second term is a variable:
 %QUERY append(X, [d,e,f], [a,b,c,d,e,f])
 %ANSWER X=[a,b,c]
 %QUERY append(X, [f], [a,b,c,d,e,f])
 %ANSWER X=[a,b,c,d,e]
 %QUERY append(X, [b,c,d,e,f], [a,b,c,d,e,f])
 %ANSWER X=[a]
 %QUERY append(X, [a,b,c,d,e,f], [a,b,c,d,e,f])
 %ANSWER X=[]
 %QUERY append(X, [], [a,b,c,d,e,f])
 %ANSWER X=[a,b,c,d,e,f]

 % Examples of when third term is a variable:
 %QUERY append([a,b,c], [d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a], [b,c,d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a,b,c,d,e], [f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a,b,c,d,e,f], [], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([], [a,b,c,d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([], [], X)
 %ANSWER X=[]

 % Examples of when first and second terms are variables:
 %QUERY append(X, Y, [a,b,c,d,e,f])
 %ANSWER
 % X=[]
 % Y=[a,b,c,d,e,f]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[b,c,d,e,f]
 %ANSWER
 %ANSWER
 % X=[a,b]
 % Y=[c,d,e,f]
 %ANSWER
 %ANSWER
 % X=[a,b,c]
 % Y=[d,e,f]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d]
 % Y=[e,f]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,e]
 % Y=[f]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,e,f]
 % Y=[]
 %ANSWER
 %QUERY append(X, Y, [a])
 %ANSWER
 % X=[]
 % Y=[a]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[]
 %ANSWER
 %QUERY append(X, Y, [])
 %ANSWER
 % X=[]
 % Y=[]
 %ANSWER

 % Examples when combination of term types cause failure:
 %QUERY append(X, Y, Z)
 %ERROR Expected list but got: NAMED_VARIABLE
 %FALSE append(X, [], Z)
 %FALSE append(a, b, Z)
 %FALSE append(a, b, c)
 %FALSE append(a, [], [])
 %FALSE append([], b, [])
 %FALSE append([], [], c)

 %QUERY append([], tail, Z)
 %ANSWER Z=tail

 %QUERY append([], Z, tail)
 %ANSWER Z=tail

 %QUERY append([a], b, X)
 %ANSWER X = [a|b]

 %QUERY append([a,b,c], d, X)
 %ANSWER X = [a,b,c|d]

 %QUERY append([a], [], X)
 %ANSWER X = [a]

 %QUERY append([a], [b], X)
 %ANSWER X = [a,b]

 %QUERY append([X|FL],['^'],[a,f,g,^])
 %ANSWER
 % FL = [f,g]
 % X = a
 %ANSWER

 %FALSE append([X|FL],['^'],[a,f,g,^,z])

 %QUERY append([X|FL],['^'],[a,f,g,^,z,^])
 %ANSWER
 % FL = [f,g,^,z]
 % X = a
 %ANSWER

 %QUERY append([X|FL],['^'],[a,f,g,^,^])
 %ANSWER
 % FL = [f,g,^]
 % X = a
 %ANSWER

 %FALSE append([a|b], [b|c], X)
 %FALSE append([a|b], [b|c], [a,b,c,d])
 %FALSE append([a|b], X, [a,b,c,d])
 %FALSE append(X, [b|c], [a,b,c,d])
 %FALSE append([a|b], X, Y)
 %FALSE append(X, [b|c], Y)
 */
/**
 * <code>append(X,Y,Z)</code> - concatenates two lists.
 * <p>
 * The <code>append(X,Y,Z)</code> goal succeeds if the concatenation of lists <code>X</code> and <code>Y</code> matches
 * the list <code>Z</code>.
 * </p>
 */
public final class Append extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term prefix, Term suffix, Term concatenated) {
      if (prefix.getType().isVariable() && suffix.getType().isVariable()) {
         List<Term> javaUtilList = toJavaUtilList(concatenated);
         if (javaUtilList == null) {
            throw new ProjogException("Expected list but got: " + concatenated.getType());
         }
         return new Retryable(prefix, suffix, javaUtilList);
      } else {
         boolean result = evaluateSingleOutcome(prefix, suffix, concatenated);
         return AbstractSingletonPredicate.toPredicate(result);
      }
   }

   private boolean evaluateSingleOutcome(final Term prefix, final Term suffix, final Term concatenated) {
      if (prefix.getType() == TermType.EMPTY_LIST) {
         return concatenated.unify(suffix);
      }

      if (concatenated.getType() == TermType.EMPTY_LIST) {
         return EmptyList.EMPTY_LIST.unify(prefix) && EmptyList.EMPTY_LIST.unify(suffix);
      }

      final List<Term> prefixList = toJavaUtilList(prefix);
      final List<Term> suffixList = toJavaUtilList(suffix);

      if (prefixList != null && suffixList != null) {
         final List<Term> concatenatedList = new ArrayList<>();
         concatenatedList.addAll(prefixList);
         concatenatedList.addAll(suffixList);
         return concatenated.unify(createList(concatenatedList));
      }

      if (prefixList == null && suffixList == null) {
         return false;
      }

      if (concatenated.getType() == TermType.LIST) {
         final List<Term> concatenatedList = toJavaUtilList(concatenated);
         final int concatenatedLength = concatenatedList.size();

         final int splitIdx;
         if (prefixList != null) {
            splitIdx = prefixList.size();
         } else {
            splitIdx = concatenatedLength - suffixList.size();
         }

         return prefix.unify(createList(concatenatedList.subList(0, splitIdx))) && suffix.unify(createList(concatenatedList.subList(splitIdx, concatenatedLength)));
      }

      if (prefixList != null) {
         return concatenated.unify(ListFactory.createList(prefixList.toArray(new Term[prefixList.size()]), suffix));
      }

      return false;
   }

   private static class Retryable implements Predicate {
      final Term arg1;
      final Term arg2;
      final List<Term> combined;
      int ctr;

      @SuppressWarnings("unchecked")
      Retryable(Term arg1, Term arg2, List<Term> combined) {
         this.arg1 = arg1;
         this.arg2 = arg2;
         this.combined = combined == null ? Collections.EMPTY_LIST : combined;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            arg1.backtrack();
            arg2.backtrack();

            Term prefix = createList(combined.subList(0, ctr));
            Term suffix = createList(combined.subList(ctr, combined.size()));
            ctr++;

            return arg1.unify(prefix) && arg2.unify(suffix);
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr <= combined.size();
      }
   }
}
