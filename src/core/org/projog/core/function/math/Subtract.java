package org.projog.core.function.math;

/* TEST
 %LINK prolog-arithmetic
 */
/**
 * <code>-</code> - performs subtraction.
 */
public final class Subtract extends AbstractTwoArgumentsCalculatable {
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