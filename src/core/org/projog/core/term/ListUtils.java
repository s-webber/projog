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
package org.projog.core.term;

import static org.projog.core.term.TermComparator.TERM_COMPARATOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper methods for performing common tasks with Prolog list data structures.
 * 
 * @see List
 * @see ListFactory
 * @see TermUtils
 */
public class ListUtils {
   /**
    * Private constructor as all methods are static.
    */
   private ListUtils() {
      // do nothing
   }

   /**
    * Returns a new {@code java.util.List} containing the contents of the specified {@code org.projog.core.term.List}.
    * <p>
    * Will return {@code null} if {@code list} is neither of type {@link TermType#LIST} or {@link TermType#EMPTY_LIST},
    * or if {@code list} represents a partial list (i.e. a list that does not have an empty list as its tail).
    * </p>
    * 
    * @see #toSortedJavaUtilList(Term)
    */
   public static List<Term> toJavaUtilList(Term list) {
      if (list.getType() == TermType.LIST) {
         final List<Term> result = new ArrayList<Term>();
         do {
            result.add(list.getArgument(0));
            list = list.getArgument(1);
         } while (list.getType() == TermType.LIST);

         if (list.getType() == TermType.EMPTY_LIST) {
            return result;
         } else {
            // partial list
            return null;
         }
      } else if (list.getType() == TermType.EMPTY_LIST) {
         return Collections.emptyList();
      } else {
         // not a list
         return null;
      }
   }

   /**
    * Returns a new {@code java.util.List} containing the sorted contents of the specified
    * {@code org.projog.core.term.List}.
    * <p>
    * The elements in the returned list will be ordered using the standard ordering of terms, as implemented by
    * {@link TermComparator}.
    * </p>
    * <p>
    * Will return {@code null} if {@code list} is neither of type {@link TermType#LIST} or {@link TermType#EMPTY_LIST},
    * or if {@code list} represents a partial list (i.e. a list that does not have an empty list as its tail).
    * </p>
    * 
    * @see #toJavaUtilList(Term)
    */
   public static List<Term> toSortedJavaUtilList(Term unsorted) {
      List<Term> elements = toJavaUtilList(unsorted);
      if (elements != null) {
         Collections.sort(elements, TERM_COMPARATOR);
      }
      return elements;
   }

   /**
    * Checks is a term can be unified with at least one element of a list.
    * <p>
    * Iterates through each element of {@code list} attempting to unify with {@code element}. Returns {@code true}
    * immediately after the first unifiable element is found. If {@code list} contains no elements that can be unified
    * with {@code element} then {@code false} is returned.
    * </p>
    * 
    * @throws IllegalArgumentException if {@code list} is not of type {@code TermType#LIST} or {@code TermType#EMPTY_LIST}
    */
   public static boolean isMember(Term element, Term list) {
      if (list.getType() != TermType.LIST && list.getType() != TermType.EMPTY_LIST) {
         throw new IllegalArgumentException("Expected list but got: " + list);
      }
      while (list.getType() == TermType.LIST) {
         if (element.unify(list.getArgument(0))) {
            return true;
         }
         element.backtrack();
         list.backtrack();
         list = list.getArgument(1);
      }
      return false;
   }
}
