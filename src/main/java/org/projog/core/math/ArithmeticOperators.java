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
package org.projog.core.math;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseUtils;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;

/**
 * Maintains a collection of {@link ArithmeticOperator} instances.
 * <p>
 * This class provides a mechanism for "plugging in" or "injecting" implementations of {@link ArithmeticOperator} at
 * runtime. This mechanism provides an easy way to configure and extend the arithmetic operations supported by Projog.
 * </p>
 * <p>
 * Each {@link org.projog.core.kb.KnowledgeBase} has a single unique {@code ArithmeticOperators} instance.
 * </p>
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
    * @param key The name and arity to associate the {@link ArithmeticOperator} with.
    * @param operator The instance of {@code ArithmeticOperator} to be associated with {@code key}.
    * @throws ProjogException if there is already a {@link ArithmeticOperator} associated with the {@code PredicateKey}
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
    * @param key The name and arity to associate the {@link ArithmeticOperator} with.
    * @param operatorClassName The class name of the {@link ArithmeticOperator} to be associated with {@code key}.
    * @throws ProjogException if there is already a {@link ArithmeticOperator} associated with the {@code PredicateKey}
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

   /**
    * @return null if not found
    */
   public ArithmeticOperator getPreprocessedArithmeticOperator(Term argument) {
      if (argument.getType().isNumeric()) {
         return (Numeric) argument.getTerm();
      } else if (argument.getType() == TermType.ATOM || argument.getType() == TermType.STRUCTURE) {
         PredicateKey key = PredicateKey.createForTerm(argument);
         return getPreprocessedArithmeticOperator(key, argument);
      } else {
         return null;
      }
   }

   private ArithmeticOperator getPreprocessedArithmeticOperator(PredicateKey key, Term argument) {
      if (operatorInstances.containsKey(key) || operatorClassNames.containsKey(key)) {
         ArithmeticOperator ao = getArithmeticOperator(key);
         if (ao instanceof PreprocessableArithmeticOperator) {
            return ((PreprocessableArithmeticOperator) ao).preprocess(argument);
         } else {
            return ao;
         }
      } else {
         return null;
      }
   }

   /**
    * @throws ProjogException if not found
    */
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
            operatorInstances.put(key, operator);
         }
         return operator;
      }
   }

   private ArithmeticOperator instantiateArithmeticOperator(String className) {
      try {
         return KnowledgeBaseUtils.instantiate(kb, className);
      } catch (Exception e) {
         throw new RuntimeException("Could not create new ArithmeticOperator using: " + className, e);
      }
   }
}
