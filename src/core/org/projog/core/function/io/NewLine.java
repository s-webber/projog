package org.projog.core.function.io;

import org.projog.core.term.Term;

/* SYSTEM TEST
 % %QUERY% write('a'), write('b'), nl, write('c')
 % %OUTPUT%
 % ab
 % c
 % %OUTPUT%
 % %ANSWER/%
 */
/**
 * <code>nl</code> - outputs a new line character.
 * <p>
 * Causes a line break to be output to the current stream.
 * </p>
 * <p>
 * This goal succeeds only once.
 * </p>
 */
public final class NewLine extends org.projog.core.function.AbstractSingletonPredicate {
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
      getKnowledgeBase().getFileHandles().getCurrentOutputStream().println();
      return true;
   }
}