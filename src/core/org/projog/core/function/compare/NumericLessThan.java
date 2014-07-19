package org.projog.core.function.compare;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;
import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.Calculatables;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %FALSE 2<1
 %FALSE 2<2
 %TRUE 2<3
 %FALSE 3-1<1
 %FALSE 1+1<4-2
 %TRUE 8/4<9/3
 %FALSE 1.5<3.0/2.0
 */
/**
 * <code>X&lt;Y</code> - numeric "less than" test.
 * <p>
 * Succeeds when the number argument <code>X</code> is less than the number argument <code>Y</code>.
 * </p>
 */
public final class NumericLessThan extends AbstractSingletonPredicate {
   private Calculatables calculatables;

   @Override
   public void init() {
      calculatables = getCalculatables(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, calculatables) == -1;
   }
}