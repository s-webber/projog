package org.projog.core.function.math;

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/** 
 * Performs integer division.
 * <p>
 * The result will be rounded down to the nearest whole number.
 */
public final class IntegerDivide implements Calculatable {
   @Override
   public IntegerNumber calculate(KnowledgeBase kb, Term[] args) {
      final int dividend = toInt(args[0]);
      final int divisor = toInt(args[1]);
      return new IntegerNumber(dividend / divisor);
   }
}