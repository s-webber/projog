package org.projog.core.function.io;

import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-io
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
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term argument) {
      return argument.unify(getKnowledgeBase().getFileHandles().getCurrentInputHandle());
   }
}