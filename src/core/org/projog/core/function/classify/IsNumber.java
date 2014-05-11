package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* SYSTEM TEST
 % %TRUE% number(1)
 % %TRUE% number(-1)
 % %TRUE% number(0)
 % %TRUE% number(0.0)
 % %TRUE% number(1.0)
 % %TRUE% number(-1.0)
 % %FALSE% number('1')
 % %FALSE% number('1.0')
 % %FALSE% number(1+1)
 % %FALSE% number(a)
 % %FALSE% number(p(1,2,3))
 % %FALSE% number([1,2,3])
 % %FALSE% number([a,b,c]) 
 % %FALSE% number([])
 % %FALSE% number(X)
 */
/**
 * <code>number(X)</code> - checks that a term is numeric.
 * <p>
 * <code>number(X)</code> succeeds if <code>X</code> currently stands for a number.
 * </p>
 */
public final class IsNumber extends AbstractSingletonPredicate {
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
      TermType type = arg.getType();
      return type.isNumeric();
   }
}