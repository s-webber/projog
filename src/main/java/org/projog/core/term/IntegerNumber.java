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

import java.util.Map;

import org.projog.core.math.Numeric;

/**
 * Represents a value of the primitive type {@code long} as a {@link Term}.
 * <p>
 * IntegerNumbers are constant; their values cannot be changed after they are created. IntegerNumbers have no arguments.
 */
public final class IntegerNumber implements Numeric {
   private final long value;

   /**
    * @param value the value this term represents
    */
   public IntegerNumber(long value) {
      this.value = value;
   }

   /**
    * Returns a {@code String} representation of the {@code long} this term represents.
    *
    * @return a {@code String} representation of the {@code long} this term represents
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
    * @throws ArrayIndexOutOfBoundsException as this implementation of {@link Term} has no arguments
    */
   @Override
   public Term getArgument(int index) {
      throw new ArrayIndexOutOfBoundsException(index);
   }

   /**
    * Returns {@link TermType#INTEGER}.
    *
    * @return {@link TermType#INTEGER}
    */
   @Override
   public TermType getType() {
      return TermType.INTEGER;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public IntegerNumber getTerm() {
      return this;
   }

   @Override
   public IntegerNumber copy(Map<Variable, Variable> sharedVariables) {
      return this;
   }

   @Override
   public boolean unify(Term t) {
      TermType tType = t.getType();
      if (tType == TermType.INTEGER) {
         return value == ((Numeric) t.getTerm()).getLong();
      } else if (tType.isVariable() || tType == TermType.CLP_VARIABLE) {
         return t.unify(this);
      } else {
         return false;
      }
   }

   @Override
   public void backtrack() {
      // do nothing
   }

   /**
    * @return the {@code long} value of this term
    */
   @Override
   public long getLong() {
      return value;
   }

   /**
    * @return the {@code long} value of this term cast to a {@code double}
    */
   @Override
   public double getDouble() {
      return value;
   }

   @Override
   public IntegerNumber calculate(Term[] args) {
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof Numeric) {
         Numeric n = (Numeric) o;
         return n.isImmutable() && n.getType() == TermType.INTEGER && value == n.getLong();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Long.hashCode(value);
   }

   /**
    * @return a {@code String} representation of the {@code long} this term represents
    */
   @Override
   public String toString() {
      return Long.toString(value);
   }
}
