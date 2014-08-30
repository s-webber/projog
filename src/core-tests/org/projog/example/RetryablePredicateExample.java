package org.projog.example;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class RetryablePredicateExample extends AbstractRetryablePredicate {
   private Iterator<Map.Entry<Object, Object>> systemProperties;

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      if (systemProperties == null) {
         systemProperties = System.getProperties().entrySet().iterator();
      } else {
         arg1.backtrack();
         arg2.backtrack();
      }
      while (systemProperties.hasNext()) {
         Entry<Object, Object> entry = systemProperties.next();
         String key = (String) entry.getKey();
         String value = (String) entry.getValue();
         if (arg1.unify(new Atom(key)) && arg2.unify(new Atom(value))) {
            return true;
         } else {
            arg1.backtrack();
            arg2.backtrack();
         }
      }
      return false;
   }

   @Override
   public RetryablePredicateExample getPredicate(Term... args) {
      return getPredicate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public RetryablePredicateExample getPredicate(Term arg1, Term arg2) {
      return new RetryablePredicateExample();
   }
}