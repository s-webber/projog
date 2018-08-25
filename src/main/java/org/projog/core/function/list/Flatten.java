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

import java.util.ArrayList;
import java.util.List;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %QUERY flatten([a,[[b]],[c]], X)
 %ANSWER X=[a,b,c]
 
 %QUERY flatten([a,b,c], X)
 %ANSWER X=[a,b,c]

 %QUERY flatten([[[[a]]],[],[],[]], X)
 %ANSWER X=[a]

 %QUERY flatten([a], X)
 %ANSWER X=[a]
 
 %QUERY flatten(a, X)
 %ANSWER X=[a]

 %QUERY flatten([[[[]]],[],[],[]], X)
 %ANSWER X=[]

 %QUERY flatten([], X)
 %ANSWER X=[]
 
 %QUERY flatten([a|b], X)
 %ANSWER X=[a,b]

 %QUERY flatten([a|[]], X)
 %ANSWER X=[a]
 
 %QUERY flatten([[a|b],[c,d|e],[f|[]],g|h], X)
 %ANSWER X=[a,b,c,d,e,f,g,h]
 
 %QUERY flatten([p([[a]]),[[[p(p(x))]],[p([a,b,c])]]], X)
 %ANSWER X=[p([[a]]),p(p(x)),p([a,b,c])]
 
 %FALSE flatten([a,b,c], [c,b,a])
 %FALSE flatten([a,b,c], [a,[b],c])
 */
/**
 * <code>flatten(X,Y)</code> - flattens a nested list.
 * <p>
 * Flattens the nested list represented by <code>X</code> and attempts to unify it with <code>Y</code>.
 */
public final class Flatten extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(final Term original, final Term expected) {
      final Term flattenedVersion;
      switch (original.getType()) {
         case LIST:
            flattenedVersion = ListFactory.createList(flattenList(original));
            break;
         case EMPTY_LIST:
            flattenedVersion = original;
            break;
         default:
            flattenedVersion = ListFactory.createList(original, EmptyList.EMPTY_LIST);
      }
      return expected.unify(flattenedVersion);
   }

   private List<Term> flattenList(final Term input) {
      List<Term> result = new ArrayList<Term>();
      Term next = input;
      while (next.getType() == TermType.LIST) {
         Term head = next.getArgument(0);
         if (head.getType() == TermType.LIST) {
            result.addAll(flattenList(head));
         } else if (head.getType() != TermType.EMPTY_LIST) {
            result.add(head);
         }

         next = next.getArgument(1);
      }
      if (next.getType() != TermType.EMPTY_LIST) {
         result.add(next);
      }
      return result;
   }
}
