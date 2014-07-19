package org.projog.core.function.math;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;

import org.projog.core.Calculatable;
import org.projog.core.Calculatables;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;

/* TEST
 validate_in_range(X) :- Y is random(X), Y>=0, Y<X.
 
 %TRUE validate_in_range(3), validate_in_range(7), validate_in_range(100)

 %QUERY X is random(1)
 %ANSWER X=0
 */
/**
 * <code>random(X)</code> Evaluate to a random integer i for which 0 =< i < X.
 */
public final class Random implements Calculatable {
   private Calculatables calculatables;

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      calculatables = getCalculatables(kb);
   }

   @Override
   public Numeric calculate(Term[] args) {
      long max = calculatables.getNumeric(args[0]).getLong();
      return new IntegerNumber((long) (Math.random() * max));
   }
}
