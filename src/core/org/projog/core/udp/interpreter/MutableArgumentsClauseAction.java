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
 * A fact (works with variable arguments).
 * <p>
 * e.g. {@code p(a,Y,Z).} or {@code p(X,Y,X).}
 */
public final class MutableArgumentsClauseAction extends AbstractFactClauseAction {
   MutableArgumentsClauseAction(Term[] consequentArgs) {
      super(consequentArgs);
   }

   @Override
   public MutableArgumentsClauseAction getFree() {
      Term[] newConsequentArgs = TermUtils.copy(getConsequentArgs());
      return new MutableArgumentsClauseAction(newConsequentArgs);
   }
}
