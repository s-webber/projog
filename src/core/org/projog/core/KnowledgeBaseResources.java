package org.projog.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Associates arbitrary objects with a {@code KnowledgeBase}.
 * <p>
 * Provides a way to implement a one-to-one relationship between a {@code KnowledgeBase} and its resources. i.e. A
 * {@code KnowledgeBase} can be associated with one, and only one, {@code SpyPoints} - and a {@code SpyPoints} can be
 * associated with one, and only one, {@code KnowledgeBase}.
 */
public class KnowledgeBaseResources {
   private static final Map<KnowledgeBase, KnowledgeBaseResources> CACHE = new HashMap<>();

   /**
    * Returns the {@code KnowledgeBaseResources} associated with the specified {@code KnowledgeBase}.
    * <p>
    * If no {@code KnowledgeBaseResources} is already associated with the specified {@code KnowledgeBase} then a new
    * {@code KnowledgeBaseResources} will be created.
    * </p>
    */
   public static KnowledgeBaseResources getKnowledgeBaseResources(KnowledgeBase kb) {
      KnowledgeBaseResources l = CACHE.get(kb);
      if (l == null) {
         l = createKnowledgeBaseResources(kb);
      }
      return l;
   }

   private static KnowledgeBaseResources createKnowledgeBaseResources(KnowledgeBase kb) {
      synchronized (CACHE) {
         KnowledgeBaseResources l = CACHE.get(kb);
         if (l == null) {
            l = new KnowledgeBaseResources(kb);
            CACHE.put(kb, l);
         }
         return l;
      }
   }

   private final KnowledgeBase kb;
   private final Map<Class<?>, Object> resources = new HashMap<>();

   /** @see #getKnowledgeBaseResources */
   private KnowledgeBaseResources(KnowledgeBase kb) {
      this.kb = kb;
   }

   /**
    * Adds the specified {@code instance} with the specified {@code referenceType} as its key.
    * 
    * @throws IllegalArgumentException If {@code instance} is not an instance of {@code ReferenceType}.
    * @throws IlegalStateException If there is already a resource associated with {@code referenceType}.
    */
   public void addResource(Class<?> referenceType, Object instance) {
      assertInstanceOf(referenceType, instance);
      synchronized (resources) {
         Object r = resources.get(referenceType);
         if (r == null) {
            resources.put(referenceType, instance);
         } else {
            throw new IllegalStateException("Already have a resource with key: " + referenceType);
         }
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
   public <T> T getResource(Class<?> instanceType) {
      return getResource(instanceType, instanceType);
   }

   /**
    * Returns the {@code Object} associated the specified {@code referenceType}.
    * <p>
    * If no {@code Object} is already associated with {@code referenceType} then a new instance of {@code instanceType}
    * will be created and associated with {@code referenceType} for future use.
    * </p>
    * 
    * @param referenceType The class to use as the key to retrieve an existing resource.
    * @param instanceType The class to create a new instance of if there is no existing resource associated with
    * {@code referenceType}.
    * @throws RuntimeException If an attempt to instantiate a new instance of the {@code instanceType} fails. e.g. If
    * {@code instanceType} does not have a public constructor that accepts either no arguments or a single
    * {@code KnowledgeBase} argument - or if {@code referenceType} is not the same as, nor is a superclass or
    * superinterface of, {@code instanceType}.
    */
   @SuppressWarnings("unchecked")
   public <T> T getResource(Class<?> referenceType, Class<?> instanceType) {
      Object r = resources.get(referenceType);
      if (r == null) {
         r = createResource(referenceType, instanceType);
      }
      return (T) r;
   }

   private Object createResource(Class<?> referenceType, Class<?> instanceType) {
      synchronized (resources) {
         Object r = resources.get(referenceType);
         if (r == null) {
            assertAssignableFrom(referenceType, instanceType);
            r = newInstance(instanceType);
            resources.put(referenceType, r);
         }
         return r;
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

   private Object newInstance(Class<?> c) {
      try {
         Constructor<?> constructor = getKnowledgeBaseArgumentConstructor(c);
         if (constructor != null) {
            return constructor.newInstance(kb);
         } else {
            return c.newInstance();
         }
      } catch (Exception e) {
         throw new RuntimeException("Could not create new instance of resource: " + c, e);
      }
   }

   private Constructor<?> getKnowledgeBaseArgumentConstructor(Class<?> c) throws InstantiationException,
               IllegalAccessException, InvocationTargetException {
      for (Constructor<?> constructor : c.getConstructors()) {
         Class<?>[] parameterTypes = constructor.getParameterTypes();
         if (parameterTypes.length == 1 && parameterTypes[0] == KnowledgeBase.class) {
            return constructor;
         }
      }
      return null;
   }
}
