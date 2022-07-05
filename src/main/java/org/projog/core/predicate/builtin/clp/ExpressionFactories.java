/*
 * Copyright 2022 S. Webber
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
package org.projog.core.predicate.builtin.clp;

import static org.projog.core.term.TermUtils.castToNumeric;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseUtils;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

/**
 * Maintains a collection of {@link ExpressionFactory} instances.
 * <p>
 * This class provides a mechanism for "plugging in" or "injecting" implementations of {@code org.projog.clp.Expression}
 * at runtime. This mechanism provides an easy way to configure and extend the CLP expressions supported by Projog.
 * </p>
 * <p>
 * Each {@link org.projog.core.kb.KnowledgeBase} has at most one unique {@code ClpExpressions} instance.
 * </p>
 */
public final class ExpressionFactories {
   private final KnowledgeBase kb;
   private final Object lock = new Object();
   private final Map<PredicateKey, String> factoryClassNames = new HashMap<>();
   private final Map<PredicateKey, ExpressionFactory> factoryInstances = new HashMap<>();

   public ExpressionFactories(KnowledgeBase kb) {
      this.kb = kb;
   }

   /**
    * Associates a {@link ExpressionFactory} with this {@code KnowledgeBase}.
    *
    * @param key The name and arity to associate the {@code ExpressionFactory} with.
    * @param operatorClassName The class name of the {@code ExpressionFactory} to be associated with {@code key}.
    * @throws ProjogException if there is already a {@code ExpressionFactory} associated with the {@code PredicateKey}
    */
   public void addExpressionFactory(PredicateKey key, String operatorClassName) {
      synchronized (lock) {
         if (factoryClassNames.containsKey(key)) {
            throw new ProjogException("Already defined CLP expression: " + key);
         } else {
            factoryClassNames.put(key, operatorClassName);
         }
      }
   }

   public Expression toExpression(Term t, Set<ClpVariable> vars) {
      switch (t.getType()) {
         case VARIABLE:
            ClpVariable c = new ClpVariable();
            t.unify(c);
            vars.add(c);
            return c;
         case CLP_VARIABLE:
            ClpVariable e = (ClpVariable) t.getTerm();
            vars.add(e);
            return e;
         case INTEGER:
            return new FixedValue(castToNumeric(t).getLong());
         case ATOM:
         case STRUCTURE:
            PredicateKey key = PredicateKey.createForTerm(t);
            ExpressionFactory factory = getExpressionFactory(key);
            Expression[] args = new Expression[t.getNumberOfArguments()];
            for (int i = 0; i < args.length; i++) {
               args[i] = toExpression(t.getArgument(i), vars);
            }
            return factory.createExpression(args);
         default:
            throw new ProjogException("Cannot get CLP expression for term: " + t + " of type: " + t.getType());
      }
   }

   private ExpressionFactory getExpressionFactory(PredicateKey key) {
      ExpressionFactory e = factoryInstances.get(key);
      if (e != null) {
         return e;
      } else if (factoryClassNames.containsKey(key)) {
         return instantiateExpressionFactory(key);
      } else {
         throw new ProjogException("Cannot find CLP expression: " + key);
      }
   }

   private ExpressionFactory instantiateExpressionFactory(PredicateKey key) {
      synchronized (lock) {
         ExpressionFactory factory = factoryInstances.get(key);
         if (factory == null) {
            factory = instantiateExpressionFactory(factoryClassNames.get(key));
            factoryInstances.put(key, factory);
         }
         return factory;
      }
   }

   private ExpressionFactory instantiateExpressionFactory(String className) {
      try {
         return KnowledgeBaseUtils.instantiate(kb, className);
      } catch (Exception e) {
         throw new RuntimeException("Could not create new ExpressionFactory using: " + className, e);
      }
   }
}
