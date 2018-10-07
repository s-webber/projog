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
package org.projog.core.term;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Static factory methods for creating new instances of {@link List}.
 *
 * @see List
 * @see ListUtils
 */
public final class ListFactory {
   /**
    * A "{@code .}" is the functor name for all lists in Prolog.
    */
   public static final String LIST_PREDICATE_NAME = ".";

   /**
    * Private constructor as all methods are static.
    */
   private ListFactory() {
      // do nothing
   }

   /**
    * Returns a new {@link List} with specified head and tail.
    *
    * @param head the first argument in the list
    * @param tail the second argument in the list
    * @return a new {@link List} with specified head and tail
    */
   public static List createList(Term head, Term tail) {
      return new List(head, tail);
   }

   /**
    * Returns a new {@link List} with the specified terms and a empty list as the final tail element.
    *
    * @param terms contents of the list
    * @return a new {@link List} with the specified terms and a empty list as the final tail element
    */
   public static Term createList(final Collection<Term> terms) {
      return createList(terms.toArray(new Term[terms.size()]));
   }

   /**
    * Returns a new {@link List} with the specified terms and a empty list as the final tail element.
    * <p>
    * By having a {@code List} with a {@code List} as its tail it is possible to represent an ordered sequence of the
    * specified terms.
    *
    * @param terms contents of the list
    * @return a new {@link List} with the specified terms and a empty list as the final tail element
    */
   public static Term createList(Term[] terms) {
      return createList(terms, EmptyList.EMPTY_LIST);
   }

   /**
    * Returns a new {@link List} with the specified terms and the second parameter as the tail element.
    * <p>
    * By having a {@code List} with a {@code List} as its tail it is possible to represent an ordered sequence of the
    * specified terms.
    *
    * @param terms contents of the list
    * @return a new {@link List} with the specified terms and the second parameter as the tail element
    */
   public static Term createList(Term[] terms, Term tail) {
      int numberOfElements = terms.length;
      if (numberOfElements == 0) {
         return EmptyList.EMPTY_LIST;
      }
      Term list = tail;
      for (int i = numberOfElements - 1; i > -1; i--) {
         Term element = terms[i];
         list = createList(element, list);
      }
      return list;
   }

   /** Returns a new list of the specified length where is each element is a variable. */
   public static Term createListOfLength(final int length) {
      final java.util.List<Term> javaList = new ArrayList<Term>();
      for (int i = 0; i < length; i++) {
         javaList.add(new Variable("E" + i));
      }
      return createList(javaList);
   }
}
