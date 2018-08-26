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
package org.projog.core.function;

import org.projog.core.Predicate;
import org.projog.core.term.Term;

/** A skeletal implementation of {@link Predicate} */
public abstract class AbstractPredicate implements Predicate {
   @Override
   public boolean evaluate(Term... args) {
      switch (args.length) {
         case 0:
            return evaluate();
         case 1:
            return evaluate(args[0]);
         case 2:
            return evaluate(args[0], args[1]);
         case 3:
            return evaluate(args[0], args[1], args[2]);
         default:
            throw createWrongNumberOfArgumentsException(args.length);
      }
   }

   protected boolean evaluate() {
      throw createWrongNumberOfArgumentsException(0);
   }

   protected boolean evaluate(Term arg) {
      throw createWrongNumberOfArgumentsException(1);
   }

   protected boolean evaluate(Term arg1, Term arg2) {
      throw createWrongNumberOfArgumentsException(2);
   }

   protected boolean evaluate(Term arg1, Term arg2, Term arg3) {
      throw createWrongNumberOfArgumentsException(3);
   }

   private IllegalArgumentException createWrongNumberOfArgumentsException(int numberOfArguments) {
      throw new IllegalArgumentException("The predicate: " + getClass() + " does next accept the number of arguments: " + numberOfArguments);
   }
}
