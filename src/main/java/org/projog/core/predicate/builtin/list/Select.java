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

import java.util.ArrayList;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;

/* TEST
 %QUERY select(X,[h,e,l,l,o],Z)
 %ANSWER
 % X=h
 % Z=[e,l,l,o]
 %ANSWER
 %ANSWER
 % X=e
 % Z=[h,l,l,o]
 %ANSWER
 %ANSWER
 % X=l
 % Z=[h,e,l,o]
 %ANSWER
 %ANSWER
 % X=l
 % Z=[h,e,l,o]
 %ANSWER
 %ANSWER
 % X=o
 % Z=[h,e,l,l]
 %ANSWER

 %QUERY select(l,[h,e,l,l,o],Z)
 %ANSWER Z=[h,e,l,o]
 %ANSWER Z=[h,e,l,o]
 %NO

 %QUERY select(l,[h,e,l,l,o],[h,e,l,o])
 %ANSWER/
 %ANSWER/
 %NO

 %QUERY select(p(a,B),[p(X,q), p(a,X)],Z)
 %ANSWER
 % B=q
 % X=a
 % Z=[p(a, a)]
 %ANSWER
 %ANSWER
 % B=UNINSTANTIATED VARIABLE
 % X=UNINSTANTIATED VARIABLE
 % Z=[p(X, q)]
 %ANSWER

 %QUERY select(a, Result, [x,y,z])
 %ANSWER Result=[a,x,y,z]
 %ANSWER Result=[x,a,y,z]
 %ANSWER Result=[x,y,a,z]
 %ANSWER Result=[x,y,z,a]
 */
/**
 * <code>select(X,Y,Z)</code> - removes an element from a list.
 * <p>
 * Attempts to unify <code>Z</code> with the result of removing an occurrence of <code>X</code> from the list
 * represented by <code>Y</code>. An attempt is made to retry the goal during backtracking.
 * </p>
 */
public final class Select extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term element, Term inputList, Term outputList) {
      if (inputList.getType().isVariable()) {
         List<Term> list = ListUtils.toJavaUtilList(outputList);
         if (list == null) {
            throw new ProjogException("Expected list but got: " + outputList.getType());
         }
         return new IncludePredicate(element, inputList, outputList, list);
      } else {
         List<Term> list = ListUtils.toJavaUtilList(inputList);
         if (list == null) {
            throw new ProjogException("Expected list but got: " + inputList.getType());
         }
         return new ExcludePredicate(element, inputList, outputList, list);
      }
   }

   private final class IncludePredicate implements Predicate {
      private final Term element;
      private final Term variable;
      private final Term thirdArg;
      private final List<Term> list;
      private int ctr;

      private IncludePredicate(Term element, Term variable, Term thirdArg, List<Term> list) {
         this.element = element;
         this.variable = variable;
         this.thirdArg = thirdArg;
         this.list = list;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            if (retrying()) {
               element.backtrack();
               variable.backtrack();
               thirdArg.backtrack();
            }

            boolean unified = variable.unify(include(ctr));
            ctr++;
            if (unified) {
               return true;
            }
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < list.size() + 1;
      }

      private boolean retrying() {
         return ctr > 0;
      }

      /**
       * Create a a new {@code org.projog.core.term.List} based on {@code list} but including the element at index
       * {@code indexOfElementToInclude}.
       */
      private Term include(int indexOfElementToInclude) {
         final int size = list.size();
         final List<Term> result = new ArrayList<>(size + 1);
         for (int i = 0; i < size; i++) {
            if (i == ctr) {
               result.add(element);
            }
            result.add(list.get(i));
         }
         if (size == ctr) {
            result.add(element);
         }
         return ListFactory.createList(result);
      }
   }

   private final class ExcludePredicate implements Predicate {
      private final Term element;
      private final Term inputList;
      private final Term outputList;
      private final List<Term> list;
      private int ctr;

      private ExcludePredicate(Term element, Term inputList, Term outputList, List<Term> list) {
         this.element = element;
         this.inputList = inputList;
         this.outputList = outputList;
         this.list = list;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            if (retrying()) {
               element.backtrack();
               inputList.backtrack();
               outputList.backtrack();
            }

            Term listElement = list.get(ctr);
            boolean unified = element.unify(listElement) && outputList.unify(exclude(ctr));
            ctr++;
            if (unified) {
               return true;
            }
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr < list.size();
      }

      private boolean retrying() {
         return ctr > 0;
      }

      /**
       * Create a a new {@code org.projog.core.term.List} based on {@code list} but excluding the element at index
       * {@code indexOfElementToExclude}.
       */
      private Term exclude(int indexOfElementToExclude) {
         final int size = list.size();
         final List<Term> result = new ArrayList<>(size - 1);
         for (int i = 0; i < size; i++) {
            if (i != ctr) {
               result.add(list.get(i));
            }
         }
         return ListFactory.createList(result);
      }
   }
}
