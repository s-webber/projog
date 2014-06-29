package org.projog.core.term;

import java.util.Map;

/**
 * Represents a value of the primitive type {@code double} as a {@link Term}.
 * <p>
 * DoubleNumbers are constant; their values cannot be changed after they are created. DoubleNumbers have no arguments.
 */
public final class DoubleNumber implements Numeric {
   private final double value;

   /**
    * @param value the value this term represents
    */
   public DoubleNumber(double value) {
      this.value = value;
   }

   /**
    * Returns a {@code String} representation of the {@code double} this term represents.
    * 
    * @return a {@code String} representation of the {@code double} this term represents
    */
   @Override
   public String getName() {
      return toString();
   }

   @Override
   public Term[] getArgs() {
      return TermUtils.EMPTY_ARRAY;
   }

   @Override
   public int getNumberOfArguments() {
      return 0;
   }

   /**
    * @throws UnsupportedOperationException as this implementation of {@link Term} has no arguments
    */
   @Override
   public Term getArgument(int index) {
      throw new UnsupportedOperationException();
   }

   /**
    * Returns {@link TermType#DOUBLE}.
    * 
    * @return {@link TermType#DOUBLE}
    */
   @Override
   public TermType getType() {
      return TermType.DOUBLE;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public DoubleNumber copy(Map<Variable, Variable> sharedVariables) {
      return this;
   }

   @Override
   public DoubleNumber getTerm() {
      return this;
   }

   @Override
   public boolean unify(Term t) {
      TermType tType = t.getType();
      if (tType == TermType.DOUBLE) {
         return value == ((DoubleNumber) t.getTerm()).value;
      } else if (tType.isVariable()) {
         return t.unify(this);
      } else {
         return false;
      }
   }

   /**
    * Performs a strict comparison of this term to the specified term.
    * 
    * @param t the term to compare this term against
    * @return {@code true} if the given term represents a {@link TermType#DOUBLE} with a value equal to the value of
    * this {@code DoubleNumber}
    */
   @Override
   public boolean strictEquality(Term t) {
      return t.getType() == TermType.DOUBLE && value == ((DoubleNumber) t.getTerm()).value;
   }

   @Override
   public void backtrack() {
      // do nothing
   }

   /**
    * @return the {@code double} value of this term cast to an {@code long}
    */
   @Override
   public long getLong() {
      return (long) value;
   }

   /**
    * @return the {@code double} value of this term
    */
   @Override
   public double getDouble() {
      return value;
   }

   /**
    * @return a {@code String} representation of the {@code double} this term represents
    */
   @Override
   public String toString() {
      return Double.toString(value);
   }
}