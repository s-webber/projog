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
%FAIL append([a,b], [d,e,f], [a,b,c,d,e,f])
%FAIL append([a,b,c], [e,f], [a,b,c,d,e,f])
%?- append([W,b,c], [d,Y,f], [a,X,c,d,e,Z])
% W=a
% X=b
% Y=e
% Z=f

% Examples of when first term is a variable:
%?- append(X, [d,e,f], [a,b,c,d,e,f])
% X=[a,b,c]
%NO
%?- append(X, [f], [a,b,c,d,e,f])
% X=[a,b,c,d,e]
%NO
%?- append(X, [b,c,d,e,f], [a,b,c,d,e,f])
% X=[a]
%NO
%?- append(X, [a,b,c,d,e,f], [a,b,c,d,e,f])
% X=[]
%NO
%?- append(X, [], [a,b,c,d,e,f])
% X=[a,b,c,d,e,f]

% Examples of when second term is a variable:
%?- append([a,b,c], X, [a,b,c,d,e,f])
% X=[d,e,f]
%?- append([a,b,c,d,e], X, [a,b,c,d,e,f])
% X=[f]
%?- append([a], X, [a,b,c,d,e,f])
% X=[b,c,d,e,f]
%?- append([], X, [a,b,c,d,e,f])
% X=[a,b,c,d,e,f]
%?- append([a,b,c,d,e,f], X, [a,b,c,d,e,f])
% X=[]

% Examples of when third term is a variable:
%?- append([a,b,c], [d,e,f], X)
% X=[a,b,c,d,e,f]
%?- append([a], [b,c,d,e,f], X)
% X=[a,b,c,d,e,f]
%?- append([a,b,c,d,e], [f], X)
% X=[a,b,c,d,e,f]
%?- append([a,b,c,d,e,f], [], X)
% X=[a,b,c,d,e,f]
%?- append([], [a,b,c,d,e,f], X)
% X=[a,b,c,d,e,f]
%?- append([], [], X)
% X=[]

% Examples of when first and second terms are variables:
%?- append(X, Y, [a,b,c,d,e,f])
% X=[]
% Y=[a,b,c,d,e,f]
% X=[a]
% Y=[b,c,d,e,f]
% X=[a,b]
% Y=[c,d,e,f]
% X=[a,b,c]
% Y=[d,e,f]
% X=[a,b,c,d]
% Y=[e,f]
% X=[a,b,c,d,e]
% Y=[f]
% X=[a,b,c,d,e,f]
% Y=[]
%?- append(X, Y, [a])
% X=[]
% Y=[a]
% X=[a]
% Y=[]
%?- append(X, Y, [])
% X=[]
% Y=[]

% Examples when combination of term types cause failure:
%FAIL append(a, b, Z)
%FAIL append(a, b, c)
%FAIL append(a, [], [])
%FAIL append([], b, [])
%FAIL append([], [], c)

%?- append([], tail, Z)
% Z=tail

%?- append([], Z, tail)
% Z=tail

%?- append([a], b, X)
% X=[a|b]

%?- append([a,b,c], d, X)
% X=[a,b,c|d]

%?- append([a], [], X)
% X=[a]

%?- append([a], [b], X)
% X=[a,b]

%?- append([X|Y],['^'],[a,b,c,^])
% X=a
% Y=[b,c]
%NO

%FAIL append([X|Y],['^'],[a,b,c,^,z])

%?- append([X|Y],['^'],[a,b,c,^,z,^])
% X=a
% Y=[b,c,^,z]
%NO

%?- append([X|Y],['^'],[a,b,c,^,^])
% X=a
% Y=[b,c,^]
%NO

%FAIL append([a|b], [b|c], X)
%FAIL append([a|b], [b|c], [a,b,c,d])
%FAIL append([a|b], X, [a,b,c,d])
%FAIL append(X, [b|c], [a,b,c,d])
%FAIL append([a|b], X, Y)

%FAIL append([a, a], X, [a])
%FAIL append(X,[a,a],[a])
%FAIL append([a,a],X,[])
%FAIL append(X,[a,a],[])

%?- append(X,[a,a],[a,a])
% X=[]
%NO
%?- append([a,a],X,[a,a])
% X=[]
%?- append(X,[],[a,a])
% X=[a,a]
%?- append([],X,[a,a])
% X=[a,a]
%?- append([],[],X)
% X=[]

%?- append(Left,[x|Right],[a,x,b,c,d,x,e,f])
% Left=[a]
% Right=[b,c,d,x,e,f]
% Left=[a,x,b,c,d]
% Right=[e,f]
%NO

