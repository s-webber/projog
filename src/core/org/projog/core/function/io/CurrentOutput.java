package org.projog.core.function.io;

import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>current_output(X)</code> - match a term to the current output stream.
 * <p>
 * <code>current_output(X)</code> succeeds if the name of the current output stream matches with <code>X</code>, else
 * fails.
 * </p>
 */
public final class CurrentOutput extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term argument) {
      return argument.unify(getKnowledgeBase().getFileHandles().getCurrentOutputHandle());
   }
}