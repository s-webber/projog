package org.projog.core.function.math;

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/**
 * Finds the remainder of division of one number by another.
 * <p>
 * The result has the same sign as the divisor (i.e. second argument).
 */
public final class Modulo implements Calculatable {
   @Override
   public IntegerNumber calculate(KnowledgeBase kb, Term[] args) {
      final int numerator = toInt(kb, args[0]);
      final int divider = toInt(kb, args[1]);
      final int modulo = numerator % divider;
      if (modulo == 0 || numerator * divider > 0) {
         return new IntegerNumber(modulo);
      } else {
         return new IntegerNumber(modulo + divider);
      }
   }
}