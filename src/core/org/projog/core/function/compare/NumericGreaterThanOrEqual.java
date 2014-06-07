package org.projog.core.function.compare;

import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE 2>=1
 %TRUE 2>=2
 %FALSE 2>=3
 %TRUE 3-1>=1
 %TRUE 1+1>=4-2
 %FALSE 8/4>=9/3
 %TRUE 1.5>=3.0/2.0
 */
/**
 * <code>X&gt;=Y</code> - numeric "greater than or equal" test.
 * <p>
 * Succeeds when the number argument <code>X</code> is greater than or equal to the number argument <code>Y</code>.
 * </p>
 */
public final class NumericGreaterThanOrEqual extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, getKnowledgeBase()) > -1;
   }
}