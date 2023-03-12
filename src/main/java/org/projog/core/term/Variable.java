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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an unspecified {@link Term}.
 * <p>
 * A {@code Variable} can be either instantiated (representing another single {@link Term}) or uninstantiated (not
 * representing any other {@link Term}). {@code Variable}s are not constants. What {@link Term}, if any, a
 * {@code Variable} is instantiated with can vary during its life time. A {@code Variable} becomes instantiated by calls
 * to {@link #unify(Term)} and becomes uninstantiated again by calls to {@link #backtrack()}.
 */
public final class Variable implements Term {
   public static final String ANONYMOUS_VARIABLE_ID = "_";

   /**
    * The value by which the variable can be identified
    */
   private final String id;

   /**
    * The {@link Term} this object is currently instantiated with (or {@code null} if it is currently uninstantiated)
    */
   private Term value;

   /**
    * A collection of attributes associated with the variable.
    */
   private final Map<VariableAttribute, Term> attributes;

   /**
    * Creates an anonymous variable. The ID of the variable will be an underscore.
    */
   public Variable() {
      this(ANONYMOUS_VARIABLE_ID);
   }

   /**
    * @param id value by which this variable can be identified
    */
   public Variable(String id) {
      this.id = id;
      this.attributes = null;
   }

   private Variable(String id, Map<VariableAttribute, Term> attributes) {
      this.id = id;
      this.attributes = attributes;
   }

   public Term getAttributeOrDefault(VariableAttribute attributeKey, Term defaultValue) {
      if (value != null) {
         throw new IllegalStateException();
      }

      return attributes == null ? defaultValue : attributes.getOrDefault(attributeKey, defaultValue);
   }

   public void putAttribute(VariableAttribute attributeKey, Term attributeValue) {
      if (value != null) {
         throw new IllegalStateException();
      }

      Map<VariableAttribute, Term> updatedAttributes;
      if (attributes == null) {
         updatedAttributes = Collections.singletonMap(attributeKey, attributeValue);
      } else {
         updatedAttributes = new HashMap<>(attributes);
         updatedAttributes.put(attributeKey, attributeValue);
      }

      value = new Variable(id, updatedAttributes);
   }

   public void removeAttribute(VariableAttribute attributeKey) {
      if (value != null) {
         throw new IllegalStateException();
      } else if (attributes == null || !attributes.containsKey(attributeKey)) {
         // do nothing
      } else if (attributes.size() == 1) {
         value = new Variable(id);
      } else {
         Map<VariableAttribute, Term> updatedAttributes = new HashMap<>(attributes);
         updatedAttributes.remove(attributeKey);
         value = new Variable(id, updatedAttributes);
      }
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

   public boolean isAnonymous() {
      return ANONYMOUS_VARIABLE_ID.equals(id);
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
      t = t.getBound();

      if (t == this || t == value) {
         return true;
      } else if (value != null) {
         return getValue().unify(t.getTerm());
      } else if (t.getType().isVariable()) {
         Variable otherVariable = (Variable) t.getTerm();
         if (attributes == null && otherVariable.attributes == null) {
            value = t;
            return true;
         } else if (attributes == null) {
            value = otherVariable;
            return otherVariable.postUnify();
         } else if (otherVariable.attributes == null) {
            otherVariable.value = this;
            return this.postUnify();
         } else {
            return unifyVariablesWithAttributes(this, otherVariable);
         }
      } else {
         value = t;
         return postUnify();
      }
   }

   /** Unify two variables that both have attributes. */
   private static boolean unifyVariablesWithAttributes(Variable v1, Variable v2) {
      Map<VariableAttribute, Term> copy = new HashMap<>();

      // Add attributes in v1 to map. If attribute also appears in v2 then join their associated attribute values.
      for (Map.Entry<VariableAttribute, Term> a : v1.attributes.entrySet()) {
         if (v2.attributes.containsKey(a.getKey())) {
            Term join = a.getKey().join(a.getValue(), v2.attributes.get(a.getKey()));
            copy.put(a.getKey(), join);
         } else {
            copy.put(a.getKey(), a.getValue());
         }
      }

      // Add attributes in v2 to map if not an attribute of v1.
      for (Map.Entry<VariableAttribute, Term> a : v2.attributes.entrySet()) {
         if (!copy.containsKey(a.getKey())) {
            copy.put(a.getKey(), a.getValue());
         }
      }

      // create new variable using attributes in map and unify with v1 and v2.
      Variable c = new Variable(v2.getId(), copy);
      v1.value = c;
      v2.value = c;

      return c.postUnify();
   }

   private boolean postUnify() {
      if (attributes == null) {
         return true;
      }

      for (Map.Entry<VariableAttribute, Term> a : attributes.entrySet()) {
         Term attributeValue = a.getValue().getTerm();
         if (!a.getKey().postUnify(this, attributeValue)) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns {@link TermType#VARIABLE} if uninstantiated else {@link TermType} of instantiated {@link Term}.
    *
    * @return {@link TermType#VARIABLE} if this variable is uninstantiated else calls {@link Term#getType()} on the
    * {@link Term} this variable is instantiated with.
    */
   @Override
   public TermType getType() {
      if (value == null) {
         return TermType.VARIABLE;
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
            result = new Variable(id, attributes);
            sharedVariables.put(this, result);
         }
         return result.getTerm();
      } else {
         return getValue().copy(sharedVariables);
      }
   }

   @Override
   public Term getBound() {
      return value == null ? this : getValue();
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
         } while (t != value);

         throw new IllegalStateException();
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
