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
    * Returns a new {@code java.util.List} containing the sorted contents of the specified {@code org.projog.core.term.List}.
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
}
