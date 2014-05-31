package org.projog.core.function.debug;

/* TEST
 %LINK prolog-debugging
 */
/**
 * <code>spy X</code> - add a spy point for a predicate.
 * <p>
 * By adding a spy point for the predicate name instantiated to <code>X</code> the programmer will be informed how it is
 * used in the resolution of a goal.
 * </p>
 */
public final class Spy extends AbstractAlterSpyPointFunction {
   public Spy() {
      super(true);
   }
}