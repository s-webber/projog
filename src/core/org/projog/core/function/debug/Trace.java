package org.projog.core.function.debug;

import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-debugging
 */
/**
 * <code>trace</code> - enables exhaustive tracing.
 * <p>
 * By enabling exhaustive tracing the programmer will be informed of every goal their program attempts to resolve.
 * </p>
 */
public final class Trace extends org.projog.core.function.AbstractSingletonPredicate {
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
      getKnowledgeBase().getSpyPoints().setTraceEnabled(true);
      return true;
   }
}