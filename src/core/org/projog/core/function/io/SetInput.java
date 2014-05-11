package org.projog.core.function.io;

import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-io
 */
/**
 * <code>set_input(X)</code> - sets the current input.
 * <p>
 * <code>set_input(X)</code> sets the current input to the stream represented by <code>X</code>.
 * </p>
 * <p>
 * <code>X</code> will be a term returned as the third argument of <code>open</code>, or the atom
 * <code>user_input</code>, which specifies that input to come from the keyboard.
 * </p>
 */
public final class SetInput extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg) {
      getKnowledgeBase().getFileHandles().setInput(arg);
      return true;
   }
}