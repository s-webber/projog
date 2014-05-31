package org.projog.core.function.compare;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE abc \= def

 %FALSE X \= Y

 %FALSE p(X,b) \= p(a,Y)

 %QUERY p(X,b,c) \= p(a,Y,z)
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE 
 %ANSWER
 */
/**
 * <code>X \= Y</code> - checks whether two terms cannot be unified.
 * <p>
 * If <code>X</code> can be NOT unified with <code>Y</code> the goal succeeds
 * else the goal fails.
 * </p>
 */
public final class NotUnifiable extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   public boolean evaluate(Term arg1, Term arg2) {
      final boolean unifiable = arg1.unify(arg2);
      arg1.backtrack();
      arg2.backtrack();
      return !unifiable;
   }
}