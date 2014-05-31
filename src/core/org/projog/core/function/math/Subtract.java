package org.projog.core.function.math;

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
 * <code>-</code> - performs subtraction.
 */
public final class Subtract extends AbstractTwoArgumentsCalculatable {
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      if (args.length == 1) { // e.g. X is -Y or X is -(4+2)
         return calculateNegation(kb.getNumeric(args[0]));
      } else {
         return super.calculate(kb, args);
      }
   }

   private Numeric calculateNegation(Numeric n1) {
      if (n1.getType() == TermType.DOUBLE) {
         return new DoubleNumber(-n1.getDouble());
      } else {
         return new IntegerNumber(-n1.getInt());
      }
   }

   /** Returns the difference of the two arguments */
   @Override
   protected double calculateDouble(double d1, double d2) {
      return d1 - d2;
   }

   /** Returns the difference of the two arguments */
   @Override
   protected int calculateInt(int i1, int i2) {
      return i1 - i2;
   }
}