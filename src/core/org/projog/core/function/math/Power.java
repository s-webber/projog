package org.projog.core.function.math;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %QUERY X is 2 ** 1
 %ANSWER X = 2

 %QUERY X is 2 ** 2
 %ANSWER X = 4

 %QUERY X is 2 ** 5
 %ANSWER X = 32

 %QUERY X is 5 ** 3
 %ANSWER X = 125

 %QUERY X is 5.0 ** 3
 %ANSWER X = 125.0

 %QUERY X is 5 ** 3.0
 %ANSWER X = 125.0

 %QUERY X is 5.0 ** 3.0
 %ANSWER X = 125.0

 %QUERY X is 2 + 5 ** 3 - 1
 %ANSWER X = 126

 %QUERY X is -2 ** 2
 %ANSWER X = 4

 % Note: in some Prolog implementations the result would be 0.25
 %QUERY X is -2 ** -2
 %ANSWER X = 0

 % Note: in some Prolog implementations the result would be 0.25
 %QUERY X is 2 ** -2
 %ANSWER X = 0

 %QUERY X is 0.5 ** 2
 %ANSWER X = 0.25
 */
/**
 * <code>**</code> calculates the result of the first argument raised to the power of the second argument.
 */
public final class Power implements Calculatable {
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      Numeric n1 = kb.getNumeric(args[0]);
      Numeric n2 = kb.getNumeric(args[1]);
      double result = Math.pow(n1.getDouble(), n2.getDouble());
      if (isInteger(n1) && isInteger(n2)) {
         return new IntegerNumber((int) result);
      } else {
         return new DoubleNumber(result);
      }
   }

   private boolean isInteger(Numeric n) {
      return n.getType() == TermType.INTEGER;
   }
}
