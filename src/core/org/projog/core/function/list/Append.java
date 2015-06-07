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

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

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
 %FALSE append([], Y, Z)
 %FALSE append(X, [], Z)
 %FALSE append(a, b, c)
 %FALSE append(a, [], [])
 %FALSE append([], b, [])
 %FALSE append([], [], c)
 */
/**
 * <code>append(X,Y,Z)</code> - concatenates two lists.
 * <p>
 * The <code>append(X,Y,Z)</code> goal succeeds if the concatenation of lists <code>X</code> and <code>Y</code> matches
 * the list <code>Z</code>.
 * </p>
 */
public final class Append implements PredicateFactory {
   private final Singleton singleton = new Singleton();

   @Override
   public Predicate getPredicate(Term... args) {
      return getPredicate(args[0], args[1], args[2]);
   }

   public Predicate getPredicate(Term prefix, Term suffix, Term combined) {
      if (prefix.getType().isVariable() && suffix.getType().isVariable()) {
         List<Term> javaUtilList = toJavaUtilList(combined);
         if (javaUtilList == null) {
            throw new ProjogException("Expected list but got: " + combined.getType());
         }
         return new Retryable(javaUtilList);
      } else {
         return singleton;
      }
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      singleton.setKnowledgeBase(kb);
   }

   private static class Singleton extends AbstractSingletonPredicate {
      @Override
      public boolean evaluate(final Term prefix, final Term suffix, final Term concatenated) {
         final List<Term> prefixList = toJavaUtilList(prefix);
         final List<Term> suffixList = toJavaUtilList(suffix);

         if (prefixList != null && suffixList != null) {
            final List<Term> concatenatedList = new ArrayList<Term>();
            concatenatedList.addAll(prefixList);
            concatenatedList.addAll(suffixList);
            return concatenated.unify(createList(concatenatedList));
         }

         if (prefixList == null && suffixList == null) {
            return false;
         }

         final List<Term> concatenatedList = toJavaUtilList(concatenated);
         if (concatenatedList == null) {
            return false;
         }
         final int concatenatedLength = concatenatedList.size();

         final int splitIdx;
         if (prefixList != null) {
            splitIdx = prefixList.size();
         } else {
            splitIdx = concatenatedLength - suffixList.size();
         }

         return prefix.unify(createList(concatenatedList.subList(0, splitIdx))) && suffix.unify(createList(concatenatedList.subList(splitIdx, concatenatedLength)));
      }
   }

   private static class Retryable implements Predicate {
      final List<Term> combined;
      int ctr;

      @SuppressWarnings("unchecked")
      Retryable(List<Term> combined) {
         this.combined = combined == null ? Collections.EMPTY_LIST : combined;
      }

      @Override
      public boolean evaluate(Term... args) {
         return evaluate(args[0], args[1], args[2]);
      }

      private boolean evaluate(Term arg1, Term arg2, Term arg3) {
         while (couldReEvaluationSucceed()) {
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
      public boolean isRetryable() {
         return true;
      }

      @Override
      public boolean couldReEvaluationSucceed() {
         return ctr <= combined.size();
      }
   }
}
