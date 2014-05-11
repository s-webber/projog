package org.projog.core.function.debug;

import java.util.Map;

import org.projog.core.PredicateKey;
import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-debugging
 */
/**
 * <code>debugging</code> - lists current spy points.
 * <p>
 * The list of spy points currently set is printed as a side effect of <code>debugging</code> being satisfied.
 * </p>
 */
public final class Debugging extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate();
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate() {
      SpyPoints spyPoints = getKnowledgeBase().getSpyPoints();
      Map<PredicateKey, SpyPoints.SpyPoint> map = spyPoints.getSpyPoints();
      for (Map.Entry<PredicateKey, SpyPoints.SpyPoint> e : map.entrySet()) {
         if (e.getValue().isEnabled()) {
            getKnowledgeBase().getFileHandles().getCurrentOutputStream().println(e.getKey());
         }
      }
      return true;
   }
}