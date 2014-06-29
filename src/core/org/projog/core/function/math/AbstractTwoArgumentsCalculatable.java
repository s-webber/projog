package org.projog.core.function.math;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * A template for {@code Calculatable}s that accept two arguments.
 */
abstract class AbstractTwoArgumentsCalculatable implements Calculatable {
   @Override
   public final Numeric calculate(KnowledgeBase kb, Term[] args) {
      Numeric n1 = kb.getNumeric(args[0]);
      Numeric n2 = kb.getNumeric(args[1]);
      if (containsDouble(n1, n2)) {
         double answer = calculateDouble(n1.getDouble(), n2.getDouble());
         return new DoubleNumber(answer);
      } else {
         long answer = calculateLong(n1.getLong(), n2.getLong());
         return new IntegerNumber(answer);
      }
   }

   private static boolean containsDouble(Numeric n1, Numeric n2) {
      return n1.getType() == TermType.DOUBLE || n2.getType() == TermType.DOUBLE;
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract double calculateDouble(double n1, double n2);

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract long calculateLong(long n1, long n2);
}