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
package org.projog.core.udp.interpreter;

import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/**
 * Facts are clauses that have a body of {@code true}.
 * <p>
 * As facts have a body of {@code true} then, if the head unifies with the query, the clause will always be successfully
 * evaluated once and only once.
 * <p>
 * e.g. {@code p(a,b,c).} or {@code p :- true.}
 */
abstract class AbstractFactClauseAction implements ClauseAction {
   private final Term[] consequentArgs;

   AbstractFactClauseAction(Term[] consequentArgs) {
      this.consequentArgs = consequentArgs;
   }

   @Override
   public AbstractFactClauseAction getFree() {
      return this;
   }

   @Override
   public final boolean couldReevaluationSucceed() {
      return false;
   }

   @Override
   public boolean evaluate(Term[] queryArgs) {
      return TermUtils.unify(queryArgs, consequentArgs);
   }

   protected Term[] getConsequentArgs() {
      return consequentArgs;
   }
}
