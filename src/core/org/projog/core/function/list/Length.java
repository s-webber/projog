package org.projog.core.function.list;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %QUERY% length([],X)
 % %ANSWER% X=0
 % %QUERY% length([a],X)
 % %ANSWER% X=1
 % %QUERY% length([a,b],X)
 % %ANSWER% X=2
 % %QUERY% length([a,b,c],X)
 % %ANSWER% X=3
 
 % %FALSE% length([a,b|c],X)
 % %FALSE% length([a,b],1)
 % %FALSE% length([a,b],3)
 % %FALSE% length(X,3)
 % %FALSE% length(abc,3)
 */
/**
 * <code>length(X,Y)</code> - determines the length of a list.
 * <p>
 * The <code>length(X,Y)</code> goal succeeds if the number of elements in the list <code>X</code> matches the integer
 * value <code>Y</code>.
 * </p>
 */
public final class Length extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(final Term list, final Term expectedLength) {
      final java.util.List<Term> javaList = ListFactory.toJavaUtilList(list);
      if (javaList != null) {
         final IntegerNumber actualLength = new IntegerNumber(javaList.size());
         return expectedLength.unify(actualLength);
      } else {
         return false;
      }
   }
}