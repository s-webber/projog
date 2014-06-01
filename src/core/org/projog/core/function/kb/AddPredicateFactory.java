package org.projog.core.function.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import java.util.Arrays;

import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
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
 * <code>pj_add_predicate(X,Y)</code> - defines a Java class as an built-in predicate.
 * <p>
 * <code>X</code> represents the name and arity of the predicate.
 * <code>Y</code> represents the full class name of an implementation of <code>org.projog.core.PredicateFactory</code>.
 * </p>
 * This predicate provides an easy way to configure and extend the functionality of Projog - including adding
 * functionality not possible to define in pure Prolog syntax.
 * </p>
 */
public final class AddPredicateFactory extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      try {
         PredicateKey key = PredicateKey.createFromNameAndArity(args[0]);
         String className = getAtomName(args[1]);
         Class<?> c = Class.forName(className);
         PredicateFactory pf = (PredicateFactory) c.newInstance();
         getKnowledgeBase().addPredicateFactory(key, pf);
         return true;
      } catch (Exception e) {
         throw new ProjogException("Could not register new PredicateFactory using arguments: " + Arrays.toString(args), e);
      }
   }
}