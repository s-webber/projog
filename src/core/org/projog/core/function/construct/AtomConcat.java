package org.projog.core.function.construct;

import static org.projog.core.term.TermType.ATOM;
import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %TRUE atom_concat(abc, def, abcdef)
  
 %QUERY atom_concat(X, def, abcdef)
 %ANSWER X=abc

 %QUERY atom_concat(abc, X, abcdef)
 %ANSWER X=def
 
 %QUERY atom_concat(abc, def, X)
 %ANSWER X=abcdef
 
 %FALSE atom_concat(abc, def, qwerty)
 %FALSE atom_concat(X, def, qwerty)
 %FALSE atom_concat(abc, X, qwerty)
 */
/**
 * <code>atom_concat(X, Y, Z)</code> - concatenates atom names.
 * <p>
 * <code>atom_concat(X, Y, Z)</code> succeeds if the name of atom <code>Z</code> matches the concatenation of the names
 * of atoms <code>X<code> and <code>Y</code>.
 * </p>
 */
public final class AtomConcat extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2, Term arg3) {
      assertAtomOrVariable(arg1);
      assertAtomOrVariable(arg2);
      assertAtomOrVariable(arg3);

      final boolean isArg1Atom = isAtom(arg1);
      final boolean isArg2Atom = isAtom(arg2);
      if (isArg1Atom && isArg2Atom) {
         final Atom concat = new Atom(arg1.getName() + arg2.getName());
         return arg3.unify(concat);
      } else if (isAtom(arg3)) {
         final String atomName = getAtomName(arg3);
         if (isArg1Atom) {
            String prefix = arg1.getName();
            return (atomName.startsWith(prefix) && arg2.unify(new Atom(atomName.substring(prefix.length()))));
         } else if (isArg2Atom) {
            String suffix = arg2.getName();
            return (atomName.endsWith(suffix) && arg1.unify(new Atom(atomName.substring(0, (atomName.length() - suffix.length())))));
         } else {
            throw new ProjogException("If third argument is not an atom then both first and second arguments must be: " + arg1 + " " + arg2 + " " + arg3);
         }
      } else {
         // TODO add support for when first two args are variables #7
         throw new ProjogException("If first and second arguments are not both atoms then third argument must be: " + arg1 + " " + arg2 + " " + arg3);
      }
   }

   private void assertAtomOrVariable(Term t) {
      final TermType type = t.getType();
      if (type != TermType.ATOM && !type.isVariable()) {
         throw new ProjogException("Expected an atom or variable but got: " + type + " with value: " + t);
      }
   }

   private boolean isAtom(Term t) {
      return t.getType() == ATOM;
   }
}