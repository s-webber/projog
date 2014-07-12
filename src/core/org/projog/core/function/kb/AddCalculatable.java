package org.projog.core.function.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY X is sum(1, 1)
 %ERROR Cannot find calculatable: sum/2
 
 %TRUE pj_add_calculatable(sum/2, 'org.projog.core.function.math.Add')
 
 %QUERY X is sum(1, 1)
 %ANSWER X=2
 */
/**
 * <code>pj_add_calculatable(X,Y)</code> - defines a Java class as an arithmetic function.
 * <p>
 * <code>X</code> represents the name and arity of the predicate. <code>Y</code> represents the full class name of an
 * implementation of <code>org.projog.core.Calculatable</code>.
 */
public final class AddCalculatable extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term functionNameAndArity, Term javaClass) {
      PredicateKey key = PredicateKey.createFromNameAndArity(functionNameAndArity);
      String className = getAtomName(javaClass);
      getKnowledgeBase().addCalculatable(key, className);
      return true;
   }
}
