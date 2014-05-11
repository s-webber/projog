package org.projog.core.function.compound;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %TRUE% call(true)
 % %FALSE% call(fail)
 % %QUERY% X = true, call(X)
 % %ANSWER% X = true
 % %FALSE% X = fail, call(X)

 test(a).
 test(b).
 test(c).

 % %QUERY% X = test(Y), call(X)
 % %ANSWER%
 % X = test(a)
 % Y = a
 % %ANSWER%
 % %ANSWER%
 % X = test(b)
 % Y = b
 % %ANSWER%
 % %ANSWER%
 % X = test(c)
 % Y = c
 % %ANSWER%
 
 testCall(X) :- call(X).
 
 % %FALSE% testCall(fail)
 % %TRUE% testCall(true)
 % %QUERY% testCall((true ; true))
 % %ANSWER/%
 % %ANSWER/%
 */
/**
 * <code>call(X)</code> - calls the goal represented by a term.
 * <p>
 * The predicate <code>call</code> makes it possible to call goals that are determined at runtime rather than when a
 * program is written. <code>call(X)</code> succeeds if the goal represented by the term <code>X</code> succeeds.
 * <code>call(X)</code> fails if the goal represented by the term <code>X</code> fails. An attempt is made to retry the
 * goal during backtracking.
 * </p>
 */
public final class Call extends AbstractRetryablePredicate {
   private Predicate predicateToCall;
   private Term[] argumentsForPredicateToCall;

   /** needed to create prototype actual instances can be created from */
   public Call() {
   }

   private Call(KnowledgeBase kb) {
      setKnowledgeBase(kb);
   }

   @Override
   public Call getPredicate(Term... args) {
      return getPredicate(args[0]);
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public Call getPredicate(Term arg) {
      return new Call(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term t) {
      if (predicateToCall == null) {
         predicateToCall = KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), t);
         argumentsForPredicateToCall = t.getArgs();
      } else if (predicateToCall.isRetryable() == false) {
         return false;
      }

      return predicateToCall.evaluate(argumentsForPredicateToCall);
   }

   @Override
   public boolean couldReEvaluationSucceed() {
      return predicateToCall == null || predicateToCall.couldReEvaluationSucceed();
   }
}