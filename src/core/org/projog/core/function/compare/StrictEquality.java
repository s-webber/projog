package org.projog.core.function.compare;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %FALSE X==Y
 %QUERY X==X
 %ANSWER X=UNINSTANTIATED VARIABLE
 %QUERY X=Y, X==Y, Y=1
 %ANSWER
 % X=1
 % Y=1
 %ANSWER
 %FALSE append([A|B],C) == append(X,Y)
 %QUERY append([A|B],C) == append([A|B],C)
 %ANSWER 
 % A=UNINSTANTIATED VARIABLE
 % B=UNINSTANTIATED VARIABLE
 % C=UNINSTANTIATED VARIABLE
 %ANSWER 
 */
/**
 * <code>X==Y</code> - a strict equality test.
 * <p>
 * If <code>X</code> can be matched with <code>Y</code> the goal succeeds else the goal fails. A <code>X==Y</code> goal
 * will only consider an uninstantiated variable to be equal to another uninstantiated variable that is already sharing
 * with it.
 * </p>
 */
public final class StrictEquality extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   public boolean evaluate(Term arg1, Term arg2) {
      return arg1.strictEquality(arg2);
   }
}