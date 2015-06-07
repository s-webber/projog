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
package org.projog.core.function.classify;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE compound(a(b,c))
 %TRUE compound(1+1)
 %TRUE compound([a,b,c])
 %FALSE compound([])
 %FALSE compound(abc)
 %FALSE compound(1)
 %FALSE compound(1.5)
 %FALSE compound(X)
 %FALSE compound(_)
 */
/**
 * <code>compound(X)</code> - checks that a term is a compound term.
 * <p>
 * <code>compound(X)</code> succeeds if <code>X</code> currently stands for a compound term.
 * </p>
 */
public final class IsCompound extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg) {
      return arg.getType().isStructure();
   }
}
