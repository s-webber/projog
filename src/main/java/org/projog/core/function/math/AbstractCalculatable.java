/*
 * Copyright 2013-2014 S. Webber
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

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;

import org.projog.core.Calculatable;
import org.projog.core.Calculatables;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;

public abstract class AbstractCalculatable implements Calculatable {
   private Calculatables calculatables;

   @Override
   public final void setKnowledgeBase(KnowledgeBase kb) {
      calculatables = getCalculatables(kb);
   }

   @Override
   public final Numeric calculate(Term... args) {
      switch (args.length) {
         case 1:
            Numeric n = calculatables.getNumeric(args[0]);
            return calculate(n);
         case 2:
            Numeric n1 = calculatables.getNumeric(args[0]);
            Numeric n2 = calculatables.getNumeric(args[1]);
            return calculate(n1, n2);
         default:
            throw createWrongNumberOfArgumentsException(args.length);
      }
   }

   protected Numeric calculate(Numeric n) {
      throw createWrongNumberOfArgumentsException(1);
   }

   protected Numeric calculate(Numeric n1, Numeric n2) {
      throw createWrongNumberOfArgumentsException(2);
   }

   private IllegalArgumentException createWrongNumberOfArgumentsException(int numberOfArguments) {
      throw new IllegalArgumentException("The Calculatable: " + getClass() + " does next accept the number of arguments: " + numberOfArguments);
   }
}
