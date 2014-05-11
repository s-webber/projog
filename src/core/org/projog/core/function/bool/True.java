package org.projog.core.function.bool;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %TRUE% true
 */
/**
 * <code>true</code> - always succeeds.
 * <p>
 * The goal <code>true</code> always succeeds.
 * </p>
 */
public final class True extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate();
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate() {
      return true;
   }
}