package org.projog.core.function.debug;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-debugging
 */
/**
 * <code>notrace</code> - disables exhaustive tracing.
 * <p>
 * By disabling exhaustive tracing the programmer will no longer be informed of every goal their program attempts to
 * resolve. Any tracing due to the presence of spy points <i>will</i> continue.
 * </p>
 */
public final class NoTrace extends AbstractSingletonPredicate {
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
      getKnowledgeBase().getSpyPoints().setTraceEnabled(false);
      return true;
   }
}