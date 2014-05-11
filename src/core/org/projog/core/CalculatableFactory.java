package org.projog.core;

import static org.projog.core.term.TermUtils.getAtomName;

import java.util.Arrays;
import java.util.HashMap;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;

/**
 * Maintains a collection of {@link Calculatable} instance.
 * <p>
 * This class provides a mechanism for "plugging in" or "injecting" implementations of {@link Calculatable} at runtime.
 * This mechanism provides an easy way to configure and extend the arithmetic operations supported by Projog.
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single unique {@code CalculatableFactory} instance.
 */
public final class CalculatableFactory extends AbstractSingletonPredicate { // only public so it is included in javadoc
   private final Object lock = new Object();
   private final HashMap<String, Calculatable> calculatables = new HashMap<>();

   CalculatableFactory() {
      // only created by KnowledgeBase which is in the same package as this class
   }

   /**
    * Adds support for a {@link Calculatable} to this factory.
    * <p>
    * The method expects two arguments. The first argument should be an {@code Atom} with the name to associate with the
    * {@code Calculatable}. The second argument should be an {@code Atom} whose name is the class name of the
    * {@code Calculatable}.
    * <p>
    * For example the following code:
    * 
    * <pre>
	 * // assuming cf refers to an instance of CalculatableFactory 
	 * cf.evaluate("+", "org.projog.core.function.math.Add");
	 * </pre>
    * would associate the '+' sign with the {@link org.projog.core.function.math.Add} {@code Calculatable}.
    * <p>
    * Rather than being called directly via other Java code it is more common for this class to be called from Prolog
    * source code using the {@code pj_add_calculatable/2} predicate hard-coded into every {@link KnowledgeBase}. For
    * example:
    * 
    * <pre>
	 * ?- pj_add_calculatable('+', 'org.projog.core.function.math.Add').
	 * </pre>
    * 
    * @return {@code true} if the method succeeded
    * @throws ProjogException if there was a problem adding the specified {@code Calculatable}
    * @see KnowledgeBase#getNumeric(Term)
    */
   @Override
   public boolean evaluate(Term... args) {
      try {
         String key = getAtomName(args[0]);
         String className = getAtomName(args[1]);
         Class<?> c = Class.forName(className);
         Calculatable calculatable = (Calculatable) c.newInstance();
         addCalculatable(key, calculatable);
         return true;
      } catch (Exception e) {
         throw new ProjogException("Could not register new Calculatable using arguments: " + Arrays.toString(args), e);
      }
   }

   private void addCalculatable(String key, Calculatable calculatable) {
      synchronized (lock) {
         if (calculatables.containsKey(key)) {
            throw new ProjogException("Already defined calculatable: " + key);
         } else {
            calculatables.put(key, calculatable);
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
   Numeric getNumeric(Term t) {
      TermType type = t.getType();
      switch (type) {
         case DOUBLE:
         case INTEGER:
            return TermUtils.castToNumeric(t);
         case STRUCTURE:
            Term[] args = t.getArgs();
            return getCalculatable(t.getName()).calculate(getKnowledgeBase(), args);
         case ATOM:
            return getCalculatable(t.getName()).calculate(getKnowledgeBase(), TermUtils.EMPTY_ARRAY);
         default:
            throw new ProjogException("Can't get Numeric for term: " + t + " of type: " + type);
      }
   }

   private Calculatable getCalculatable(String name) {
      Calculatable e = calculatables.get(name);
      if (e == null) {
         throw new ProjogException("Cannot find calculatable: " + name);
      }
      return e;
   }
}