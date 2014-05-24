package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* SYSTEM TEST
 % %TRUE% float(1.0)
 % %TRUE% float(-1.0)
 % %TRUE% float(0.0)
 % %FALSE% float(1)
 % %FALSE% float(-1)
 % %FALSE% float(0)
 % %FALSE% float('1')
 % %FALSE% float('1.0')
 % %FALSE% float(a)
 % %FALSE% float(p(1.0,2.0,3.0))
 % %FALSE% float([1.0,2.0,3.0])
 % %FALSE% float([])
 % %FALSE% float(X)
 % %FALSE% float(_)
*/
/**
 * <code>float(X)</code> - checks that a term is a floating point number.
 * <p>
 * <code>float(X)</code> succeeds if <code>X</code> currently stands for a floating point number.
 * </p>
 */
public final class IsFloat extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg) {
      return arg.getType() == TermType.DOUBLE;
   }
}