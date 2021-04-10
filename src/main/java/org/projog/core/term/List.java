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

import java.util.ArrayList;
import java.util.Map;
import java.util.function.UnaryOperator;

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
   private final Term tail;
   private final boolean immutable;
   private final int hashCode;

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
      this.hashCode = head.hashCode() + (tail.hashCode() * 7);
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
      return new Term[] {head, tail};
   }

   @Override
   public int getNumberOfArguments() {
      return 2;
   }

   @Override
   public Term getArgument(int index) {
      switch (index) {
         case 0:
            return head;
         case 1:
            return tail;
         default:
            throw new ArrayIndexOutOfBoundsException(index);
      }
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
      return traverse(Term::getTerm);
   }

   @Override
   public List copy(Map<Variable, Variable> sharedVariables) {
      return traverse(t -> t.copy(sharedVariables));
   }

   /**
    * Used by {@link #getTerm()} and {@link #copy(Map)} to traverse a list without using recursion.
    *
    * @param f the operation to apply to each mutable element of the list
    * @return the resulting list produced as a result of applying {@link f} to each of the mutable elements
    */
   private List traverse(UnaryOperator<Term> f) {
      if (immutable) {
         return this;
      }

      List list = this;

      ArrayList<List> elements = new ArrayList<>();
      while (!list.immutable && list.tail.getType() == TermType.LIST) {
         elements.add(list);
         list = (List) list.tail.getBound();
      }

      if (!list.immutable) {
         Term newHead = f.apply(list.head);
         Term newTail = f.apply(list.tail);
         if (newHead != list.head || newTail != list.tail) {
            list = new List(newHead, newTail);
         }
      }

      for (int i = elements.size() - 1; i > -1; i--) {
         List next = elements.get(i);
         Term newHead = f.apply(next.head);
         if (newHead != next.head || list != next.tail) {
            list = new List(newHead, list);
         } else {
            list = next;
         }
      }

      return list;
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

   @Override
   public void backtrack() {
      // used to be implemented using recursion but caused stack overflow problems with long lists
      List list = this;
      while (!list.immutable) {
         list.head.backtrack();
         if (list.tail.getClass() == List.class) {
            list = (List) list.tail;
         } else {
            list.tail.backtrack();
            return;
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o.getClass() == List.class && hashCode == o.hashCode()) {
         // used to be implemented using recursion but caused stack overflow problems with long lists
         Term a = this;
         Term b = (List) o;

         do {
            if (!a.getArgument(0).equals(b.getArgument(0))) {
               return false;
            }

            a = a.getArgument(1);
            b = b.getArgument(1);
         } while (a.getClass() == List.class && b.getClass() == List.class);

         return a.equals(b);
      }

      return false;
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public String toString() {
      // used to be implemented using recursion but caused stack overflow problems with long lists
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
