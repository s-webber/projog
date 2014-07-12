package org.projog.core.function.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE pj_add_predicate(xyz/1, 'org.projog.core.function.compound.Call')

 %TRUE xyz(true)
 %QUERY xyz(repeat(3))
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %FALSE xyz(fail)
 */
/**
 * <code>pj_add_predicate(X,Y)</code> - defines a Java class as a built-in predicate.
 * <p>
 * <code>X</code> represents the name and arity of the predicate. <code>Y</code> represents the full class name of an
 * implementation of <code>org.projog.core.PredicateFactory</code>.
 * </p>
 * <p>
 * This predicate provides an easy way to configure and extend the functionality of Projog - including adding
 * functionality not possible to define in pure Prolog syntax.
 * </p>
 */
public final class AddPredicateFactory extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      PredicateKey key = PredicateKey.createFromNameAndArity(arg1);
      String className = getAtomName(arg2);
      getKnowledgeBase().addPredicateFactory(key, className);
      return true;
   }
}