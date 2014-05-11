package org.projog.core.function.io;

import org.projog.core.term.Term;

/* SYSTEM TEST
 % %QUERY% write_canonical( 1+1 )
 % %OUTPUT% +(1, 1)
 % %ANSWER/%
 % %QUERY% write_canonical( '+'(1,1) )
 % %OUTPUT% +(1, 1)
 % %ANSWER/%
 */
/**
 * <code>write_canonical(X)</code> - writes a term to the output stream.
 * <p>
 * Writes the term <code>X</code> to the current output stream. <code>write_canonical</code> does not take account of
 * current operator declarations - thus any structures are printed out with the functor first and the arguments in
 * brackets.
 * </p>
 * <p>
 * Succeeds only once.
 * </p>
 */
public final class WriteCanonical extends org.projog.core.function.AbstractSingletonPredicate {
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
      getKnowledgeBase().getFileHandles().getCurrentOutputStream().print(arg.toString());
      return true;
   }
}