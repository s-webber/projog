/*
 * Copyright 2020 S. Webber
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
package org.projog.core.predicate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseUtils;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.DynamicUserDefinedPredicateFactory;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.Term;

/** Acts as a repository of rules and facts. */
public class Predicates {
   /**
    * Used to coordinate access to {@link javaPredicateClassNames}, {@link #javaPredicateInstances} and
    * {@link #userDefinedPredicates}
    */
   private final Object predicatesLock = new Object();

   /**
    * The class names of "built-in" Java predicates (i.e. not defined using Prolog syntax) associated with this
    * {@code KnowledgeBase}.
    */
   private final Map<PredicateKey, String> javaPredicateClassNames = new HashMap<>();

   /**
    * The instances of "built-in" Java predicates (i.e. not defined using Prolog syntax) associated with this
    * {@code KnowledgeBase}.
    */
   private final Map<PredicateKey, PredicateFactory> javaPredicateInstances = new HashMap<>();

   /**
    * The user-defined predicates (i.e. defined using Prolog syntax) associated with this {@code KnowledgeBase}.
    * <p>
    * Uses TreeMap to enforce predictable ordering for when iterated (e.g. by <code>listing(X)</code>).
    */
   private final Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = new TreeMap<>();

   private final KnowledgeBase kb;

   public Predicates(KnowledgeBase kb) {
      this.kb = kb;
   }

   public Predicate getPredicate(Term t) {
      return getPredicateFactory(t).getPredicate(t.getArgs());
   }

   /**
    * Returns details of all predicates, both user-defined and built-in predicates.
    */
   public Set<PredicateKey> getAllDefinedPredicateKeys() {
      Set<PredicateKey> result = new TreeSet<>();
      result.addAll(javaPredicateClassNames.keySet());
      result.addAll(userDefinedPredicates.keySet());
      return Collections.unmodifiableSet(result);
   }

   /**
    * Returns details of all the user define predicates of this object.
    */
   public Map<PredicateKey, UserDefinedPredicateFactory> getUserDefinedPredicates() {
      return Collections.unmodifiableMap(userDefinedPredicates);
   }

   /**
    * Returns the {@code UserDefinedPredicateFactory} for the specified {@code PredicateKey}.
    * <p>
    * If this object does not already have a {@code UserDefinedPredicateFactory} for the specified {@code PredicateKey}
    * then it will create it.
    *
    * @throws ProjogException if the specified {@code PredicateKey} represents an existing "plugin" predicate
    */
   public UserDefinedPredicateFactory createOrReturnUserDefinedPredicate(PredicateKey key) {
      UserDefinedPredicateFactory userDefinedPredicate;
      synchronized (predicatesLock) { // TODO if already in userDefinedPredicates then avoid need to synch
         if (isExistingJavaPredicate(key)) {
            throw new ProjogException("Cannot replace already defined built-in predicate: " + key);
         }

         userDefinedPredicate = userDefinedPredicates.get(key);

         if (userDefinedPredicate == null) {
            // assume dynamic
            userDefinedPredicate = new DynamicUserDefinedPredicateFactory(kb, key);
            addUserDefinedPredicate(userDefinedPredicate);
         }
      }
      return userDefinedPredicate;
   }

   /**
    * Adds a user defined predicate to this object.
    * <p>
    * Any existing {@code UserDefinedPredicateFactory} with the same {@code PredicateKey} will be replaced.
    *
    * @throws ProjogException if the {@code PredicateKey} of the specified {@code UserDefinedPredicateFactory}
    * represents an existing "plugin" predicate
    */
   public void addUserDefinedPredicate(UserDefinedPredicateFactory userDefinedPredicate) {
      PredicateKey key = userDefinedPredicate.getPredicateKey();
      synchronized (predicatesLock) {
         if (isExistingPredicate(key)) {
            updateExistingPredicate(key, userDefinedPredicate);
         } else {
            userDefinedPredicates.put(key, userDefinedPredicate);
         }
      }
   }

   private void updateExistingPredicate(PredicateKey key, UserDefinedPredicateFactory userDefinedPredicate) {
      if (isExistingJavaPredicate(key)) {
         throw new ProjogException("Cannot replace already defined built-in predicate: " + key);
      }

      UserDefinedPredicateFactory existingUserDefinedPredicateFactory = userDefinedPredicates.get(key);
      if (!existingUserDefinedPredicateFactory.isDynamic()) {
         throw new ProjogException(
                     "Cannot append to already defined user defined predicate as it is not dynamic. You can set the predicate to dynamic by adding the following line to start of the file that the predicate is defined in:\n?- dynamic("
                                 + key
                                 + ").");
      }

      Iterator<ClauseModel> models = userDefinedPredicate.getImplications();
      while (models.hasNext()) {
         existingUserDefinedPredicateFactory.addLast(models.next());
      }
   }

