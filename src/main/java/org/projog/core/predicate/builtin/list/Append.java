/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.builtin.list;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.List;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
 % Examples of when all three terms are lists:
 %TRUE append([a,b,c], [d,e,f], [a,b,c,d,e,f])
 %TRUE append([a,b,c], [a,b,c], [a,b,c,a,b,c])
 %TRUE append([a], [b,c,d,e,f], [a,b,c,d,e,f])
 %TRUE append([a,b,c,d,e], [f], [a,b,c,d,e,f])
 %TRUE append([a,b,c,d,e,f], [], [a,b,c,d,e,f])
 %TRUE append([], [a,b,c,d,e,f], [a,b,c,d,e,f])
 %TRUE append([], [], [])
 %FALSE append([a,b], [d,e,f], [a,b,c,d,e,f])
 %FALSE append([a,b,c], [e,f], [a,b,c,d,e,f])
 %QUERY append([W,b,c], [d,Y,f], [a,X,c,d,e,Z])
 %ANSWER
 % W=a
 % X=b
 % Y=e
 % Z=f
 %ANSWER

 % Examples of when first term is a variable:
 %QUERY append(X, [d,e,f], [a,b,c,d,e,f])
 %ANSWER X=[a,b,c]
 %NO
 %QUERY append(X, [f], [a,b,c,d,e,f])
 %ANSWER X=[a,b,c,d,e]
 %NO
 %QUERY append(X, [b,c,d,e,f], [a,b,c,d,e,f])
 %ANSWER X=[a]
 %NO
 %QUERY append(X, [a,b,c,d,e,f], [a,b,c,d,e,f])
 %ANSWER X=[]
 %NO
 %QUERY append(X, [], [a,b,c,d,e,f])
 %ANSWER X=[a,b,c,d,e,f]

 % Examples of when second term is a variable:
 %QUERY append([a,b,c], X, [a,b,c,d,e,f])
 %ANSWER X=[d,e,f]
 %QUERY append([a,b,c,d,e], X, [a,b,c,d,e,f])
 %ANSWER X=[f]
 %QUERY append([a], X, [a,b,c,d,e,f])
 %ANSWER X=[b,c,d,e,f]
 %QUERY append([], X, [a,b,c,d,e,f])
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a,b,c,d,e,f], X, [a,b,c,d,e,f])
 %ANSWER X=[]

 % Examples of when third term is a variable:
 %QUERY append([a,b,c], [d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a], [b,c,d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a,b,c,d,e], [f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([a,b,c,d,e,f], [], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([], [a,b,c,d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 %QUERY append([], [], X)
 %ANSWER X=[]

 % Examples of when first and second terms are variables:
 %QUERY append(X, Y, [a,b,c,d,e,f])
 %ANSWER
 % X=[]
 % Y=[a,b,c,d,e,f]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[b,c,d,e,f]
 %ANSWER
 %ANSWER
 % X=[a,b]
 % Y=[c,d,e,f]
 %ANSWER
 %ANSWER
 % X=[a,b,c]
 % Y=[d,e,f]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d]
 % Y=[e,f]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,e]
 % Y=[f]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,e,f]
 % Y=[]
 %ANSWER
 %QUERY append(X, Y, [a])
 %ANSWER
 % X=[]
 % Y=[a]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[]
 %ANSWER
 %QUERY append(X, Y, [])
 %ANSWER
 % X=[]
 % Y=[]
 %ANSWER

 % Examples when combination of term types cause failure:
 %FALSE append(a, b, Z)
 %FALSE append(a, b, c)
 %FALSE append(a, [], [])
 %FALSE append([], b, [])
 %FALSE append([], [], c)

 %QUERY append([], tail, Z)
 %ANSWER Z=tail

 %QUERY append([], Z, tail)
 %ANSWER Z=tail

 %QUERY append([a], b, X)
 %ANSWER X = [a|b]

 %QUERY append([a,b,c], d, X)
 %ANSWER X = [a,b,c|d]

 %QUERY append([a], [], X)
 %ANSWER X = [a]

 %QUERY append([a], [b], X)
 %ANSWER X = [a,b]

 %QUERY append([X|Y],['^'],[a,b,c,^])
 %ANSWER
 % X = a
 % Y = [b,c]
 %ANSWER
 %NO

 %FALSE append([X|Y],['^'],[a,b,c,^,z])

 %QUERY append([X|Y],['^'],[a,b,c,^,z,^])
 %ANSWER
 % X = a
 % Y = [b,c,^,z]
 %ANSWER
 %NO

 %QUERY append([X|Y],['^'],[a,b,c,^,^])
 %ANSWER
 % X = a
 % Y = [b,c,^]
 %ANSWER
 %NO

 %FALSE append([a|b], [b|c], X)
 %FALSE append([a|b], [b|c], [a,b,c,d])
 %FALSE append([a|b], X, [a,b,c,d])
 %FALSE append(X, [b|c], [a,b,c,d])
 %FALSE append([a|b], X, Y)

 %FALSE append([a, a], X, [a])
 %FALSE append(X,[a,a],[a])
 %FALSE append([a,a],X,[])
 %FALSE append(X,[a,a],[])

 %QUERY append(X,[a,a],[a,a])
 %ANSWER X=[]
 %NO
 %QUERY append([a,a],X,[a,a])
 %ANSWER X=[]
 %QUERY append(X,[],[a,a])
 %ANSWER X=[a,a]
 %QUERY append([],X,[a,a])
 %ANSWER X=[a,a]
 %QUERY append([],[],X)
 %ANSWER X=[]

 %QUERY append(Left,[x|Right],[a,x,b,c,d,x,e,f])
 %ANSWER
 % Left=[a]
 % Right=[b,c,d,x,e,f]
 %ANSWER
 %ANSWER
 % Left=[a,x,b,c,d]
 % Right=[e,f]
 %ANSWER
 %NO

 %QUERY append(Left,[x,b|Right],[a,x,b,c,d,x,e,f])
 %ANSWER
 % Left=[a]
 % Right=[c,d,x,e,f]
 %ANSWER
 %NO

 %QUERY append([a|X],[a|Y],[a,a,a,a,a,a,a])
 %ANSWER
 % X=[]
 % Y=[a,a,a,a,a]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[a,a,a,a]
 %ANSWER
 %ANSWER
 % X=[a,a]
 % Y=[a,a,a]
 %ANSWER
 %ANSWER
 % X=[a,a,a]
 % Y=[a,a]
 %ANSWER
 %ANSWER
 % X=[a,a,a,a]
 % Y=[a]
 %ANSWER
 %ANSWER
 % X=[a,a,a,a,a]
 % Y=[]
 %ANSWER
 %NO

 %QUERY append([a,a|X],[a|Y],[a,a,a,a,a,a,a])
 %ANSWER
 % X=[]
 % Y=[a,a,a,a]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[a,a,a]
 %ANSWER
 %ANSWER
 % X=[a,a]
 % Y=[a,a]
 %ANSWER
 %ANSWER
 % X=[a,a,a]
 % Y=[a]
 %ANSWER
 %ANSWER
 % X=[a,a,a,a]
 % Y=[]
 %ANSWER
 %NO

 %QUERY append([a|X],[Y|[a]],[a,a,a,a,a,a,a])
 %ANSWER
 % X=[a,a,a,a]
 % Y=a
 %ANSWER
 %NO

 %QUERY append([X|[a]],Y,[a,a,a,a,a,a,a])
 %ANSWER
 % X=a
 % Y=[a,a,a,a,a]
 %ANSWER

 %FALSE append([X|[a]],[Y|[a]],[a,a,a,a,a,a,a])

 %QUERY append([a,a,a],[a,a,a],[a|X])
 %ANSWER X=[a,a,a,a,a]

 %QUERY append([a,b|X],[d,e|Y],Z)
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,b,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,b,X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,b,X,X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,b,X,X,X,d,e|Y]
 %ANSWER
 %QUIT

 %QUERY append(X,Y,Z)
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X|L3]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X|L3]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,X|L3]
 %ANSWER
 %QUIT

 %QUERY append(X,[],Z)
 %ANSWER
 % X=[]
 % Z=[]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[X]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[X,X]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[X,X,X]
 %ANSWER
 %QUIT

 %QUERY append(X,[b|c],Z)
 %ANSWER
 % X=[]
 % Z=[b|c]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[X,b|c]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[X,X,b|c]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[X,X,X,b|c]
 %ANSWER
 %QUIT

 %QUERY append([a,b|X],[d,e|Y],[a|Z])
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[b,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[b,X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[b,X,X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[b,X,X,X,d,e|Y]
 %ANSWER
 %QUIT

 %QUERY append([a,b|X],[d,e|Y],[a,b|Z])
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,X,d,e|Y]
 %ANSWER
 %QUIT

 %QUERY append([a,b|X],[d,e|Y],[a,b,c|Z])
 %ANSWER
 % X=[c]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[d,e|Y]
 %ANSWER
 %ANSWER
 % X=[c,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[c,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,d,e|Y]
 %ANSWER
 %ANSWER
 % X=[c,X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,X,d,e|Y]
 %ANSWER
 %QUIT

 %QUERY append([a|X],Y,Z)
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a|L3]
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,X|L3]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,X,X|L3]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,X,X,X|L3]
 %ANSWER
 %QUIT

 %QUERY append([a|X],[z],Z)
 %ANSWER
 % X=[]
 % Z=[a,z]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[a,X,z]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[a,X,X,z]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[a,X,X,X,z]
 %ANSWER
 %QUIT

 %QUERY append([a|X],z,Z)
 %ANSWER
 % X=[]
 % Z=[a|z]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[a,X|z]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[a,X,X|z]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[a,X,X,X|z]
 %ANSWER
 %QUIT

 %QUERY append([a|X],[z|Y],Z)
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,z|Y]
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,X,z|Y]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,X,X,z|Y]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,X,X,X,z|Y]
 %ANSWER
 %QUIT

 %FALSE append(a,b,Z)
 %FALSE append([a],b,c)
 %FALSE append(a,b,c)

 %QUERY append([a],b,Z)
 %ANSWER Z=[a|b]

 %QUERY append(X,Y,c)
 %ANSWER
 % X=[]
 % Y=c
 %ANSWER
 %NO

 %QUERY append([a|X],b,Z)
 %ANSWER
 % X=[]
 % Z=[a|b]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[a,X|b]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[a,X,X|b]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[a,X,X,X|b]
 %ANSWER
 %QUIT

 %QUERY append([a,b,c|X],z,Z)
 %ANSWER
 % X=[]
 % Z=[a,b,c|z]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[a,b,c,X|z]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[a,b,c,X,X|z]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[a,b,c,X,X,X|z]
 %ANSWER
 %QUIT

 %QUERY append([a,b,c|X],[z],Z)
 %ANSWER
 % X=[]
 % Z=[a,b,c,z]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[a,b,c,X,z]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[a,b,c,X,X,z]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[a,b,c,X,X,X,z]
 %ANSWER
 %QUIT

 %QUERY append([a,b,c|X],[],Z)
 %ANSWER
 % X=[]
 % Z=[a,b,c]
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[a,b,c,X]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[a,b,c,X,X]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[a,b,c,X,X,X]
 %ANSWER
 %QUIT

 %QUERY append([a],[a,b,c|X],Z)
 %ANSWER
 % X=UNINSTANTIATED VARIABLE
 % Z=[a,a,b,c|X]
 %ANSWER

 %QUERY append(X,[a,b,c|Y],Z)
 %ANSWER
 % X=[]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[a,b,c|Y]
 %ANSWER
 %ANSWER
 % X=[X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,a,b,c|Y]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,a,b,c|Y]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,X,a,b,c|Y]
 %ANSWER
 %QUIT

 %QUERY append(X,z,Z)
 %ANSWER
 % X=[]
 % Z=z
 %ANSWER
 %ANSWER
 % X=[X]
 % Z=[X|z]
 %ANSWER
 %ANSWER
 % X=[X,X]
 % Z=[X,X|z]
 %ANSWER
 %ANSWER
 % X=[X,X,X]
 % Z=[X,X,X|z]
 %ANSWER
 %QUIT

 %QUERY append(X,Y,[Z|1])
 %ANSWER
 % X=[]
 % Y=[Z|1]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[Z]
 % Y=1
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %NO

 %QUERY append(X,Y,[Z,b|1])
 %ANSWER
 % X=[]
 % Y=[Z,b|1]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[Z]
 % Y=[b|1]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[Z,b]
 % Y=1
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %NO

 %QUERY append(X,Y,[a,b,c,d|1])
 %ANSWER
 % X=[]
 % Y=[a,b,c,d|1]
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[b,c,d|1]
 %ANSWER
 %ANSWER
 % X=[a,b]
 % Y=[c,d|1]
 %ANSWER
 %ANSWER
 % X=[a,b,c]
 % Y=[d|1]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d]
 % Y=1
 %ANSWER
 %NO

 %QUERY append(X,Y,[a,b,c,d|Z])
 %ANSWER
 % X=[]
 % Y=[a,b,c,d|Z]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[a]
 % Y=[b,c,d|Z]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[a,b]
 % Y=[c,d|Z]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[a,b,c]
 % Y=[d|Z]
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[a,b,c,d]
 % Y=UNINSTANTIATED VARIABLE
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X|L3]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X|L3]
 %ANSWER
 %ANSWER
 % X=[a,b,c,d,X,X,X]
 % Y=UNINSTANTIATED VARIABLE
 % Z=[X,X,X|L3]
 %ANSWER
 %QUIT

 %QUERY append([a|X],[x,y,z],[a,b,c|Z])
 %ANSWER
 % X=[b,c]
 % Z=[x,y,z]
 %ANSWER
 %ANSWER
 % X=[b,c,X]
 % Z=[X,x,y,z]
 %ANSWER
 %ANSWER
 % X=[b,c,X,X]
 % Z=[X,X,x,y,z]
 %ANSWER
 %ANSWER
 % X=[b,c,X,X,X]
 % Z=[X,X,X,x,y,z]
 %ANSWER
 %QUIT

 %QUERY append([a,b|X],[c,d|X],Y)
 %ANSWER
 % X = []
 % Y = [a,b,c,d]
 %ANSWER
 %ANSWER
 % X = [X]
 % Y = [a,b,X,c,d,X]
 %ANSWER
 %ANSWER
 % X = [X,X]
 % Y = [a,b,X,X,c,d,X,X]
 %ANSWER
 %ANSWER
 % X = [X,X,X]
 % Y = [a,b,X,X,X,c,d,X,X,X]
 %ANSWER
 %QUIT

 %QUERY append([a,b|X],[c,d|X],Y), numbervars(Y)
 %ANSWER
 % X = []
 % Y = [a,b,c,d]
 %ANSWER
 %ANSWER
 % X = [$VAR(0)]
 % Y = [a,b,$VAR(0),c,d,$VAR(0)]
 %ANSWER
 %ANSWER
 % X = [$VAR(0),$VAR(1)]
 % Y = [a,b,$VAR(0),$VAR(1),c,d,$VAR(0),$VAR(1)]
 %ANSWER
 %ANSWER
 % X = [$VAR(0),$VAR(1),$VAR(2)]
 % Y = [a,b,$VAR(0),$VAR(1),$VAR(2),c,d,$VAR(0),$VAR(1),$VAR(2)]
 %ANSWER
 %QUIT
 */
