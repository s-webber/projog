package org.projog.core.function.debug;

import org.projog.core.term.Term;

/* TEST
 %LINK prolog-debugging
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

   public boolean evaluate() {
      getKnowledgeBase().getSpyPoints().setTraceEnabled(true);
      return true;
   }
}