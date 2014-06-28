package org.projog.core.function.math;

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * A template for {@code Calculatable}s that accept two arguments of type {@link TermType#INTEGER}.
 */
abstract class AbstractTwoIntegerArgumentsCalculatable implements Calculatable {
   @Override
   public final Numeric calculate(KnowledgeBase kb, Term[] args) {
      final int i1 = toInt(kb, args[0]);
      final int i2 = toInt(kb, args[1]);
      return new IntegerNumber(calculateInt(i1, i2));
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract int calculateInt(int i1, int i2);
}
