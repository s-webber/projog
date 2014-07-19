package org.projog.core.function.math;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;

import org.projog.core.Calculatable;
import org.projog.core.Calculatables;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %LINK prolog-arithmetic
 */
/**
 * <code>/</code> - performs division.
 */
public final class Divide implements Calculatable {
   private Calculatables calculatables;

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      calculatables = getCalculatables(kb);
   }

   @Override
   public Numeric calculate(Term[] args) {
      Numeric n1 = calculatables.getNumeric(args[0]);
      Numeric n2 = calculatables.getNumeric(args[1]);
      if (containsFraction(n1, n2)) {
         return divideFractions(n1, n2);
      } else {
         long dividend = n1.getLong();
         long divisor = n2.getLong();
         if (dividend % divisor == 0) {
            // e.g. 6 / 2 = 3
            return new IntegerNumber(dividend / divisor);
         } else {
            // e.g. 7 / 2 = 3.5
            return divideFractions(n1, n2);
         }
      }
   }

   private static boolean containsFraction(Numeric n1, Numeric n2) {
      return n1.getType() == TermType.FRACTION || n2.getType() == TermType.FRACTION;
   }

   private DecimalFraction divideFractions(Numeric n1, Numeric n2) {
      return new DecimalFraction(n1.getDouble() / n2.getDouble());
   }
}