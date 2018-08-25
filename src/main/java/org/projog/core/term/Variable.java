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
 * Represents an unspecified {@link Term}.
 * <p>
 * A {@code Variable} can be either instantiated (representing another single {@link Term}) or uninstantiated (not
 * representing any other {@link Term}). {@code Variable}s are not constants. What {@link Term}, if any, a
 * {@code Variable} is instantiated with can vary during its life time. A {@code Variable} becomes instantiated by
 * calls to {@link #unify(Term)} and becomes uninstantiated again by calls to {@link #backtrack()}.
 * <p>
 * <img src="doc-files/Variable.png">
 */
public final class Variable implements Term {
   /**
    * The value by which the variable can be identified
    */
   private final String id;

   /**
    * The {@link Term} this object is currently instantiated with (or {@code null} if it is currently uninstantiated)
    */
   private Term value;

   /**
    * @param id value by which this variable can be identified
    */
   public Variable(String id) {
      this.id = id;
   }

   /**
    * Calls {@link Term#getName()} on the {@link Term} this variable is instantiated with.
    * 
    * @throws NullPointerException if the {@code Variable} is currently uninstantiated
    */
   @Override
   public String getName() {
      if (value == null) {
         throw new NullPointerException();
      }
      return getValue().getName();
   }

   /**
    * @return value provided in constructor by which this variable can be identified
    */
   public String getId() {
      return id;
   }

   /**
    * Calls {@link Term#getArgs()} on the {@link Term} this variable is instantiated with.
    * 
    * @throws NullPointerException if the {@code Variable} is currently uninstantiated
    */
   @Override
   public Term[] getArgs() {
      if (value == null) {
         throw new NullPointerException();
      }
      return getValue().getArgs();
   }

   /**
    * Calls {@link Term#getNumberOfArguments()} on the {@link Term} this variable is instantiated with.
    * 
    * @throws NullPointerException if the {@code Variable} is currently uninstantiated
    */
   @Override
   public int getNumberOfArguments() {
      if (value == null) {
         throw new NullPointerException();
      }
      return getValue().getNumberOfArguments();
   }

   /**
    * Calls {@link Term#getArgument(int)} on the {@link Term} this variable is instantiated with.
    * 
    * @throws NullPointerException if the {@code Variable} is currently uninstantiated
    */
   @Override
   public Term getArgument(int index) {
      if (value == null) {
         throw new NullPointerException();
      }
      return getValue().getArgument(index);
   }

   @Override
   public boolean unify(Term t) {
      if (value == null) {
         if (this != t) {
            value = t;
         }
         return true;
      } else {
         return getValue().unify(t.getTerm());
      }
   }

   @Override
   public boolean strictEquality(Term t) {
      boolean b;
      if (this == t) {
         b = true;
      } else if (value != null) {
         b = getValue().strictEquality(t);
      } else if (t.getType() == TermType.NAMED_VARIABLE && ((Variable) t).value != null) {
         // this is for when two unassigned variables are unified with each other
         b = t.strictEquality(this);
      } else {
         b = false;
      }
      return b;
   }

   /**
    * Returns {@link TermType#NAMED_VARIABLE} if uninstantiated else {@link TermType} of instantiated {@link Term}.
    * 
    * @return {@link TermType#NAMED_VARIABLE} if this variable is uninstantiated else calls {@link Term#getType()} on
    * the {@link Term} this variable is instantiated with.
    */
   @Override
   public TermType getType() {
      if (value == null) {
         return TermType.NAMED_VARIABLE;
      } else {
         return getValue().getType();
      }
   }

   /**
    * Always returns {@code false} even if instantiated with an immutable {@link Term}.
    * 
    * @return {@code false}
    */
   @Override
   public boolean isImmutable() {
      return false;
   }

   @Override
   public Term copy(Map<Variable, Variable> sharedVariables) {
      if (value == null) {
         Variable result = sharedVariables.get(this);
         if (result == null) {
            result = new Variable(id);
            sharedVariables.put(this, result);
         }
         return result.getTerm();
      } else {
         return getValue().copy(sharedVariables);
      }
   }

   /**
    * @return itself if this variable is uninstantiated else calls {@link Term#getType()} on the {@link Term} this
    * variable is instantiated with.
    */
   @Override
   public Term getTerm() {
      if (value == null) {
         return this;
      } else {
         return getValue().getTerm();
      }
   }

   private Term getValue() {
      if (value.getClass() == Variable.class) {
         // if variable assigned to another variable use while loop
         // rather than value.getTerm() to avoid StackOverflowError
         Term t = value;
         do {
            Variable v = (Variable) t;
            if (v.value == null) {
               return v;
            }
            if (v.value.getClass() != Variable.class) {
               return v.value;
            }
            t = v.value;
         } while (true);
      } else {
         return value;
      }
   }

   /**
    * Reverts this variable to an uninstantiated state.
    */
   @Override
   public void backtrack() {
      value = null;
   }

   /**
    * @return if this variable is uninstantiated then returns this variable's id else calls {@code toString()} on the
    * {@link Term} this variable is instantiated with.
    */
   @Override
   public String toString() {
      if (value == null) {
         return id;
      } else {
         return getValue().toString();
      }
   }
}
