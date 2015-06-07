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
 * Represents a specific object or relationship.
 * <p>
 * Atoms are constant; their values cannot be changed after they are created. Atoms have no arguments.
 */
public final class Atom implements Term {
   private final String value;

   /**
    * @param value the value this {@code Atom} represents
    */
   public Atom(String value) {
      this.value = value;
   }

   /**
    * Returns the value this {@code Atom} represents.
    * 
    * @return the value this {@code Atom} represents
    */
   @Override
   public String getName() {
      return value;
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
    * Returns {@link TermType#ATOM}.
    * 
    * @return {@link TermType#ATOM}
    */
   @Override
   public TermType getType() {
      return TermType.ATOM;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public Atom copy(Map<Variable, Variable> sharedVariables) {
      return this;
   }

   @Override
   public Atom getTerm() {
      return this;
   }

   @Override
   public boolean unify(Term t) {
      TermType tType = t.getType();
      if (tType == TermType.ATOM) {
         return value.equals(t.getName());
      } else if (tType.isVariable()) {
         return t.unify(this);
      } else {
         return false;
      }
   }

   /**
    * Performs a strict comparison of this atom to the specified term.
    * 
    * @param t the term to compare this atom against
    * @return {@code true} if the given term represents a {@link TermType#ATOM} with a value equal to the value of this
    * atom
    */
   @Override
   public boolean strictEquality(Term t) {
      return t.getType() == TermType.ATOM && value.equals(t.getName());
   }

   @Override
   public void backtrack() {
      // do nothing
   }

   /**
    * @return {@link #getName()}
    */
   @Override
   public String toString() {
      return getName();
   }
}
