package org.projog.core.function.math;

/**
 * Performs division.
 */
public final class Divide extends AbstractTwoArgumentsCalculatable {
   /** Returns the quotient of the two arguments */
   @Override
   protected double calculateDouble(double d1, double d2) {
      return d1 / d2;
   }

   /** Returns the quotient of the two arguments */
   @Override
   protected int calculateInt(int i1, int i2) {
      return i1 / i2;
   }
}