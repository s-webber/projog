package org.projog.core;

import static org.projog.core.KnowledgeBaseResources.getKnowledgeBaseResources;
import static org.projog.core.KnowledgeBaseUtils.getProjogEventsObservable;
import static org.projog.core.KnowledgeBaseUtils.getProjogProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventType;
import org.projog.core.function.kb.AddPredicateFactory;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.udp.DynamicUserDefinedPredicateFactory;
import org.projog.core.udp.UserDefinedPredicateFactory;

/**
 * Acts as a repository of rules and facts.
 * <p>
 * The central object that connects the various components of an instance of the "core" inference engine.
 * <p>
 * <img src="doc-files/KnowledgeBase.png">
 */
public final class KnowledgeBase {
   /**
    * Represents the {@code pj_add_predicate/2} predicate hard-coded in every {@code KnowledgeBase}.
    * <p>
    * The {@code pj_add_predicate/2} predicate allows other implementations of {@link PredicateFactory} to be
    * "plugged-in" to a {@code KnowledgeBase} at runtime using Prolog syntax.
    * 
    * @see AddPredicateFactory#evaluate(Term[])
    */
   private static final PredicateKey ADD_PREDICATE_KEY = new PredicateKey("pj_add_predicate", 2);

   /** The arithmetic functions associated with this {@code KnowledgeBase}. */
   private final Calculatables calculatables = new Calculatables(this);

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

   /**
    * Constructs a new {@code KnowledgeBase} object using {@link ProjogSystemProperties}
    */
   public KnowledgeBase() {
      this(new ProjogSystemProperties());
   }

   /**
    * Constructs a new {@code KnowledgeBase} object using the specified {@link ProjogProperties}
    */
   public KnowledgeBase(ProjogProperties projogProperties) {
      addPredicateFactory(ADD_PREDICATE_KEY, AddPredicateFactory.class.getName());
      getKnowledgeBaseResources(this).addResource(ProjogProperties.class, projogProperties);
   }

   /**
    * Consults the {@link ProjogProperties#getBootstrapScript()} for this object.
    * <p>
    * This is a way to configure a new {@code KnowledgeBase} (i.e. plugging in {@link Calculatable} and
    * {@link PredicateFactory} instances).
    * <p>
    * When using {@link ProjogSystemProperties} the resource parsed will be {@code projog-bootstrap.pl} (contained in
    * {@code projog-core.jar}).
    * 
    * @link ProjogSourceReader#parseResource(KnowledgeBase, String)
    */
   public void bootstrap() {
      String bootstrapScript = getProjogProperties(this).getBootstrapScript();
      ProjogSourceReader.parseResource(this, bootstrapScript);
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
      return calculatables.getNumeric(t);
   }

   /**
    * Associates a {@link Calculatable} with this {@code KnowledgeBase}.
    */
   public void addCalculatable(PredicateKey key, Calculatable calculatable) {
      calculatables.addCalculatable(key, calculatable);
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
      synchronized (predicatesLock) {
         userDefinedPredicate = userDefinedPredicates.get(key);

         if (userDefinedPredicate == null) {
            // assume dynamic
            userDefinedPredicate = new DynamicUserDefinedPredicateFactory(this, key);
            setUserDefinedPredicate(userDefinedPredicate);
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
   public void setUserDefinedPredicate(UserDefinedPredicateFactory userDefinedPredicate) {
      PredicateKey key = userDefinedPredicate.getPredicateKey();
      synchronized (predicatesLock) {
         if (isExistingJavaPredicate(key)) {
            throw new ProjogException("Cannot replace already defined plugin predicate: " + key);
         }

         userDefinedPredicates.put(key, userDefinedPredicate);
      }
   }

   /**
    * Returns the {@code PredicateFactory} associated with the specified {@code Term}.
    * <p>
    * If this object has no {@code PredicateFactory} associated with the {@code PredicateKey} of the specified
    * {@code Term} then {@link UnknownPredicate#UNKNOWN_PREDICATE} is returned.
    */
   public PredicateFactory getPredicateFactory(Term term) {
      PredicateKey key = PredicateKey.createForTerm(term);
      return getPredicateFactory(key);
   }

   /**
    * Returns the {@code PredicateFactory} associated with the specified {@code PredicateKey}.
    * <p>
    * If this object has no {@code PredicateFactory} associated with the specified {@code PredicateKey} then
    * {@link UnknownPredicate#UNKNOWN_PREDICATE} is returned.
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
         Class<?> c = Class.forName(className);
         PredicateFactory predicateFactory = (PredicateFactory) c.newInstance();
         predicateFactory.setKnowledgeBase(this);
         return predicateFactory;
      } catch (Exception e) {
         throw new RuntimeException("Could not create new PredicateFactory", e);
      }
   }

   private PredicateFactory unknownPredicate(PredicateKey key) {
      ProjogEvent event = new ProjogEvent(ProjogEventType.WARN, "Not defined: " + key, this);
      getProjogEventsObservable(this).notifyObservers(event);
      return UnknownPredicate.UNKNOWN_PREDICATE;
   }

   /**
    * Associates a {@link PredicateFactory} with this {@code KnowledgeBase}.
    * <p>
    * This method provides a mechanism for "plugging in" or "injecting" implementations of {@link PredicateFactory} at
    * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including
    * adding functionality not possible to define in pure Prolog syntax.
    * </p>
    */
   public void addPredicateFactory(PredicateKey key, String className) {
      synchronized (predicatesLock) {
         if (isExistingPredicate(key)) {
            throw new ProjogException("Already defined: " + key);
         } else {
            javaPredicateClassNames.put(key, className);
         }
      }
   }

   private boolean isExistingPredicate(PredicateKey key) {
      return isExistingJavaPredicate(key) || userDefinedPredicates.containsKey(key);
   }

   private boolean isExistingJavaPredicate(PredicateKey key) {
      return javaPredicateClassNames.containsKey(key);
   }
}