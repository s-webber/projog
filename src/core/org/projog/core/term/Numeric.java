package org.projog.core.term;

/**
 * A {@link Term} that has a numerical value.
 * <p>
 * <img src="doc-files/Term.png">
 * 
 * @see TermUtils#castToNumeric(Term)
 */
public interface Numeric extends Term {
   /**
    * Returns the value of this numeric as a {@code long}.
    * 
    * @return the value of this numeric as a {@code long}
    */
   long getLong();

   /**
    * Returns the value of this numeric as a {@code double}.
    * 
    * @return the value of this numeric as a {@code double}
    */
   double getDouble();
}