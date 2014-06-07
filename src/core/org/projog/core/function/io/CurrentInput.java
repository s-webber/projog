package org.projog.core.function.io;

import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>current_input(X)</code> - match a term to the current input stream.
 * <p>
 * <code>current_input(X)</code> succeeds if the name of the current input stream matches with <code>X</code>, else
 * fails.
 * </p>
 */
public final class CurrentInput extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term argument) {
      return argument.unify(getKnowledgeBase().getFileHandles().getCurrentInputHandle());
   }
}