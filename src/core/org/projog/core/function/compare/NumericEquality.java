package org.projog.core.function.compare;

import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %TRUE% 1=:=1
 % %TRUE% 1.5=:=3.0/2.0
 % %TRUE% 6*6=:=9*4
 % %FALSE% 1=:=2
 % %FALSE% 1+1=:=1-1
 % %QUERY% X=1, Y=1, X=:=Y
 % %ANSWER%
 % X=1
 % Y=1
 % %ANSWER%
 % %FALSE% X=1, Y=2, X=:=Y
 */
/**
 * <code>X=:=Y</code> - numeric equality test.
 * <p>
 * Succeeds when the number argument <code>X</code> is equal to the number argument <code>Y</code>.
 * </p>
 */
public final class NumericEquality extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, getKnowledgeBase()) == 0;
   }
}