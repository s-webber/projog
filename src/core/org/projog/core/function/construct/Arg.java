package org.projog.core.function.construct;

import static org.projog.core.term.TermUtils.castToNumeric;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY arg(2, a(b,c(d)), X)
 %ANSWER X=c(d)

 %QUERY arg(1, a+(b+c), X )
 %ANSWER X=a

 %FALSE arg(1, a+(b+c), b)

 %QUERY arg(2, [a,b,c], X)
 %ANSWER X=[b,c]
 
 %QUERY arg(3, [a,b,c], X)
 %ERROR Cannot get argument at position: 3 from: .(a, .(b, .(c, [])))
 */
/**
 * <code>arg(N,T,A)</code> - allows access to an argument of a structure.
 * <p>
 * <code>arg(N,T,A)</code> provides a mechanism for accessing a specific argument of a structure.
 * <code>arg(N,T,A)</code> succeeds if the <code>N</code>th argument of the structure <code>T</code> is, or can be
 * assigned to, <code>A</code>.
 * </p>
 */
public final class Arg extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2, Term arg3) {
      int argIdx = castToNumeric(arg1).getInt();
      if (arg2.getNumberOfArguments() < argIdx) {
         throw new ProjogException("Cannot get argument at position: " + argIdx + " from: " + arg2);
      }
      Term t = arg2.getArgument(argIdx - 1);
      return arg3.unify(t);
   }
}