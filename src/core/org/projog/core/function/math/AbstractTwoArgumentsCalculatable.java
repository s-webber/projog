/*
 * Copyright 2013 S Webber
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.core.function.math;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * A template for {@code Calculatable}s that accept two arguments.
 */
abstract class AbstractTwoArgumentsCalculatable implements Calculatable {
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      Numeric n1 = kb.getNumeric(args[0]);
      Numeric n2 = kb.getNumeric(args[1]);
      if (containsDouble(n1, n2)) {
         double answer = calculateDouble(n1.getDouble(), n2.getDouble());
         return new DoubleNumber(answer);
      } else {
         int answer = calculateInt(n1.getInt(), n2.getInt());
         return new IntegerNumber(answer);
      }
   }

   private static boolean containsDouble(Numeric n1, Numeric n2) {
      return n1.getType() == TermType.DOUBLE || n2.getType() == TermType.DOUBLE;
   }

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract double calculateDouble(double d1, double d2);

   /** Returns the result of evaluating an arithmetic expression using the two arguments */
   protected abstract int calculateInt(int i1, int i2);
}