%?- append(Left,[x,b|Right],[a,x,b,c,d,x,e,f])
% Left=[a]
% Right=[c,d,x,e,f]
%NO

%?- append([a|X],[a|Y],[a,a,a,a,a,a,a])
% X=[]
% Y=[a,a,a,a,a]
% X=[a]
% Y=[a,a,a,a]
% X=[a,a]
% Y=[a,a,a]
% X=[a,a,a]
% Y=[a,a]
% X=[a,a,a,a]
% Y=[a]
% X=[a,a,a,a,a]
% Y=[]
%NO

%?- append([a,a|X],[a|Y],[a,a,a,a,a,a,a])
% X=[]
% Y=[a,a,a,a]
% X=[a]
% Y=[a,a,a]
% X=[a,a]
% Y=[a,a]
% X=[a,a,a]
% Y=[a]
% X=[a,a,a,a]
% Y=[]
%NO

%?- append([a|X],[Y|[a]],[a,a,a,a,a,a,a])
% X=[a,a,a,a]
% Y=a
%NO

%?- append([X|[a]],Y,[a,a,a,a,a,a,a])
% X=a
% Y=[a,a,a,a,a]

%FAIL append([X|[a]],[Y|[a]],[a,a,a,a,a,a,a])

%?- append([a,a,a],[a,a,a],[a|X])
% X=[a,a,a,a,a]

%?- append([a,b|X],[d,e|Y],Z)
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,b,d,e|Y]
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,b,X,d,e|Y]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,b,X,X,d,e|Y]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,b,X,X,X,d,e|Y]
%QUIT

%?- append(X,Y,Z)
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X|L3]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X|L3]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,X|L3]
%QUIT

%?- append(X,[],Z)
% X=[]
% Z=[]
% X=[X]
% Z=[X]
% X=[X,X]
% Z=[X,X]
% X=[X,X,X]
% Z=[X,X,X]
%QUIT

%?- append(X,[b|c],Z)
% X=[]
% Z=[b|c]
% X=[X]
% Z=[X,b|c]
% X=[X,X]
% Z=[X,X,b|c]
% X=[X,X,X]
% Z=[X,X,X,b|c]
%QUIT

%?- append([a,b|X],[d,e|Y],[a|Z])
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=[b,d,e|Y]
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[b,X,d,e|Y]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[b,X,X,d,e|Y]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[b,X,X,X,d,e|Y]
%QUIT

%?- append([a,b|X],[d,e|Y],[a,b|Z])
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=[d,e|Y]
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,d,e|Y]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,d,e|Y]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,X,d,e|Y]
%QUIT

%?- append([a,b|X],[d,e|Y],[a,b,c|Z])
% X=[c]
% Y=UNINSTANTIATED VARIABLE
% Z=[d,e|Y]
% X=[c,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,d,e|Y]
% X=[c,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,d,e|Y]
% X=[c,X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,X,d,e|Y]
%QUIT

%?- append([a|X],Y,Z)
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=[a|L3]
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,X|L3]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,X,X|L3]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,X,X,X|L3]
%QUIT

%?- append([a|X],[z],Z)
% X=[]
% Z=[a,z]
% X=[X]
% Z=[a,X,z]
% X=[X,X]
% Z=[a,X,X,z]
% X=[X,X,X]
% Z=[a,X,X,X,z]
%QUIT

%?- append([a|X],z,Z)
% X=[]
% Z=[a|z]
% X=[X]
% Z=[a,X|z]
% X=[X,X]
% Z=[a,X,X|z]
% X=[X,X,X]
% Z=[a,X,X,X|z]
%QUIT

%?- append([a|X],[z|Y],Z)
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,z|Y]
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,X,z|Y]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,X,X,z|Y]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,X,X,X,z|Y]
%QUIT

%FAIL append(a,b,Z)
%FAIL append([a],b,c)
%FAIL append(a,b,c)

%?- append([a],b,Z)
% Z=[a|b]

%?- append(X,Y,c)
% X=[]
% Y=c
%NO

%?- append([a|X],b,Z)
% X=[]
% Z=[a|b]
% X=[X]
% Z=[a,X|b]
% X=[X,X]
% Z=[a,X,X|b]
% X=[X,X,X]
% Z=[a,X,X,X|b]
%QUIT

