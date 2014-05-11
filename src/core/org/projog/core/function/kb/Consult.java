package org.projog.core.function.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.ProjogSourceReader;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-io
 */
/**
 * <code>consult(X)</code> - reads clauses and goals from a file.
 * <p>
 * <code>consult(X)</code> reads clauses and goals from a file. <code>X</code> must be instantiated to the name of a
 * text file containing Prolog clauses and goals which will be added to the knowledge base.
 * </p>
 */
public final class Consult extends AbstractSingletonPredicate {
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
      String resourceName = getAtomName(arg);
      ProjogSourceReader.parseResource(getKnowledgeBase(), resourceName);
      return true;
   }
}