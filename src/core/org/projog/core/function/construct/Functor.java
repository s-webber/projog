package org.projog.core.function.construct;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.AnonymousVariable;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* SYSTEM TEST
 % %QUERY% functor(f(a,b,c(Z)),F,N)
 % %ANSWER%
 % Z=UNINSTANTIATED VARIABLE
 % F=f
 % N=3
 % %ANSWER%

 % %QUERY% functor(a+b,F,N)
 % %ANSWER%
 % F=+
 % N=2
 % %ANSWER%

 % %QUERY% functor([a,b,c],F,N)
 % %ANSWER%
 % F=.
 % N=2
 % %ANSWER%

 % %QUERY% functor(atom,F,N)
 % %ANSWER%
 % F=atom
 % N=0
 % %ANSWER%

 % %FALSE% functor([a,b,c],'.',3)
 % %FALSE% functor([a,b,c],a,Z)

 % %QUERY% functor( X, sentence, 2)
 % %ANSWER% X = sentence(_, _)

 copy(Old, New) :- functor(Old, F, N), functor(New, F, N).

 % %QUERY% copy(sentence(a,b), X)
 % %ANSWER% X = sentence(_, _)
 */
/**
 * <code>functor(T,F,N)</code>
 * <p>
 * Predicate <code>functor(T,F,N)</code> means "<code>T</code> is a structure with name (functor) <code>F</code> and
 * <code>N</code> number of arguments".
 * </p>
 */
public final class Functor extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1], args[2]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term t, Term f, Term n) {
      switch (t.getType()) {
         case ATOM:
            return f.unify(t) && n.unify(new IntegerNumber(0));
         case STRUCTURE:
         case LIST:
         case EMPTY_LIST:
            return f.unify(new Atom(t.getName())) && n.unify(new IntegerNumber(t.getNumberOfArguments()));
         case ANONYMOUS_VARIABLE:
         case NAMED_VARIABLE:
            int numArgs = TermUtils.castToNumeric(n).getInt();
            Term[] a = new Term[numArgs];
            for (int i = 0; i < numArgs; i++) {
               a[i] = AnonymousVariable.ANONYMOUS_VARIABLE;
            }
            String functorName = getAtomName(f);
            return t.unify(Structure.createStructure(functorName, a));
         default:
            throw new ProjogException("Invalid type for first argument of Functor command: " + t.getType());
      }
   }
}