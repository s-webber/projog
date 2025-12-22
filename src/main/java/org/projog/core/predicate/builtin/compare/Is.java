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

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.math.ArithmeticOperator;
import org.projog.core.math.Numeric;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
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
public final class Is implements PredicateFactory {
   private final KnowledgeBase kb;
   private final ArithmeticOperator arithmeticOperator;

   public Is(KnowledgeBase kb) {
      this(kb, kb.getArithmeticOperators().placeholder());
   }

   private Is(KnowledgeBase kb, ArithmeticOperator arithmeticOperator) {
      this.kb = kb;
      this.arithmeticOperator = arithmeticOperator;
      if (arithmeticOperator == null) {
         throw new NullPointerException();
      }
   }

   @Override
   public PredicateFactory preprocess(Term term) {
      Term secondArgument = term.secondArgument();
      if (secondArgument.getType().isVariable()) {
         return this;
      }

      ArithmeticOperator arithmeticOperator = kb.getArithmeticOperators().getPreprocessedArithmeticOperator(secondArgument);
      return new Is(kb, arithmeticOperator);
   }

   @Override
   public Predicate getPredicate(Term term) {
      Numeric n = arithmeticOperator.calculate(term.secondArgument());
      return term.firstArgument().unify(n) ? PredicateUtils.TRUE : PredicateUtils.FALSE;
   }

   @Override
   public boolean isRetryable() {
      return false;
   }
}
