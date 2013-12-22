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
package org.projog.core.function.compare;

import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %FALSE% 1=\=1
 % %FALSE% 1.5=\=3.0/2.0
 % %FALSE% 6*6=\=9*4
 % %TRUE% 1=\=2
 % %TRUE% 1+1=\=1-1
 % %FALSE% X=1, Y=1, X=\=Y
 % %QUERY% X=1, Y=2, X=\=Y
 % %ANSWER%
 % X=1
 % Y=2
 % %ANSWER%
 */
/**
 * <code>X=\=Y</code> - numeric inequality test.
 * <p>
 * Succeeds when the number argument <code>X</code> is <i>not</i> equal to the number argument <code>Y</code>.
 * </p>
 */
public final class NumericInequality extends AbstractSingletonPredicate {
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
      return NUMERIC_TERM_COMPARATOR.compare(arg1, arg2, getKnowledgeBase()) != 0;
   }
}