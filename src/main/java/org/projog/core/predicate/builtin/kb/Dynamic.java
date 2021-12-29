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
package org.projog.core.predicate.builtin.kb;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.Term;

/* TEST
%?- dynamic(true/0)
%ERROR Cannot replace already defined built-in predicate: true/0

%?- dynamic(is/2)
%ERROR Cannot replace already defined built-in predicate: is/2

%TRUE dynamic(test/2)

%TRUE write_to_file('dynamic1.tmp', 'test(a,1). test(b,2). test(c,3). test(d,4). test(e,5). testRule(X) :- test(X, Y), Y mod 2 =:= 0.')
%TRUE consult('dynamic1.tmp')
%?- testRule(X)
% X=b
% X=d
%NO

%TRUE write_to_file('dynamic2.tmp', 'test(f,6). test(g,7). test(h,8).')
%TRUE consult('dynamic2.tmp')
%?- testRule(X)
% X=b
% X=d
% X=f
% X=h

%TRUE assertz(test(x,180))
%TRUE asserta(test(y,42))

%?- testRule(X)
% X=y
% X=b
% X=d
% X=f
% X=h
% X=x

% Not OK to call dynamic/1 on a predicate that has already been defined and is not marked as dynamic.
%TRUE write_to_file('dynamic3.tmp', 'not_dynamic(1,2,3).')
%TRUE consult('dynamic3.tmp')
%?- dynamic(not_dynamic/3)
%ERROR Predicate has already been defined and is not dynamic: not_dynamic/3

% OK to call dynamic/1 on a predicate that has already been marked as dynamic.
%TRUE dynamic(test/2)

write_to_file(Filename, Contents) :-
   open(Filename, write, Z),
   set_output(Z),
   writef(Contents),
   close(Z),
   set_output('user_output').
*/
/**
 * <code>dynamic/1</code> - indicates that a user defined predicate is dynamic.
 * <p>
 * If a user defined predicate is dynamic then it can be updated after it is first defined. It can subsequently have
 * clauses removed using <code>retract/1</code> and <code>retractall/1</code> or new clauses added using
 * <code>asserta(X)</code>, <code>assertz(X)</code> and <code>consult/1</code>.
 */
public final class Dynamic extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      PredicateKey key = PredicateKey.createFromNameAndArity(arg);
      UserDefinedPredicateFactory pf = getPredicates().createOrReturnUserDefinedPredicate(key);
      if (!pf.isDynamic()) {
         throw new ProjogException("Predicate has already been defined and is not dynamic: " + key);
      }
      return true;
   }
}
