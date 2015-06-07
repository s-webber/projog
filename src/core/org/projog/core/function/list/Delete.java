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

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import java.util.Iterator;
import java.util.List;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY delete([a,b,c],a,X)
 %ANSWER X=[b,c]
 %QUERY delete([a,b,c],b,X)
 %ANSWER X=[a,c]
 %QUERY delete([a,b,c],c,X)
 %ANSWER X=[a,b]
 %QUERY delete([a,b,c],z,X)
 %ANSWER X=[a,b,c]
 
 %QUERY delete([a,b,X],a,[Y,c])
 %ANSWER 
 % X=c
 % Y=b
 %ANSWER

 %QUERY delete([a,b,c],Y,X)
 %ANSWER 
 % X=[a,b,c]
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY delete([a,Y,c],b,X)
 %ANSWER 
 % X=[a,Y,c]
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY delete([a,Y,_],_,X)
 %ANSWER 
 % X=[a,Y,_]
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER
 %QUERY W=Y,delete([a,Y,_],W,X)
 %ANSWER 
 % X=[a,_]
 % W=UNINSTANTIATED VARIABLE
 % Y=UNINSTANTIATED VARIABLE
 %ANSWER

 %QUERY delete([],a,X)
 %ANSWER X=[]
 */
/**
 * <code>delete(X,Y,Z)</code> - remove all occurrences of a term from a list.
 * <p>
 * Removes all occurrences of the term <code>Y</code> in the list represented by <code>X</code> and attempts to unify
 * the result with <code>Z</code>. Strict term equality is used to identify occurrences.
 */
public final class Delete extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term input, Term element, Term output) {
      List<Term> javaList = toJavaUtilList(input);
      if (javaList == null) {
         return false;
      }

      Iterator<Term> itr = javaList.iterator();
      while (itr.hasNext()) {
         Term next = itr.next();
         if (element.strictEquality(next)) {
            itr.remove();
         }
      }

      return output.unify(createList(javaList));
   }
}
