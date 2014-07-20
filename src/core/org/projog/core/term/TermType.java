package org.projog.core.term;

/**
 * Defines the type of terms supported by Projog.
 * 
 * @see Term#getType()
 */
public enum TermType {
   /** @see AnonymousVariable */
   ANONYMOUS_VARIABLE(false, false, true, 1),
   /** @see Variable */
   NAMED_VARIABLE(false, false, true, 2),
   /** @see DecimalFraction */
   FRACTION(false, true, false, 3),
   /** @see IntegerNumber */
   INTEGER(false, true, false, 4),
   /** @see EmptyList */
   EMPTY_LIST(false, false, false, 5),
   /** @see Atom */
   ATOM(false, false, false, 6),
   /** @see Structure */
   STRUCTURE(true, false, false, 7),
   /** @see List */
   LIST(true, false, false, 7);

   private final boolean isStructure;
   private final boolean isNumeric;
   private final boolean isVariable;
   private final int precedence;

   private TermType(boolean isStructure, boolean isNumeric, boolean isVariable, int precedence) {
      this.isStructure = isStructure;
      this.isNumeric = isNumeric;
      this.isVariable = isVariable;
      this.precedence = precedence;
   }

   /**
    * @return {@code true} if this type represents "compound structure"
    */
   public boolean isStructure() {
      return isStructure;
   }

   /**
    * @return {@code true} if this type represents instances of {@link Numeric}
    */
   public boolean isNumeric() {
      return isNumeric;
   }

   /**
    * @return {@code true} if this type represents a variable
    */
   public boolean isVariable() {
      return isVariable;
   }

   /**
    * Used to consistently order {@link Term}s of different types.
    * 
    * @return precedence of this type
    * @see TermComparator#compare(Term, Term)
    */
   public int getPrecedence() {
      return precedence;
   }
}