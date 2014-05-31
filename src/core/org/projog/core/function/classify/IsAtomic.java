package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %TRUE atomic(abc)
 %TRUE atomic(1)
 %FALSE atomic(X)
 %FALSE atomic(_)
 %FALSE atomic(a(b,c))
 %FALSE atomic([a,b,c])
 */
/**
 * <code>atomic(X)</code> - checks that a term is atomic.
 * <p>
 * <code>atomic(X)</code> succeeds if <code>X</code> currently stands for either a number or an atom.
 * </p>
 */
public final class IsAtomic extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   public boolean evaluate(Term arg) {
      TermType type = arg.getType();
      return type == TermType.ATOM || type.isNumeric();
   }
}