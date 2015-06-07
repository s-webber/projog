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

import java.util.Map;

/**
 * Represents a value of the primitive type {@code double} as a {@link Term}.
 * <p>
 * DecimalFractions are constant; their values cannot be changed after they are created. DecimalFractions have no arguments.
 */
public final class DecimalFraction implements Numeric {
   private final double value;

   /**
    * @param value the value this term represents
    */
   public DecimalFraction(double value) {
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
    * Returns {@link TermType#FRACTION}.
    * 
    * @return {@link TermType#FRACTION}
    */
   @Override
   public TermType getType() {
      return TermType.FRACTION;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public DecimalFraction copy(Map<Variable, Variable> sharedVariables) {
      return this;
   }

   @Override
   public DecimalFraction getTerm() {
      return this;
   }

   @Override
   public boolean unify(Term t) {
      TermType tType = t.getType();
      if (tType == TermType.FRACTION) {
         return value == ((DecimalFraction) t.getTerm()).value;
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
    * @return {@code true} if the given term represents a {@link TermType#FRACTION} with a value equal to the value of
    * this {@code DecimalFraction}
    */
   @Override
   public boolean strictEquality(Term t) {
      return t.getType() == TermType.FRACTION && value == ((DecimalFraction) t.getTerm()).value;
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
