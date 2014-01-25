/*
 * Copyright 2013 S Webber
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

import static org.projog.core.term.TermUtils.getAtomName;

import java.util.Arrays;
import java.util.HashMap;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/**
 * Maintains a collection of {@link PredicateFactory} instances.
 * <p>
 * This class provides a mechanism for "plugging in" or "injecting" implementations of {@link PredicateFactory} at
 * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including adding
 * functionality not possible to define in pure Prolog syntax.
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single unique {@code PluginPredicateFactoryFactory} instance.
 */
public final class PluginPredicateFactoryFactory extends AbstractSingletonPredicate { // only public so it is included in javadoc
   private final Object lock = new Object();
   private final HashMap<PredicateKey, PredicateFactory> plugins = new HashMap<>();

   PluginPredicateFactoryFactory() {
      // only created by KnowledgeBase which is in the same package as this class
   }

   /**
    * Adds support for a {@link PredicateFactory} to this factory.
    * <p>
    * The method expects two arguments. The first argument should represent the term to associate with the
    * {@code PredicateFactory}. The second argument should be an {@code Atom} whose name is the class name of the
    * {@code PredicateFactory}. This method will attempt to invoke a no argument constructor on the class specified by
    * the second argument and then call {@link PredicateFactory#setKnowledgeBase(KnowledgeBase)} on the new instance.
    * <p>
    * For example the following code:
    * 
    * <pre>
	 * // assuming peff refers to an instance of PluginPredicateFactoryFactory 
	 * peff.evaluate("is/2", "org.projog.core.function.math.Is");
	 * peff.evaluate("true, "org.projog.core.function.bool.True");
	 * </pre>
    * would associate predicates with the functor 'is' and 2 arguments with the 'Is' {@code PredicateFactory} and atoms
    * with a value of 'true' with the 'True' {@code PredicateFactory}.
    * <p>
    * Rather than being called directly via other Java code it is more common for this class to be called from Prolog
    * source code using the {@code pj_add_predicate/2} predicate hard-coded into every {@link KnowledgeBase}. For
    * example:
    * 
    * <pre>
	 * ?- pj_add_predicate(is/2, 'org.projog.core.function.math.Is').
	 * ?- pj_add_predicate(true, 'org.projog.core.function.bool.True').
	 * </pre>
    * 
    * @return {@code true} if the method succeeded
    * @throws ProjogException if there was a problem adding the specified {@code PredicateFactory}
    * @see KnowledgeBase#getPredicateFactory(PredicateKey)
    */
   @Override
   public boolean evaluate(Term... args) {
      try {
         PredicateKey key = PredicateKey.createFromNameAndArity(args[0]);
         String className = getAtomName(args[1]);
         Class<?> c = Class.forName(className);
         PredicateFactory pf = (PredicateFactory) c.newInstance();
         pf.setKnowledgeBase(getKnowledgeBase());
         addPredicateFactory(key, pf);
         return true;
      } catch (Exception e) {
         throw new ProjogException("Could not register new PredicateFactory using arguments: " + Arrays.toString(args), e);
      }
   }

   /**
    * Associates the specified key with the specified PredicateFactory.
    */
   void addPredicateFactory(PredicateKey key, PredicateFactory ef) {
      synchronized (lock) {
         if (isAlreadyDefined(key)) {
            throw new ProjogException("Already defined: " + key);
         } else {
            plugins.put(key, ef);
         }
      }
   }

   private boolean isAlreadyDefined(PredicateKey key) {
      return plugins.containsKey(key) || getKnowledgeBase().getUserDefinedPredicates().containsKey(key);
   }

   /**
    * Returns the PredicateFactory associated with the specified key.
    */
   PredicateFactory getPredicateFactory(PredicateKey key) {
      return plugins.get(key);
   }
}