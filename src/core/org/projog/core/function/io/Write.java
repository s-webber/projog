package org.projog.core.function.io;

import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;

/* TEST
 %QUERY write( 1+1 )
 %OUTPUT 1 + 1
 %ANSWER/
 %QUERY write( '+'(1,1) )
 %OUTPUT 1 + 1
 %ANSWER/
 */
/**
 * <code>write(X)</code> - writes a term to the output stream.
 * <p>
 * Writes the term <code>X</code> to the current output stream. <code>write</code> takes account of current operator
 * declarations - thus an infix operator will be printed out between it's arguments. <code>write</code> represents lists
 * as a comma separated sequence of elements enclosed in square brackets.
 * </p>
 * <p>
 * Succeeds only once.
 * </p>
 * 
 * @see #toString(Term)
 */
public final class Write extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg) {
      print(toString(arg));
      return true;
   }

   private String toString(Term t) {
      return new TermFormatter(getKnowledgeBase().getOperands()).toString(t);
   }

   private void print(String s) {
      getKnowledgeBase().getFileHandles().getCurrentOutputStream().print(s);
   }
}