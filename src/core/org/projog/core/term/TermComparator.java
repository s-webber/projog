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

import java.util.Comparator;

import org.projog.core.ProjogException;

/**
 * An implementation of {@code Comparator} for comparing instances of {@link Term}.
 * 
 * @see #compare(Term, Term)
 * @see NumericTermComparator
 * @see Term#strictEquality(Term)
 */
public final class TermComparator implements Comparator<Term> {
   /**
    * Singleton instance
    */
   public static final TermComparator TERM_COMPARATOR = new TermComparator();

   /**
    * Private constructor to force use of {@link #TERM_COMPARATOR}
    */
   private TermComparator() {
      // do nothing
   }

   /**
    * Compares the two arguments for order.
    * <p>
    * Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
    * than the second.
    * <p>
    * The criteria for deciding the order of terms is as follows:
    * <ul>
    * <li>All uninstantiated variables are less than all floating point numbers, which are less than all integers, which
    * are less than all atoms, which are less than all structures (including lists).</li>
    * <li>Comparison of two integer or two floating point numbers is done using {@link NumericTermComparator}.</li>
    * <li>Comparison of two atoms is done by comparing the {@code String} values they represent using
    * {@code String.compareTo(String)}.</li>
    * <li>One structure is less than another if it has a lower arity (number of arguments). If two structures have the
    * same arity then they are ordered by comparing their functors (names) (determined by
    * {@code String.compareTo(String)}). If two structures have the same arity and functor then they are ordered by
    * comparing their arguments in order. The first corresponding arguments that differ determines the order of the two
    * structures.</li>
    * </ul>
    * 
    * @param t1 the first term to be compared
    * @param t2 the second term to be compared
    * @return a negative integer, zero, or a positive integer as the first term is less than, equal to, or greater than
    * the second
    */
   @Override
   public int compare(Term t1, Term t2) {
      Term v1 = t1.getTerm();
      Term v2 = t2.getTerm();

      // if the both arguments refer to the same object then must be identical
      // this deals with the case where both arguments are empty lists
      // or both are an anonymous variable
      if (v1.getTerm() == v2.getTerm()) {
         return 0;
      }

      TermType type1 = v1.getType();
      TermType type2 = v2.getType();

      if (type1.isStructure() && type2.isStructure()) {
         return compareStructures(v1, v2);
      } else if (type1 != type2) {
         return type1.getPrecedence() > type2.getPrecedence() ? 1 : -1;
      } else {
         switch (type1) {
            case FRACTION:
            case INTEGER:
               return NumericTermComparator.NUMERIC_TERM_COMPARATOR.compare(v1, v2);
            case ATOM:
               return t1.getName().compareTo(t2.getName());
            case NAMED_VARIABLE:
               // NOTE: uses Object's hashCode which is not guaranteed,
               // so may get different results in different JVMs
               return v1.hashCode() > v2.hashCode() ? 1 : -1;
            default:
               throw new ProjogException("Unknown TermType: " + type1);
         }
      }
   }

   private int compareStructures(Term t1, Term t2) {
      // compare number of arguments
      int t1Length = t1.getNumberOfArguments();
      int t2Length = t2.getNumberOfArguments();
      if (t1Length != t2Length) {
         return t1Length > t2Length ? 1 : -1;
      }

      // compare predicate names
      int nameComparison = t1.getName().compareTo(t2.getName());
      if (nameComparison != 0) {
         return nameComparison;
      }

      // compare arguments one at a time
      for (int i = 0; i < t1Length; i++) {
         int argComparison = compare(t1.getArgument(i), t2.getArgument(i));
         if (argComparison != 0) {
            return argComparison;
         }
      }

      // if still cannot separate then consider them identical
      return 0;
   }
}
