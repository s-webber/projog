package org.projog.core.function.compare;

import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE 2>1
 %FALSE 2>2
 %FALSE 2>3
 %TRUE 3-1>1
 %FALSE 1+1>4-2
 %FALSE 8/4>9/3
 %FALSE 1.5>3.0/2.0
 */
/**
 * <code>X&gt;Y</code> - numeric "greater than" test.
 * <p>
 * Succeeds when the number argument <code>X</code> is greater than the number argument <code>Y</code>.
 * </p>
 */
public final class NumericGreaterThan extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   public boolean evaluate(Term arg1, Term arg2) {
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, getKnowledgeBase()) == 1;
   }
}