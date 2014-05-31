package org.projog.core.function.math;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.DoubleNumber;
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
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      Numeric n1 = kb.getNumeric(args[0]);
      Numeric n2 = kb.getNumeric(args[1]);
      if (containsDouble(n1, n2)) {
         return divideDoubles(n1, n2);
      } else {
         int dividend = n1.getInt();
         int divisor = n2.getInt();
         if (dividend % divisor == 0) {
            // e.g. 6 / 2 = 3
            return new IntegerNumber(dividend / divisor);
         } else {
            // e.g. 7 / 2 = 3.5
            return divideDoubles(n1, n2);
         }
      }
   }

   private static boolean containsDouble(Numeric n1, Numeric n2) {
      return n1.getType() == TermType.DOUBLE || n2.getType() == TermType.DOUBLE;
   }

   private DoubleNumber divideDoubles(Numeric n1, Numeric n2) {
      return new DoubleNumber(n1.getDouble() / n2.getDouble());
   }
}