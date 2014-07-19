package org.projog.core.function.math;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;
import static org.projog.core.term.TermUtils.toLong;

import org.projog.core.Calculatable;
import org.projog.core.Calculatables;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * A template for {@code Calculatable}s that accept two arguments of type {@link TermType#INTEGER}.
 */
abstract class AbstractTwoIntegerArgumentsCalculatable implements Calculatable {
   private Calculatables calculatables;

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      calculatables = getCalculatables(kb);
   }

   @Override
   public final Numeric calculate(Term[] args) {
      final long i1 = toLong(calculatables, args[0]);
      final long i2 = toLong(calculatables, args[1]);
      return new IntegerNumber(calculateLong(i1, i2));
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract long calculateLong(long n1, long n2);
}
