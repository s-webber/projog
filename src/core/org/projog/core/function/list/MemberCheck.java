package org.projog.core.function.list;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* SYSTEM TEST
 % %TRUE% memberchk(a, [a,b,c])
 % %TRUE% memberchk(b, [a,b,c])
 % %TRUE% memberchk(c, [a,b,c])

 % %FALSE% memberchk(d, [a,b,c])
 % %FALSE% memberchk(d, [])
 % %FALSE% memberchk([], [])
 
 % %QUERY% memberchk(X, [a,b,c])
 % %ANSWER% X=a
 
 % %QUERY% memberchk(p(X,b), [p(a,b), p(z,Y), p(x(Y), Y)])
 % %ANSWER% 
 % X=a
 % Y=UNINSTANTIATED VARIABLE
 % %ANSWER%
*/
/**
 * <code>memberchk(E, L)</code> - checks is a term is a member of a list.
 * <p>
 * <code>memberchk(E, L)</code> succeeds if <code>E</code> is a member of the list <code>L</code>. No attempt is made to
 * retry the goal during backtracking - so if <code>E</code> appears multiple times in <code>L</code> only the first
 * occurrence will be matched.
 * </p>
 */
public final class MemberCheck extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term element, Term list) {
      if (list.getType() != TermType.LIST && list.getType() != TermType.EMPTY_LIST) {
         throw new ProjogException("Expected list but got: " + list);
      }
      while (list.getType() == TermType.LIST) {
         if (element.unify(list.getArgument(0))) {
            return true;
         }
         element.backtrack();
         list.backtrack();
         list = list.getArgument(1);
      }
      return false;
   }
}
