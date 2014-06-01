package org.projog.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventType;
import org.projog.core.event.ProjogEventsObservable;
import org.projog.core.function.io.Write;
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

   private final ProjogEventsObservable observable = new ProjogEventsObservable();
   private final SpyPoints spyPoints = new SpyPoints(this);
   private final FileHandles fileHandles = new FileHandles();
   private final Operands operands = new Operands();
   private final ProjogProperties projogProperties;
   /** The arithmetic functions associated with this {@code KnowledgeBase}. */
   private final Calculatables calculatables = new Calculatables(this);
   private final Write writer = new Write();

   /** Used to coordinate access to {@link #javaPredicates} and {@link #userDefinedPredicates} */
   private final Object predicatesLock = new Object();
   /**
    * The "built-in" Java predicates (i.e. not defined using Prolog syntax) associated with this {@code KnowledgeBase}.
    */
   private final HashMap<PredicateKey, PredicateFactory> javaPredicates = new HashMap<>();
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
      this.projogProperties = projogProperties;

      writer.setKnowledgeBase(this);

      addPredicateFactory(ADD_PREDICATE_KEY, new AddPredicateFactory());
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
      String bootstrapScript = projogProperties.getBootstrapScript();
      ProjogSourceReader.parseResource(this, bootstrapScript);
   }

   public ProjogEventsObservable getProjogEventsObservable() {
      return observable;
   }

   public SpyPoints getSpyPoints() {
      return spyPoints;
   }

   public FileHandles getFileHandles() {
      return fileHandles;
   }

   public Operands getOperands() {
      return operands;
   }

   public ProjogProperties getProjogProperties() {
      return projogProperties;
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
   public void addCalculatable(String functionName, Calculatable calculatable) {
      calculatables.addCalculatable(functionName, calculatable);
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
      PredicateFactory predicateFactory = javaPredicates.get(key);
      if (predicateFactory == null) {
         predicateFactory = userDefinedPredicates.get(key);
         if (predicateFactory == null) {
            ProjogEvent event = new ProjogEvent(ProjogEventType.WARN, "Not defined: " + key, this);
            observable.notifyObservers(event);
            return UnknownPredicate.UNKNOWN_PREDICATE;
         }
      }
      return predicateFactory;
   }

   /**
    * Associates a {@link PredicateFactory} with this {@code KnowledgeBase}.
    * <p>
    * This method provides a mechanism for "plugging in" or "injecting" implementations of {@link PredicateFactory} at
    * runtime. This mechanism provides an easy way to configure and extend the functionality of Projog - including adding
    * functionality not possible to define in pure Prolog syntax.
    * </p>
    */
   public void addPredicateFactory(PredicateKey key, PredicateFactory pf) {
      synchronized (predicatesLock) {
         if (isExistingPredicate(key)) {
            throw new ProjogException("Already defined: " + key);
         } else {
            pf.setKnowledgeBase(this);
            javaPredicates.put(key, pf);
         }
      }
   }

   private boolean isExistingPredicate(PredicateKey key) {
      return isExistingJavaPredicate(key) || userDefinedPredicates.containsKey(key);
   }

   private boolean isExistingJavaPredicate(PredicateKey key) {
      return javaPredicates.containsKey(key);
   }

   /**
    * Returns a string representation of the specified {@code Term}.
    * 
    * @param t the {@code Term} to represent as a string
    * @return a string representation of the specified {@code Term}
    * @see org.projog.core.function.io.Write#toString(Term)
    */
   public String toString(Term t) {
      return writer.toString(t);
   }
}