package org.projog.core.term;

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
      return createList(head, tail, head.isImmutable() && tail.isImmutable());
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
    * By having a {@code List} with a {@code List} as it's tail it is possible to represent an ordered sequence of the
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
    * By having a {@code List} with a {@code List} as it's tail it is possible to represent an ordered sequence of the
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
      // keep track of whether sublists are immutable
      boolean isImmutable = tail.isImmutable();
      Term list = tail;
      for (int i = numberOfElements - 1; i > -1; i--) {
         Term element = terms[i];
         isImmutable = isImmutable && element.isImmutable();
         list = createList(element, list, isImmutable);
      }
      return list;
   }

   private static List createList(Term head, Term tail, boolean isImmutable) {
      return new List(head, tail, isImmutable);
   }
}