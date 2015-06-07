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
package org.projog.core.function.kb;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;
import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.Calculatables;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY X is sum(1, 1)
 %ERROR Cannot find calculatable: sum/2
 
 %TRUE pj_add_calculatable(sum/2, 'org.projog.core.function.math.Add')
 
 %QUERY X is sum(1, 1)
 %ANSWER X=2
 */
/**
 * <code>pj_add_calculatable(X,Y)</code> - defines a Java class as an arithmetic function.
 * <p>
 * <code>X</code> represents the name and arity of the predicate. <code>Y</code> represents the full class name of an
 * implementation of <code>org.projog.core.Calculatable</code>.
 */
public final class AddCalculatable extends AbstractSingletonPredicate {
   private Calculatables calculatables;

   @Override
   public void init() {
      calculatables = getCalculatables(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term functionNameAndArity, Term javaClass) {
      PredicateKey key = PredicateKey.createFromNameAndArity(functionNameAndArity);
      String className = getAtomName(javaClass);
      calculatables.addCalculatable(key, className);
      return true;
   }
}
