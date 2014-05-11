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
 * <code>nodebug</code> - removes all current spy points.
 */
public final class NoDebug extends AbstractSingletonPredicate {
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
         spyPoints.setSpyPoint(e.getKey(), false);
      }
      return true;
   }
}