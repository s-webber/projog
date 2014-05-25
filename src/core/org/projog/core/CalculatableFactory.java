package org.projog.core;

import java.util.HashMap;

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
final class CalculatableFactory {
   private final KnowledgeBase knowledgeBase;
   private final Object lock = new Object();
   private final HashMap<String, Calculatable> calculatables = new HashMap<>();

   CalculatableFactory(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
   }

   void addCalculatable(String key, Calculatable calculatable) {
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
            return getCalculatable(t.getName()).calculate(knowledgeBase, args);
         case ATOM:
            return getCalculatable(t.getName()).calculate(knowledgeBase, TermUtils.EMPTY_ARRAY);
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