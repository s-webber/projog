package org.projog.core.function.compare;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;

/* SYSTEM TEST
 % %TRUE% b@>=a
 % %TRUE% b@>=b
 % %FALSE% b@>=c
 % %TRUE% b@>=1
 % %FALSE% b@>=b()
 */
/**
 * <code>X@&gt;=Y</code> - term "greater than or equal" test.
 * <p>
 * Succeeds when the term argument <code>X</code> is greater than or equal to the term argument <code>Y</code>.
 * </p>
 */
public final class TermGreaterThanOrEqual extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      return TermComparator.TERM_COMPARATOR.compare(arg1, arg2) > -1;
   }
}