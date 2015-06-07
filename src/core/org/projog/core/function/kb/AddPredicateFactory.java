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

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE pj_add_predicate(xyz/1, 'org.projog.core.function.compound.Call')

 %TRUE xyz(true)
 %QUERY xyz(repeat(3))
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %FALSE xyz(fail)
 */
/**
 * <code>pj_add_predicate(X,Y)</code> - defines a Java class as a built-in predicate.
 * <p>
 * <code>X</code> represents the name and arity of the predicate. <code>Y</code> represents the full class name of an
 * implementation of <code>org.projog.core.PredicateFactory</code>.
 * </p>
 * <p>
 * This predicate provides an easy way to configure and extend the functionality of Projog - including adding
 * functionality not possible to define in pure Prolog syntax.
 * </p>
 */
public final class AddPredicateFactory extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      PredicateKey key = PredicateKey.createFromNameAndArity(arg1);
      String className = getAtomName(arg2);
      getKnowledgeBase().addPredicateFactory(key, className);
      return true;
   }
}
