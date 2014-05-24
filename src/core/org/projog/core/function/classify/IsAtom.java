package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* SYSTEM TEST
 % %TRUE% atom(abc)
 % %FALSE% atom(1)
 % %FALSE% atom(X)
 % %FALSE% atom(_)
 % %FALSE% atom(a(b,c))
 % %FALSE% atom([a,b,c]) 
 */
/**
 * <code>atom(X)</code> - checks that a term is an atom.
 * <p>
 * <code>atom(X)</code> succeeds if <code>X</code> currently stands for an atom.
 * </p>
 */
public final class IsAtom extends AbstractSingletonPredicate {
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
      return arg.getType() == TermType.ATOM;
   }
}