   public PredicateFactory getPreprocessedPredicateFactory(Term term) {
      PredicateFactory pf = getPredicateFactory(term);
      if (pf instanceof PreprocessablePredicateFactory) {
         return ((PreprocessablePredicateFactory) pf).preprocess(term);
      } else {
         return pf;
      }
   }

   /**
    * Returns the {@code PredicateFactory} associated with the specified {@code Term}.
    * <p>
    * If this object has no {@code PredicateFactory} associated with the {@code PredicateKey} of the specified
    * {@code Term} then a new instance of {@link UnknownPredicate} is returned.
    */
   public PredicateFactory getPredicateFactory(Term term) {
      PredicateKey key = PredicateKey.createForTerm(term);
      return getPredicateFactory(key);
   }

   /**
    * Returns the {@code PredicateFactory} associated with the specified {@code PredicateKey}.
    * <p>
    * If this object has no {@code PredicateFactory} associated with the specified {@code PredicateKey} then a new
    * instance of {@link UnknownPredicate} is returned.
    */
   public PredicateFactory getPredicateFactory(PredicateKey key) {
      PredicateFactory predicateFactory = getExistingPredicateFactory(key);
      if (predicateFactory != null) {
         return predicateFactory;
      } else if (javaPredicateClassNames.containsKey(key)) {
         return instantiatePredicateFactory(key);
      } else {
         return unknownPredicate(key);
      }
   }

   private PredicateFactory getExistingPredicateFactory(PredicateKey key) {
      PredicateFactory predicateFactory = javaPredicateInstances.get(key);
      if (predicateFactory != null) {
         return predicateFactory;
      } else {
         return userDefinedPredicates.get(key);
      }
   }

   private PredicateFactory instantiatePredicateFactory(PredicateKey key) {
      synchronized (predicatesLock) {
         PredicateFactory predicateFactory = getExistingPredicateFactory(key);
         if (predicateFactory != null) {
            return predicateFactory;
         } else {
            predicateFactory = instantiatePredicateFactory(javaPredicateClassNames.get(key));
            javaPredicateInstances.put(key, predicateFactory);
            return predicateFactory;
         }
      }
   }

   private PredicateFactory instantiatePredicateFactory(String className) {
      try {
         PredicateFactory predicateFactory = KnowledgeBaseUtils.instantiate(kb, className);
         return predicateFactory;
      } catch (Exception e) {
         throw new RuntimeException("Could not create new PredicateFactory using: " + className, e);
      }
   }

   private PredicateFactory unknownPredicate(PredicateKey key) {
      return new UnknownPredicate(kb, key);
   }

   /**
    * Associates a {@link PredicateFactory} with this {@code KnowledgeBase}.
    * <p>
    * This method provides a mechanism for "plugging in" or "injecting" implementations of {@link PredicateFactory} at
    * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including
    * adding functionality not possible to define in pure Prolog syntax.
    * </p>
    *
    * @param key The name and arity to associate the {@link PredicateFactory} with.
    * @param predicateFactoryClassName The name of a class that implements {@link PredicateFactory}.
    * @throws ProjogException if there is already a {@link PredicateFactory} associated with the {@code PredicateKey}
    */
   public void addPredicateFactory(PredicateKey key, String predicateFactoryClassName) {
      synchronized (predicatesLock) {
         if (isExistingPredicate(key)) {
            throw new ProjogException("Already defined: " + key);
         } else {
            javaPredicateClassNames.put(key, predicateFactoryClassName);
         }
      }
   }

   /**
    * Associates a {@link PredicateFactory} with this {@code KnowledgeBase}.
    * <p>
    * This method provides a mechanism for "plugging in" or "injecting" implementations of {@link PredicateFactory} at
    * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including
    * adding functionality not possible to define in pure Prolog syntax.
    * </p>
    *
    * @param key The name and arity to associate the {@link PredicateFactory} with.
    * @param predicateFactory The {@link PredicateFactory} to be added.
    * @throws ProjogException if there is already a {@link PredicateFactory} associated with the {@code PredicateKey}
    */
   public void addPredicateFactory(PredicateKey key, PredicateFactory predicateFactory) {
      synchronized (predicatesLock) {
         if (isExistingPredicate(key)) {
            throw new ProjogException("Already defined: " + key);
         } else {
            javaPredicateClassNames.put(key, predicateFactory.getClass().getName());
            javaPredicateInstances.put(key, predicateFactory);
         }
      }
   }

   private boolean isExistingPredicate(PredicateKey key) {
      return isExistingJavaPredicate(key) || isExistingUserDefinedPredicate(key);
   }

   private boolean isExistingJavaPredicate(PredicateKey key) {
      return javaPredicateClassNames.containsKey(key);
   }

   private boolean isExistingUserDefinedPredicate(PredicateKey key) {
      return userDefinedPredicates.containsKey(key);
   }
}
