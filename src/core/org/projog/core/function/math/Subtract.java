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

import org.projog.core.KnowledgeBase;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Performs subtraction.
 */
public final class Subtract extends AbstractTwoArgumentsCalculatable {
   @Override
   public Numeric calculate(KnowledgeBase kb, Term[] args) {
      if (args.length == 1) { // e.g. X is -Y or X is -(4+2)
         return calculateNegation(kb.getNumeric(args[0]));
      } else {
         return super.calculate(kb, args);
      }
   }

   private Numeric calculateNegation(Numeric n1) {
      if (n1.getType() == TermType.DOUBLE) {
         return new DoubleNumber(-n1.getDouble());
      } else {
         return new IntegerNumber(-n1.getInt());
      }
   }

   /** Returns the difference of the two arguments */
   @Override
   protected double calculateDouble(double d1, double d2) {
      return d1 - d2;
   }

   /** Returns the difference of the two arguments */
   @Override
   protected int calculateInt(int i1, int i2) {
      return i1 - i2;
   }
}