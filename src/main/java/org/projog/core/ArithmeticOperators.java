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
package org.projog.core;

import static org.projog.core.CoreUtils.instantiate;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;

/**
 * Maintains a collection of {@link ArithmeticOperator} instances.
 * <p>
 * This class provides a mechanism for "plugging in" or "injecting" implementations of {@link ArithmeticOperator} at
 * runtime. This mechanism provides an easy way to configure and extend the arithmetic operations supported by Projog.
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single unique {@code ArithmeticOperators} instance.
 */
public final class ArithmeticOperators {
   private final KnowledgeBase kb;
   private final Object lock = new Object();
   private final Map<PredicateKey, String> operatorClassNames = new HashMap<>();
   private final Map<PredicateKey, ArithmeticOperator> operatorInstances = new HashMap<>();

   public ArithmeticOperators(KnowledgeBase kb) {
      this.kb = kb;
   }

   /**
    * Associates a {@link ArithmeticOperator} with this {@code KnowledgeBase}.
    *
    * @param operator The instance of {@code ArithmeticOperator} to be associated with {@code key}.
    */
   public void addArithmeticOperator(PredicateKey key, ArithmeticOperator operator) {
      synchronized (lock) {
         if (operatorClassNames.containsKey(key)) {
            throw new ProjogException("Already defined operator: " + key);
         } else {
            operatorClassNames.put(key, operator.getClass().getName());
            operatorInstances.put(key, operator);
         }
      }
   }

   /**
    * Associates a {@link ArithmeticOperator} with this {@code KnowledgeBase}.
    *
    * @param operatorClassName The class name of the {@link ArithmeticOperator} to be associated with {@code key}.
    */
   public void addArithmeticOperator(PredicateKey key, String operatorClassName) {
      synchronized (lock) {
         if (operatorClassNames.containsKey(key)) {
            throw new ProjogException("Already defined operator: " + key);
         } else {
            operatorClassNames.put(key, operatorClassName);
         }
      }
   }

   /**
    * Returns the result of evaluating the specified arithmetic expression.
    *
    * @param t a {@code Term} that can be evaluated as an arithmetic expression (e.g. a {@code Structure} of the form
    * {@code +(1,2)} or a {@code Numeric})
    * @return the result of evaluating the specified arithmetic expression
    * @throws ProjogException if the specified term does not represent an arithmetic expression
    */
   public Numeric getNumeric(Term t) {
      TermType type = t.getType();
      switch (type) {
         case FRACTION:
         case INTEGER:
            return TermUtils.castToNumeric(t);
         case STRUCTURE:
            return calculate(t, t.getArgs());
         case ATOM:
            return calculate(t, TermUtils.EMPTY_ARRAY);
         default:
            throw new ProjogException("Cannot get Numeric for term: " + t + " of type: " + type);
      }
   }

   private Numeric calculate(Term term, Term[] args) {
      PredicateKey key = PredicateKey.createForTerm(term);
      return getArithmeticOperator(key).calculate(args);
   }

   public ArithmeticOperator getArithmeticOperator(PredicateKey key) {
      ArithmeticOperator e = operatorInstances.get(key);
      if (e != null) {
         return e;
      } else if (operatorClassNames.containsKey(key)) {
         return instantiateArithmeticOperator(key);
      } else {
         throw new ProjogException("Cannot find arithmetic operator: " + key);
      }
   }

   private ArithmeticOperator instantiateArithmeticOperator(PredicateKey key) {
      synchronized (lock) {
         ArithmeticOperator operator = operatorInstances.get(key);
         if (operator == null) {
            operator = instantiateArithmeticOperator(operatorClassNames.get(key));
            operator.setKnowledgeBase(kb);
            operatorInstances.put(key, operator);
         }
         return operator;
      }
   }

   private ArithmeticOperator instantiateArithmeticOperator(String className) {
      try {
         return instantiate(className);
      } catch (Exception e) {
         throw new RuntimeException("Could not create new ArithmeticOperator using: " + className, e);
      }
   }
}