/**
 * <code>append(X,Y,Z)</code> - concatenates two lists.
 * <p>
 * The <code>append(X,Y,Z)</code> goal succeeds if the concatenation of lists <code>X</code> and <code>Y</code> matches
 * the list <code>Z</code>.
 * </p>
 */
public final class Append extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term prefix, Term suffix, Term concatenated) {
      return new AppendPredicate(prefix, suffix, concatenated);
   }

   private final static class AppendPredicate implements Predicate {
      Term prefix;
      Term suffix;
      Term concatenated;
      boolean retrying;

      AppendPredicate(Term prefix, Term suffix, Term concatenated) {
         this.prefix = prefix;
         this.suffix = suffix;
         this.concatenated = concatenated;
      }

      @Override
      public boolean evaluate() {
         while (true) {
            // conc([],L,L).
            if (!retrying && prefix.unify(EmptyList.EMPTY_LIST) && suffix.unify(concatenated)) {
               retrying = true;
               return true;
            }
            retrying = false;

            prefix.backtrack();
            suffix.backtrack();
            concatenated.backtrack();

            //conc([X|L1],L2,[X|L3]) :- conc(L1,L2,L3).
            Term x = null;
            Term l1 = null;
            Term l3 = null;
            if (prefix.getType() == TermType.LIST) {
               x = prefix.getArgument(0);
               l1 = prefix.getArgument(1);
            }
            if (concatenated.getType() == TermType.LIST) {
               if (x == null) {
                  x = concatenated.getArgument(0);
               } else if (!x.unify(concatenated.getArgument(0))) {
                  return false;
               }
               l3 = concatenated.getArgument(1);
            }
            if (x == null) {
               x = new Variable("X");
            }
            if (prefix.getType().isVariable()) {
               l1 = new Variable("L1");
               prefix.unify(new List(x, l1));
            }
            if (l1 == null) {
               return false;
            }
            if (concatenated.getType().isVariable()) {
               l3 = new Variable("L3");
               concatenated.unify(new List(x, l3));
            }
            if (l3 == null) {
               if (concatenated.getType() == TermType.LIST) {
                  l3 = concatenated.getArgument(1);
               } else {
                  return false;
               }
            }

            prefix = l1.getTerm();
            suffix = suffix.getTerm();
            concatenated = l3.getTerm();
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return !retrying || (prefix != EmptyList.EMPTY_LIST && concatenated != EmptyList.EMPTY_LIST);
      }
   }
}
