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
package org.projog.core.predicate.builtin.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

/* TEST
%?- X is sum(1, 1)
%ERROR Cannot find arithmetic operator: sum/2

%TRUE pj_add_arithmetic_operator(sum/2, 'org.projog.core.math.builtin.Add')

%?- X is sum(1, 1)
% X=2
*/
/**
 * <code>pj_add_arithmetic_operator(X,Y)</code> - defines a Java class as an arithmetic operator.
 * <p>
 * <code>X</code> represents the name and arity of the predicate. <code>Y</code> represents the full class name of an
 * implementation of <code>org.projog.core.ArithmeticOperator</code>.
 */
public final class AddArithmeticOperator extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term functionNameAndArity, Term javaClass) {
      PredicateKey key = PredicateKey.createFromNameAndArity(functionNameAndArity);
      String className = getAtomName(javaClass);
      getArithmeticOperators().addArithmeticOperator(key, className);
      return true;
   }
}
