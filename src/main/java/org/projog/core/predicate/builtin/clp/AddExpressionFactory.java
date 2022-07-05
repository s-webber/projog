/*
 * Copyright 2022 S. Webber
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
package org.projog.core.predicate.builtin.clp;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.kb.KnowledgeBaseServiceLocator;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

/* TEST
%?- 7 #= sum(X, 4)
%ERROR Cannot find CLP expression: sum/2

%TRUE pj_add_clp_expression(sum/2, 'org.projog.core.predicate.builtin.clp.CommonExpression/add')

%?- 7 #= sum(X, 4)
% X=3
*/
/**
 * <code>pj_add_clp_expression(X,Y)</code> - defines a Java class as an CLP expression.
 * <p>
 * <code>X</code> represents the name and arity of the expression. <code>Y</code> represents the full class name of an
 * implementation of <code>org.projog.core.predicate.builtin.clp.ExpressionFactory</code>.
 */
public final class AddExpressionFactory extends AbstractSingleResultPredicate {
   private ExpressionFactories expressions;

   @Override
   protected boolean evaluate(Term functionNameAndArity, Term javaClass) {
      PredicateKey key = PredicateKey.createFromNameAndArity(functionNameAndArity);
      String className = getAtomName(javaClass);
      expressions.addExpressionFactory(key, className);
      return true;
   }

   @Override
   protected void init() {
      expressions = KnowledgeBaseServiceLocator.getServiceLocator(getKnowledgeBase()).getInstance(ExpressionFactories.class);
   }
}
