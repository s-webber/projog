package org.projog.core.function.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.Calculatable;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY X is sum(1, 1)
 %ERROR Cannot find calculatable: sum
 
 %TRUE pj_add_calculatable('sum', 'org.projog.core.function.math.Add')
 
 %QUERY X is sum(1, 1)
 %ANSWER X=2
 */
/**
* <code>pj_add_calculatable(X,Y)</code> - defines a Java class as an arithmetic function.
* <p>
* <code>X</code> represents the function name.
* <code>Y</code> represents the full class name of an implementation of <code>org.projog.core.Calculatable</code>.
*/
public class AddCalculatable extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   public boolean evaluate(Term functionName, Term javaClass) {
      String key = getAtomName(functionName);
      String className = getAtomName(javaClass);
      try {
         Class<?> c = Class.forName(className);
         Calculatable calculatable = (Calculatable) c.newInstance();
         getKnowledgeBase().addCalculatable(key, calculatable);
         return true;
      } catch (Exception e) {
         throw new ProjogException("Could not register new Calculatable using name: " + key + " and class: " + className, e);
      }
   }
}
