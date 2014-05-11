package org.projog.core.function.debug;

/* SYSTEM TEST
 % %LINK% prolog-debugging
 */
/**
 * <code>nospy X</code> - removes a spy point for a predicate.
 * <p>
 * By adding removing a spy point for the predicate name instantiated to <code>X</code> the programmer will no longer be
 * informed how it is used in the resolution of a goal.
 * </p>
 */
public final class NoSpy extends AbstractAlterSpyPointFunction {
   public NoSpy() {
      super(false);
   }
}