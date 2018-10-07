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

/**
 * Represents a data structure with two {@link Term}s - a head and a tail.
 * <p>
 * The head and tail can be any {@link Term}s - including other {@code List}s. By having a {@code List} with a
 * {@code List} as its tail it is possible to represent an ordered sequence of {@link Term}s of any length. The end of
 * an ordered sequence of {@link Term}s is normally represented as a tail having the value of an {@link EmptyList}.
 *
 * @see EmptyList
 * @see ListFactory
 * @see ListUtils
 */
public final class List implements Term {
   private final Term head;
   private Term tail;
   private final boolean immutable;

   /**
    * Creates a new list with the specified head and tail.
    * <p>
    * Consider using {@link ListFactory} rather than calling directly.
    *
    * @param head the head of the new list
    * @param tail the tail of the new list
    */
   public List(Term head, Term tail) {
      this.head = head;
      this.tail = tail;
      this.immutable = head.isImmutable() && tail.isImmutable();
   }

   /**
    * Replaces the tail of the list with the specified term.
    * <p>
    * <b>Note:</b> This method has only been added to make it easier to optimise tail-recursive functions. It's use is
    * not recommend as altering the tail of a list after it has been created may cause unexpected behaviour.
    * <p>
    * TODO Find an alternative to this method for doing tail-recursive optimisation.
    *
    * @param tail term to set as the tail of this object
    * @deprecated only used to make tail recursive functions more efficient
    */
   @Deprecated
   public void setTail(Term tail) {
      this.tail = tail;
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

   @Override
   public Term[] getArgs() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getNumberOfArguments() {
      return 2;
   }

   @Override
   public Term getArgument(int index) {
      return index == 0 ? head : tail;
   }

   /**
    * Returns {@link TermType#LIST}.
    *
    * @return {@link TermType#LIST}
    */
   @Override
   public TermType getType() {
      return TermType.LIST;
   }

   @Override
   public boolean isImmutable() {
      return immutable;
   }

   @Override
   public List getTerm() {
      if (immutable) {
         return this;
      } else {
         Term newHead = head.getTerm();
         Term newTail = tail.getTerm();
         if (newHead == head && newTail == tail) {
            return this;
         } else {
            return new List(newHead, newTail);
         }
      }
   }

   @Override
   public List copy(Map<Variable, Variable> sharedVariables) {
      if (immutable) {
         return this;
      } else {
         Term newHead = head.copy(sharedVariables);
         Term newTail = tail.copy(sharedVariables);
         if (newHead == head && newTail == tail) {
            return this;
         } else {
            return new List(newHead, newTail);
         }
      }
   }

   @Override
   public boolean unify(Term t1) {
      // used to be implemented using recursion but caused stack overflow problems with long lists
      Term t2 = this;
      do {
         TermType tType = t1.getType();
         if (tType == TermType.LIST) {
            if (t2.getArgument(0).unify(t1.getArgument(0)) == false) {
               return false;
            }
            t1 = t1.getArgument(1);
            t2 = t2.getArgument(1);
         } else if (tType.isVariable()) {
            return t1.unify(t2);
         } else {
            return false;
         }
      } while (t2.getType() == TermType.LIST);
      return t2.unify(t1);
   }

   /**
    * Performs a strict comparison of this list to the specified term.
    *
    * @param t1 the term to compare this list against
    * @return {@code true} if the given term represents a {@link TermType#LIST} with a head and tail strictly equal to
    * the corresponding head and tail of this List object.
    */
   @Override
   public boolean strictEquality(Term t1) {
      // used to be implemented using recursion but caused stack overflow problems with long lists
      Term t2 = this;
      do {
         boolean equal = t1.getType() == TermType.LIST && t1.getArgument(0).strictEquality(t2.getArgument(0));
         if (equal == false) {
            return false;
         }
         t1 = t1.getArgument(1);
         t2 = t2.getArgument(1);
      } while (t2.getType() == TermType.LIST);
      return t1.strictEquality(t2);
   }

   @Override
   public void backtrack() {
      if (!immutable) {
         head.backtrack();
         tail.backtrack();
      }
   }

   @Override
   public String toString() {
      // used to be implemented using recursion but caused stack overflow problems with long listsSS
      StringBuilder sb = new StringBuilder();
      int listCtr = 0;
      Term t = this;
      do {
         sb.append(ListFactory.LIST_PREDICATE_NAME);
         sb.append("(");
         sb.append(t.getArgument(0));
         sb.append(", ");
         t = t.getArgument(1);
         listCtr++;
      } while (t.getType() == TermType.LIST);
      sb.append(t);
      for (int i = 0; i < listCtr; i++) {
         sb.append(")");
      }
      return sb.toString();
   }
}
