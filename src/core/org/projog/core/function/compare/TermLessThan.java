package org.projog.core.function.compare;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;

/* SYSTEM TEST
 % %FALSE% b@<a
 % %FALSE% b@<b
 % %TRUE% b@<c
 % %FALSE% b@<1
 % %TRUE% b@<b()
 */
/**
 * <code>X@&lt;Y</code> - term "less than" test.
 * <p>
 * Succeeds when the term argument <code>X</code> is less than the term argument <code>Y</code>.
 * </p>
 */
public final class TermLessThan extends AbstractSingletonPredicate {
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
      return TermComparator.TERM_COMPARATOR.compare(arg1, arg2) == -1;
   }
}