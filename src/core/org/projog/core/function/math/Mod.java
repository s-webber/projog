package org.projog.core.function.math;

/**
 * Determines the remainder from dividing two numbers.
 */
public final class Mod extends AbstractTwoArgumentsCalculatable {
   /** Returns the remainder resulting from the division of two arguments */
   @Override
   protected double calculateDouble(double d1, double d2) {
      return d1 % d2;
   }

   /** Returns the remainder resulting from the division of two arguments */
   @Override
   protected int calculateInt(int i1, int i2) {
      return i1 % i2;
   }
}