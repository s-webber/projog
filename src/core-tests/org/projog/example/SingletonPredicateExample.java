package org.projog.example;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class SingletonPredicateExample extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term t1, Term t2) {
      Atom t1ToUpperCase = new Atom(getAtomName(t1).toUpperCase());
      return t2.unify(t1ToUpperCase);
   }
}