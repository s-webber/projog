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
import org.projog.core.term.TermType;

/* TEST
 %TRUE is_list([1.0,2.0,3.0])
 %TRUE is_list([])
 %TRUE is_list([a|[]])

 %FALSE is_list([a|b])
 %FALSE is_list([a|X])
 %FALSE is_list(X)
 */
/**
 * <code>is_list(X)</code> - checks that a term is a list.
 * <p>
 * <code>is_list(X)</code> succeeds if <code>X</code> currently stands for a
 * list.
 * </p>
 */
public final class IsList extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(final Term arg) {
      switch (arg.getType()) {
         case EMPTY_LIST:
            return true;
         case LIST:
            Term tail = arg;
            while ((tail = tail.getArgument(1)).getType() == TermType.LIST) {
            }
            return tail.getType() == TermType.EMPTY_LIST;
         default:
            return false;
      }
   }
}
