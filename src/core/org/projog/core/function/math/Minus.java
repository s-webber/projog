package org.projog.core.function.math;

/* TEST
 %LINK prolog-arithmetic
 */
/**
 * <code>-</code> - minus operator.
 */
public final class Minus extends AbstractOneArgumentCalculatable {
   @Override
   protected double calculateDouble(double d) {
      return -d;
   }

   @Override
   protected int calculateInt(int i) {
      return -i;
   }
}
