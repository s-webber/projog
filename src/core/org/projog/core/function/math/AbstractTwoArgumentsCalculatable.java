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

/**
 * A template for {@code Calculatable}s that accept two arguments.
 */
abstract class AbstractTwoArgumentsCalculatable implements Calculatable {
   private Calculatables calculatables;

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      calculatables = getCalculatables(kb);
   }

   @Override
   public final Numeric calculate(Term[] args) {
      Numeric n1 = calculatables.getNumeric(args[0]);
      Numeric n2 = calculatables.getNumeric(args[1]);
      if (containsFraction(n1, n2)) {
         double answer = calculateDouble(n1.getDouble(), n2.getDouble());
         return new DecimalFraction(answer);
      } else {
         long answer = calculateLong(n1.getLong(), n2.getLong());
         return new IntegerNumber(answer);
      }
   }

   private static boolean containsFraction(Numeric n1, Numeric n2) {
      return n1.getType() == TermType.FRACTION || n2.getType() == TermType.FRACTION;
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract double calculateDouble(double n1, double n2);

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract long calculateLong(long n1, long n2);
}