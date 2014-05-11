package org.projog.example;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

public class CalculatableExample implements Calculatable {
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      Numeric input = TermUtils.castToNumeric(args[0]);
      long rounded = Math.round(input.getDouble());
      return new IntegerNumber((int) rounded);
   }
}