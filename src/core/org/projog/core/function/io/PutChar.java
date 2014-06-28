package org.projog.core.function.io;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>put_char(X)</code> - writes a character.
 * <p>
 * <code>put_char(X)</code> - writes character <code>X</code> to the output stream.
 * </p>
 * <p>
 * Writes to the current output stream. Succeeds only once and the operation is not undone on backtracking.
 * </p>
 */
public final class PutChar extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term argument) {
      String textToOutput = getAtomName(argument);
      getKnowledgeBase().getFileHandles().getCurrentOutputStream().print(textToOutput);
      return true;
   }
}