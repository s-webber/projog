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
 * Represents a data structure with no {@link Term}s.
 * 
 * @see List
 * @see ListFactory
 */
public final class EmptyList implements Term {
   /**
    * Singleton instance
    */
   public static final EmptyList EMPTY_LIST = new EmptyList();

   /**
    * Private constructor to force use of {@link #EMPTY_LIST}
    */
   private EmptyList() {
      // do nothing
   }

   @Override
   public void backtrack() {
      // do nothing
   }

   @Override
   public Term copy(Map<Variable, Variable> sharedVariables) {
      return EMPTY_LIST;
   }

   @Override
   public EmptyList getTerm() {
      return EMPTY_LIST;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   /**
    * @throws UnsupportedOperationException as this implementation of {@link Term} has no arguments
    */
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
    * Returns {@link ListFactory#LIST_PREDICATE_NAME}.
    * 
    * @return {@link ListFactory#LIST_PREDICATE_NAME}
    */
   @Override
   public String getName() {
      return ListFactory.LIST_PREDICATE_NAME;
   }

   /**
    * Returns {@link TermType#EMPTY_LIST}.
    * 
    * @return {@link TermType#EMPTY_LIST}
    */
   @Override
   public TermType getType() {
      return TermType.EMPTY_LIST;
   }

   /**
    * Performs a strict comparison of this term to the specified term.
    * 
    * @param t the term to compare this term against
    * @return {@code true} if the given term represents a {@link TermType#EMPTY_LIST}
    */
   @Override
   public boolean strictEquality(Term t) {
      return t.getType() == TermType.EMPTY_LIST;
   }

   @Override
   public boolean unify(Term t) {
      TermType tType = t.getType();
      if (tType == TermType.EMPTY_LIST) {
         return true;
      } else if (tType.isVariable()) {
         return t.unify(this);
      } else {
         return false;
      }
   }

   /**
    * @return {@code []}
    */
   @Override
   public String toString() {
      return "[]";
   }
}
