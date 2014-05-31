package org.projog.core.function.bool;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE true
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

   public boolean evaluate() {
      return true;
   }
}