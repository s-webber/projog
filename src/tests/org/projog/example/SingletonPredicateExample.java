package org.projog.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class SingletonPredicateExample extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      String currentDate = sdf.format(new Date());
      return arg.unify(new Atom(currentDate));
   }
}