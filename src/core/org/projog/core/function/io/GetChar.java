package org.projog.core.function.io;

import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>get_char(X)</code> - reads a character.
 * <p>
 * <code>get_char(X)</code> - reads the next character from the input stream.
 * </p>
 * <p>
 * The goal succeeds if <code>X</code> can be matched with next character read from the current input stream. Succeeds
 * only once and the operation of moving to the next character is not undone on backtracking.
 * </p>
 */
public final class GetChar extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   public boolean evaluate(Term argument) {
      try {
         char c = (char) getKnowledgeBase().getFileHandles().getCurrentInputStream().read();
         Atom a = new Atom(Character.toString(c));
         return argument.unify(a);
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}