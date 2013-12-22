/*
 * Copyright 2013 S Webber
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
 * Represents an unspecified {@link Term}.
 * <p>
 * Can be used instead of a {@link Variable} when there is no requirement to know the actual {@link Term} it has been
 * instantiated with.
 * 
 * @see Variable
 */
public final class AnonymousVariable implements Term {
   /**
    * Singleton instance
    */
   public static final AnonymousVariable ANONYMOUS_VARIABLE = new AnonymousVariable();

   /**
    * Private constructor to force use of {@link #ANONYMOUS_VARIABLE}
    */
   private AnonymousVariable() {
      // do nothing
   }

   @Override
   public String getName() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Term[] getArgs() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getNumberOfArguments() {
      return 0;
   }

   @Override
   public Term getArgument(int index) {
      throw new UnsupportedOperationException();
   }

   /**
    * Returns {@link TermType#ANONYMOUS_VARIABLE}.
    * 
    * @return {@link TermType#ANONYMOUS_VARIABLE}
    */
   @Override
   public TermType getType() {
      return TermType.ANONYMOUS_VARIABLE;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public AnonymousVariable copy(Map<Variable, Variable> sharedVariables) {
      return this;
   }

   @Override
   public AnonymousVariable getTerm() {
      return this;
   }

   /**
    * Anonymous variables always unify successfully.
    * 
    * @return true
    */
   @Override
   public boolean unify(Term t) {
      return true;
   }

   /**
    * Anonymous variables are never strictly equal to anything (not even to themselves).
    * 
    * @return false
    */
   @Override
   public boolean strictEquality(Term t) {
      return false;
   }

   @Override
   public void backtrack() {
      // do nothing
   }

   /**
    * @return {@code _}
    */
   @Override
   public String toString() {
      return "_";
   }
}