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
 * A {@link Term} consisting of a functor (name) and a number of other {@link Term} arguments.
 * <p>
 * Also known as a "compound term".
 * <p>
 * <img src="doc-files/Structure.png">
 */
public final class Structure implements Term {
   private final String functor;
   private final Term[] args;
   private final boolean immutable;

   /**
    * Factory method for creating {@code Structure} instances.
    * <p>
    * The reason that {@code Structure}s have to be created via a factory method, rather than a constructor, is to
    * enforce:
    * <ul>
    * <li>structures with the functor {@code .} and two arguments are created as instances of {@link List}</li>
    * <li>no structures can be created without any arguments</li>
    * </ul>
    * 
    * @param functor the name of the new term
    * @param args arguments for the new term
    * @return either a new {@link Structure}, a new {@link List} or {@link EmptyList#EMPTY_LIST}
    */
   public static Term createStructure(String functor, Term[] args) {
      if (args.length == 0) {
         throw new IllegalArgumentException("Cannot create structure with no arguments");
      }

      if (ListFactory.LIST_PREDICATE_NAME.equals(functor)) {
         if (args.length == 2) {
            return ListFactory.createList(args[0], args[1]);
         }
         functor = ListFactory.LIST_PREDICATE_NAME;
      }

      return new Structure(functor, args, isImmutable(args));
   }

   private static boolean isImmutable(Term[] args) {
      for (Term t : args) {
         if (t.isImmutable() == false) {
            return false;
         }
      }
      return true;
   }

   /**
    * Private constructor to force use of {@link #createStructure(String, Term[])}
    * 
    * @param immutable is this structure immutable (i.e. are all its arguments known to be immutable)?
    */
   private Structure(String functor, Term[] args, boolean immutable) {
      this.functor = functor;
      this.args = args;
      this.immutable = immutable;
   }

   /**
    * Returns the functor of this structure.
    * 
    * @return the functor of this structure
    */
   @Override
   public String getName() {
      return functor;
   }

   @Override
   public Term[] getArgs() {
      return args;
   }

   @Override
   public int getNumberOfArguments() {
      return args.length;
   }

   @Override
   public Term getArgument(int index) {
      return args[index];
   }

   /**
    * Returns {@link TermType#STRUCTURE}.
    * 
    * @return {@link TermType#STRUCTURE}
    */
   @Override
   public TermType getType() {
      return TermType.STRUCTURE;
   }

   @Override
   public boolean isImmutable() {
      return immutable;
   }

   @Override
   public Structure getTerm() {
      if (immutable) {
         return this;
      } else {
         boolean returnThis = true;
         boolean newImmutable = true;
         Term newArgs[] = new Term[args.length];
         for (int i = 0; i < args.length; i++) {
            newArgs[i] = args[i].getTerm();
            if (newArgs[i] != args[i]) {
               returnThis = false;
            }
            if (newArgs[i].isImmutable() == false) {
               newImmutable = false;
            }
         }
         if (returnThis) {
            return this;
         } else {
            return new Structure(functor, newArgs, newImmutable);
         }
      }
   }

   @Override
   public Structure copy(Map<Variable, Variable> sharedVariables) {
      if (immutable) {
         return this;
      } else {
         boolean returnThis = true;
         boolean newIsImmutable = true;
         Term newArgs[] = new Term[args.length];
         for (int i = 0; i < args.length; i++) {
            newArgs[i] = args[i].copy(sharedVariables);
            if (newArgs[i] != args[i]) {
               returnThis = false;
            }
            if (newArgs[i].isImmutable() == false) {
               newIsImmutable = false;
            }
         }
         if (returnThis) {
            return this;
         } else {
            return new Structure(functor, newArgs, newIsImmutable);
         }
      }
   }

   @Override
   public boolean unify(Term t) {
      TermType tType = t.getType();
      if (tType == TermType.STRUCTURE) {
         Term[] tArgs = t.getArgs();
         if (args.length != tArgs.length) {
            return false;
         }
         if (!functor.equals(t.getName())) {
            return false;
         }
         for (int i = 0; i < args.length; i++) {
            if (!args[i].unify(tArgs[i])) {
               return false;
            }
         }
         return true;
      } else if (tType.isVariable()) {
         return t.unify(this);
      } else {
         return false;
      }
   }

   /**
    * Performs a strict comparison of this term to the specified term.
    * <p>
    * A {@code Structure} is considered strictly equal to another term if:
    * <ul>
    * <li>The other term is of type {@link TermType#STRUCTURE}</li>
    * <li>The two terms have the equal functors</li>
    * <li>The two terms have the same number of arguments</li>
    * <li>All corresponding arguments are strictly equal</li>
    * </ul>
    * 
    * @param t the term to compare this term against
    * @return {@code true} if the given term is strictly equal to this term
    */
   @Override
   public boolean strictEquality(Term t) {
      if (t.getType() != TermType.STRUCTURE) {
         return false;
      }
      Term[] tArgs = t.getArgs();
      if (args.length != tArgs.length) {
         return false;
      }
      if (!functor.equals(t.getName())) {
         return false;
      }
      for (int i = 0; i < args.length; i++) {
         if (!args[i].strictEquality(tArgs[i])) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void backtrack() {
      if (!immutable) {
         TermUtils.backtrack(args);
      }
   }

   /**
    * Returns a {@code String} representation of this term.
    * <p>
    * The value returned will consist of the structure's functor followed be a comma separated list of its arguments
    * enclosed in brackets.
    * <p>
    * Example: {@code functor(arg1, arg2, arg3)}
    */
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(functor);
      sb.append("(");
      boolean first = true;
      if (args != null) {
         for (Term arg : args) {
            if (first) {
               first = false;
            } else {
               sb.append(", ");
            }
            sb.append(arg);
         }
      }
      sb.append(")");
      return sb.toString();
   }
}