%?- append([a,b,c|X],z,Z)
% X=[]
% Z=[a,b,c|z]
% X=[X]
% Z=[a,b,c,X|z]
% X=[X,X]
% Z=[a,b,c,X,X|z]
% X=[X,X,X]
% Z=[a,b,c,X,X,X|z]
%QUIT

%?- append([a,b,c|X],[z],Z)
% X=[]
% Z=[a,b,c,z]
% X=[X]
% Z=[a,b,c,X,z]
% X=[X,X]
% Z=[a,b,c,X,X,z]
% X=[X,X,X]
% Z=[a,b,c,X,X,X,z]
%QUIT

%?- append([a,b,c|X],[],Z)
% X=[]
% Z=[a,b,c]
% X=[X]
% Z=[a,b,c,X]
% X=[X,X]
% Z=[a,b,c,X,X]
% X=[X,X,X]
% Z=[a,b,c,X,X,X]
%QUIT

%?- append([a],[a,b,c|X],Z)
% X=UNINSTANTIATED VARIABLE
% Z=[a,a,b,c|X]

%?- append(X,[a,b,c|Y],Z)
% X=[]
% Y=UNINSTANTIATED VARIABLE
% Z=[a,b,c|Y]
% X=[X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,a,b,c|Y]
% X=[X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,a,b,c|Y]
% X=[X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,X,a,b,c|Y]
%QUIT

%?- append(X,z,Z)
% X=[]
% Z=z
% X=[X]
% Z=[X|z]
% X=[X,X]
% Z=[X,X|z]
% X=[X,X,X]
% Z=[X,X,X|z]
%QUIT

%?- append(X,Y,[Z|1])
% X=[]
% Y=[Z|1]
% Z=UNINSTANTIATED VARIABLE
% X=[Z]
% Y=1
% Z=UNINSTANTIATED VARIABLE
%NO

%?- append(X,Y,[Z,b|1])
% X=[]
% Y=[Z,b|1]
% Z=UNINSTANTIATED VARIABLE
% X=[Z]
% Y=[b|1]
% Z=UNINSTANTIATED VARIABLE
% X=[Z,b]
% Y=1
% Z=UNINSTANTIATED VARIABLE
%NO

%?- append(X,Y,[a,b,c,d|1])
% X=[]
% Y=[a,b,c,d|1]
% X=[a]
% Y=[b,c,d|1]
% X=[a,b]
% Y=[c,d|1]
% X=[a,b,c]
% Y=[d|1]
% X=[a,b,c,d]
% Y=1
%NO

%?- append(X,Y,[a,b,c,d|Z])
% X=[]
% Y=[a,b,c,d|Z]
% Z=UNINSTANTIATED VARIABLE
% X=[a]
% Y=[b,c,d|Z]
% Z=UNINSTANTIATED VARIABLE
% X=[a,b]
% Y=[c,d|Z]
% Z=UNINSTANTIATED VARIABLE
% X=[a,b,c]
% Y=[d|Z]
% Z=UNINSTANTIATED VARIABLE
% X=[a,b,c,d]
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=[a,b,c,d,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X|L3]
% X=[a,b,c,d,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X|L3]
% X=[a,b,c,d,X,X,X]
% Y=UNINSTANTIATED VARIABLE
% Z=[X,X,X|L3]
%QUIT

%?- append([a|X],[x,y,z],[a,b,c|Z])
% X=[b,c]
% Z=[x,y,z]
% X=[b,c,X]
% Z=[X,x,y,z]
% X=[b,c,X,X]
% Z=[X,X,x,y,z]
% X=[b,c,X,X,X]
% Z=[X,X,X,x,y,z]
%QUIT

%?- append([a,b|X],[c,d|X],Y)
% X=[]
% Y=[a,b,c,d]
% X=[X]
% Y=[a,b,X,c,d,X]
% X=[X,X]
% Y=[a,b,X,X,c,d,X,X]
% X=[X,X,X]
% Y=[a,b,X,X,X,c,d,X,X,X]
%QUIT

%?- append([a,b|X],[c,d|X],Y), numbervars(Y)
% X=[]
% Y=[a,b,c,d]
% X=[$VAR(0)]
% Y=[a,b,$VAR(0),c,d,$VAR(0)]
% X=[$VAR(0),$VAR(1)]
% Y=[a,b,$VAR(0),$VAR(1),c,d,$VAR(0),$VAR(1)]
% X=[$VAR(0),$VAR(1),$VAR(2)]
% Y=[a,b,$VAR(0),$VAR(1),$VAR(2),c,d,$VAR(0),$VAR(1),$VAR(2)]
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
