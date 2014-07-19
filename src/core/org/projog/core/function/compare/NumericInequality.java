package org.projog.core.function.compare;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;
import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.Calculatables;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %FALSE 1=\=1
 %FALSE 1.5=\=3.0/2.0
 %FALSE 6*6=\=9*4
 %TRUE 1=\=2
 %TRUE 1+1=\=1-1
 %FALSE X=1, Y=1, X=\=Y
 %QUERY X=1, Y=2, X=\=Y
 %ANSWER
 % X=1
 % Y=2
 %ANSWER
 */
/**
 * <code>X=\=Y</code> - numeric inequality test.
 * <p>
 * Succeeds when the number argument <code>X</code> is <i>not</i> equal to the number argument <code>Y</code>.
 * </p>
 */
public final class NumericInequality extends AbstractSingletonPredicate {
   private Calculatables calculatables;

   @Override
   public void init() {
      calculatables = getCalculatables(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, calculatables) != 0;
   }
}