package org.projog.core.function.math;

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-arithmetic
 */
/**
 * <code>//</code> - performs integer division.
 * <p>
 * The result will be rounded towards zero. e.g. <code>7 // 2</code> is rounded down to <code>3</code> while
 * <code>-7 // 2</code> is rounded up to <code>-3</code>
 */
public final class IntegerDivide implements Calculatable {
   @Override
   public IntegerNumber calculate(KnowledgeBase kb, Term[] args) {
      final int dividend = toInt(kb, args[0]);
      final int divisor = toInt(kb, args[1]);
      return new IntegerNumber(dividend / divisor);
   }
}