/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.builtin.compare;

import org.projog.core.math.ArithmeticOperator;
import org.projog.core.math.Numeric;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.Term;

/* TEST
%?- X is 3
% X=3
%?- X is 3+2
% X=5
%?- X is 3.5+2.25
% X=5.75
%TRUE 5 is 5
%FAIL 5 is 6
%TRUE 5 is 4+1
%FAIL 5 is 4+2

%?- X is Y
%ERROR Cannot get Numeric for term: Y of type: VARIABLE

%?- Z=1+1, Y=9-Z, X is Y
% X=7
% Y=9 - (1 + 1)
% Z=1 + 1

%?- X is _
%ERROR Cannot get Numeric for term: _ of type: VARIABLE

%?- X is sum(1,2)
%ERROR Cannot find arithmetic operator: sum/2

%?- X is ten
%ERROR Cannot find arithmetic operator: ten/0

%?- X is []
%ERROR Cannot get Numeric for term: [] of type: EMPTY_LIST

%?- X is [1,2,3]
%ERROR Cannot get Numeric for term: .(1, .(2, .(3, []))) of type: LIST
*/
/**
 * <code>X is Y</code> - evaluate arithmetic expression.
 * <p>
 * Firstly structure <code>Y</code> is evaluated as an arithmetic expression to give a number. Secondly an attempt is
 * made to match the number to <code>X</code>. The goal succeeds or fails based on the match.
 * </p>
 */
public final class Is extends AbstractSingleResultPredicate implements PreprocessablePredicateFactory {
   @Override
   protected boolean evaluate(Term arg1, Term arg2) {
      Numeric n = getArithmeticOperators().getNumeric(arg2);
      return arg1.unify(n);
   }

   @Override
   public PredicateFactory preprocess(Term arg) {
      final ArithmeticOperator o = getArithmeticOperators().getPreprocessedArithmeticOperator(arg.getArgument(1));
      if (o == null) {
         return this;
      } else if (o instanceof Numeric) {
         return new Unify((Numeric) o);
      } else {
         return new PreprocessedIs(o);
      }
   }

   private static final class Unify extends AbstractSingleResultPredicate {
      final Numeric n;

      Unify(Numeric n) {
         this.n = n;
      }

      @Override
      public boolean evaluate(Term arg1, Term arg2) {
         return arg1.unify(n);
      }
   }

   private static final class PreprocessedIs extends AbstractSingleResultPredicate {
      final ArithmeticOperator o;

      PreprocessedIs(ArithmeticOperator o) {
         this.o = o;
      }

      @Override
      public boolean evaluate(Term arg1, Term arg2) {
         return arg1.unify(o.calculate(arg2.getArgs()));
      }
   }
}
