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
package org.projog.core.kb;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Associates arbitrary objects with a {@code KnowledgeBase}.
 * <p>
 * Provides a way to implement a one-to-one relationship between a {@code KnowledgeBase} and its services. i.e. A
 * {@code KnowledgeBase} can be associated with one, and only one, {@code RecordedDatabase} - and a
 * {@code RecordedDatabase} can be associated with one, and only one, {@code KnowledgeBase}.
 * </p>
 */
public final class KnowledgeBaseServiceLocator {
   private static final Map<KnowledgeBase, KnowledgeBaseServiceLocator> CACHE = new ConcurrentHashMap<>();

   /**
    * Returns the {@code KnowledgeBaseServiceLocator} associated with the specified {@code KnowledgeBase}.
    * <p>
    * If no {@code KnowledgeBaseServiceLocator} is already associated with the specified {@code KnowledgeBase} then a
    * new {@code KnowledgeBaseServiceLocator} will be created.
    * </p>
    */
   public static KnowledgeBaseServiceLocator getServiceLocator(KnowledgeBase kb) {
      return CACHE.computeIfAbsent(kb, k -> new KnowledgeBaseServiceLocator(k));
   }

   private final KnowledgeBase kb;
   private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

   /** @see #getServiceLocator */
   private KnowledgeBaseServiceLocator(KnowledgeBase kb) {
      this.kb = Objects.requireNonNull(kb);
   }

   /**
    * Adds the specified {@code instance} with the specified {@code referenceType} as its key.
    *
    * @throws IllegalArgumentException If {@code instance} is not an instance of {@code ReferenceType}.
    * @throws IllegalStateException If there is already a service associated with {@code referenceType}.
    */
   public void addInstance(Class<?> referenceType, Object instance) {
      assertInstanceOf(referenceType, instance);
      Object existingValue = services.putIfAbsent(referenceType, instance);
      if (existingValue != null) {
         throw new IllegalStateException("Already have a service with key: " + referenceType);
      }
   }

   /**
    * Returns the {@code Object} associated the specified {@code instanceType}.
    * <p>
    * If no {@code Object} is already associated with {@code instanceType} then a new instance of {@code instanceType}
    * will be created and associated with {@code instanceType} for future use.
    * </p>
    *
    * @throws RuntimeException if an attempt to instantiate a new instance of the {@code instanceType} fails. e.g. If it
    * does not have a public constructor that accepts either no arguments or a single {@code KnowledgeBase} argument.
    */
   public <T> T getInstance(Class<?> instanceType) {
      return getInstance(instanceType, instanceType);
   }

   /**
    * Returns the {@code Object} associated the specified {@code referenceType}.
    * <p>
    * If no {@code Object} is already associated with {@code referenceType} then a new instance of {@code instanceType}
    * will be created and associated with {@code referenceType} for future use.
    * </p>
    *
    * @param referenceType The class to use as the key to retrieve an existing service.
    * @param instanceType The class to create a new instance of if there is no existing service associated with
    * {@code referenceType}.
    * @throws RuntimeException If an attempt to instantiate a new instance of the {@code instanceType} fails. e.g. If
    * {@code instanceType} does not have a public constructor that accepts either no arguments or a single
    * {@code KnowledgeBase} argument - or if {@code referenceType} is not the same as, nor is a superclass or
    * superinterface of, {@code instanceType}.
    */
   @SuppressWarnings("unchecked")
   public <T> T getInstance(Class<?> referenceType, Class<?> instanceType) {
      return (T) services.computeIfAbsent(referenceType, k -> createInstance(referenceType, instanceType));
   }

   private Object createInstance(Class<?> referenceType, Class<?> instanceType) {
      try {
         assertAssignableFrom(referenceType, instanceType);
         return KnowledgeBaseUtils.newInstance(kb, instanceType);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("Could not create new instance of service: " + instanceType, e);
      }
   }

   private void assertAssignableFrom(Class<?> referenceType, Class<?> instanceType) {
      if (!referenceType.isAssignableFrom(instanceType)) {
         throw new IllegalArgumentException(instanceType + " is not of type: " + referenceType);
      }
   }

   private void assertInstanceOf(Class<?> referenceType, Object instance) {
      if (!referenceType.isInstance(instance)) {
         throw new IllegalArgumentException(instance + " is not of type: " + referenceType);
      }
   }
}
