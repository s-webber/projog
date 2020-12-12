/*
 * Copyright 2020 S. Webber
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

import static org.projog.core.term.TermUtils.assertType;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.ListFactory;
import org.projog.core.term.ListUtils;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %TRUE append([], [])
 %TRUE append([[]], [])
 %TRUE append([[a]], [a])
 %TRUE append([[a,b,c],[d,e,f,g,h]], [a,b,c,d,e,f,g,h])
 %FALSE append([[a,b,c],[d,e,f,g,h]], [a,b,c,d,e,f,g,x])

 %QUERY append([[a,b,c],[[d,e,f],x,y,z],[1,2,3],[]],X)
 %ANSWER X=[a,b,c,[d,e,f],x,y,z,1,2,3]

 %QUERY append(a, X)
 %ERROR Expected LIST but got: ATOM with value: a

 %QUERY append([a], X)
 %ERROR Expected list but got: ATOM with value: a
 */
/**
 * <code>append(ListOfLists, List)</code> - concatenates a list of lists.
 * <p>
 * The <code>append(ListOfLists, List)</code> goal succeeds if the concatenation of lists contained in
 * <code>ListOfLists</code> matches the list <code>List</code>.
 * </p>
 */
public final class AppendListOfLists extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term listOfLists, Term termToUnifyWith) {
      if (listOfLists.getType() == TermType.EMPTY_LIST) {
         return termToUnifyWith.unify(EmptyList.EMPTY_LIST);
      }
      assertType(listOfLists, TermType.LIST);

      List<Term> input = ListUtils.toJavaUtilList(listOfLists); // avoid converting to java list
      List<Term> output = new ArrayList<>();
      for (Term list : input) {
         List<Term> elements = ListUtils.toJavaUtilList(list);
         if (elements == null) {
            throw new ProjogException("Expected list but got: " + list.getType() + " with value: " + list);
         }
         output.addAll(elements);
      }
      return termToUnifyWith.unify(ListFactory.createList(output));
   }
}
