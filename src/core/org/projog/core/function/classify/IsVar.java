package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %FALSE var(abc)
 %FALSE var(1)
 %FALSE var(a(b,c))
 %FALSE var([a,b,c])
 %FALSE X=1, var(X)
 %QUERY var(X)
 %ANSWER X=UNINSTANTIATED VARIABLE 
 %QUERY X=Y, var(X)
 %ANSWER 
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %TRUE var(_)
 */
/**
 * <code>var(X)</code> - checks that a term is an uninstantiated variable.
 * <p>
 * <code>var(X)</code> succeeds if <code>X</code> is an <i>uninstantiated</i> variable.
 * </p>
 */
public final class IsVar extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   public boolean evaluate(Term arg) {
      return arg.getType().isVariable();
   }
}