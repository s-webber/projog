package org.projog.core.function.math;

/**
 * Performs multiplication.
 */
public final class Multiply extends AbstractTwoArgumentsCalculatable {
   /** Returns the product of the two arguments */
   @Override
   protected double calculateDouble(double d1, double d2) {
      return d1 * d2;
   }

   /** Returns the product of the two arguments */
   @Override
   protected int calculateInt(int i1, int i2) {
      return i1 * i2;
   }
}