package org.projog.core.function.compare;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermComparator;

/* TEST
 %FALSE b@<a
 %FALSE b@<b
 %TRUE b@<c
 %FALSE b@<1
 %TRUE b@<b()
 */
/**
 * <code>X@&lt;Y</code> - term "less than" test.
 * <p>
 * Succeeds when the term argument <code>X</code> is less than the term argument <code>Y</code>.
 * </p>
 */
public final class TermLessThan extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return TermComparator.TERM_COMPARATOR.compare(arg1, arg2) == -1;
   }
}