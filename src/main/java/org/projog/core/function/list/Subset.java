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
package org.projog.core.function.list;

import static org.projog.core.term.ListUtils.isMember;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %TRUE subset([],[])
 %TRUE subset([],[a,b,c])
 %TRUE subset([a],[a,b,c])
 %TRUE subset([b,c],[a,b,c])
 %TRUE subset([a,b,c],[a,b,c])
 %TRUE subset([c,a,b],[a,b,c])
 %TRUE subset([c,a,c,b,b,c],[b,a,b,a,c])

 %FALSE subset([a,b,c,d],[a,b,c])
 %FALSE subset([a,b,c],[])
 */
/**
 * <code>subset(X,Y)</code> - checks if a set is a subset.
 * <p>
 * True if each of the elements in the list represented by <code>X</code> can be unified with elements in the list
 * represented by <code>Y</code>.
 * </p>
 */
public final class Subset extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term subsetTerm, Term set) {
      for (Term element : toJavaUtilList(subsetTerm)) {
         if (!isMember(element, set)) {
            return false;
         }
      }
      return true;
   }
}
