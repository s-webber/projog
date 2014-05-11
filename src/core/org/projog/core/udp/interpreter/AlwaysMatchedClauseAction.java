package org.projog.core.udp.interpreter;

import org.projog.core.term.Term;

/**
 * Defines a fact that will always be true.
 * <p>
 * e.g. {@code e.g. p(X,Y,Z).}
 */
public final class AlwaysMatchedClauseAction extends AbstractFactClauseAction {
   AlwaysMatchedClauseAction(Term[] consequentArgs) {
      super(consequentArgs);
   }

   @Override
   public boolean evaluate(Term[] queryArgs) {
      return true;
   }
}