package org.projog.core.function.math;

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;

public class IntegerDivide implements Calculatable {
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      int dividend = toInt(args[0]);
      int divisor = toInt(args[1]);
      return new IntegerNumber(dividend / divisor);
   }
}