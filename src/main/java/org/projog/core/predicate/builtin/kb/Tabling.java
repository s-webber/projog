/*
 * Copyright 2025 S. Webber
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

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.TabledUserDefinedPredicateFactory;
import org.projog.core.term.Term;

/* TEST
:- table(fib/2).

fib(0, 1) :- !.
fib(1, 1) :- !.
fib(N, F) :-
        N > 1,
        N1 is N-1,
        N2 is N-2,
        fib(N1, F1),
        fib(N2, F2),
        F is F1+F2.

%?- fib(91, X)
% X=7540113804746346429

:- table(connection/2).

connection(X, Y) :- write(X), write(' '), write(Y), nl, fail.
connection(X, Y) :-
        connection(X, Z),
        connection(Z, Y).
connection(X, Y) :-
        connection(Y, X).

connection(a, b).
connection(a, c).
connection(b, d).
connection(c, d).

%?- connection(a, X)
%OUTPUT
%a Y
%X a
%X Y
%b Y
%X b
%b b
%d b
%d Y
%X d
%b d
%d d
%c d
%c Y
%X c
%b c
%d c
%c b
%c c
%a c
%c a
%d a
%a d
%a b
%b a
%a a
%
%OUTPUT
% X=b
% X=c
% X=a
% X=d
%?- connection(a, X)
% X=b
% X=c
% X=a
% X=d

:- table(path/2).

edge(a, b).
edge(b, c).
edge(c, a).
edge(c, d).

path(X, Y) :-
        edge(X, Y).
path(X, Y) :-
        edge(X, Z),
        path(Z, Y).

%?- path(a,X)
% X=b
% X=c
% X=a
% X=d
%TRUE path(a,a)
%TRUE path(a,b)
%TRUE path(a,c)
%TRUE path(a,d)
%FAIL path(a,z)

:- table(test_recursion/2).

test_recursion(1, finished) :- !.
test_recursion(N, X) :-
        write('test '), write(N), nl,
        N > 1,
        N1 is N - 1,
        test_recursion(N1, X).

%?- test_recursion(5, X)
%OUTPUT
%test 5
%test 4
%test 3
%test 2
%
%OUTPUT
% X=finished
%?- test_recursion(5, X)
% X=finished
%?- test_recursion(9, X)
%OUTPUT
%test 9
%test 8
%test 7
%test 6
%
%OUTPUT
% X=finished

:- table(test_error/1).

test_error(1) :- X is Y, !.
test_error(N) :-
        write('test_error '), write(N), nl,
        N > 1,
        N1 is N - 1,
        test_error(N1).

%?- test_error(5)
%OUTPUT
%test_error 5
%test_error 4
%test_error 3
%test_error 2
%
%OUTPUT
%ERROR Cannot get Numeric for term: Y of type: VARIABLE
%?- test_error(5)
%ERROR Cannot get Numeric for term: Y of type: VARIABLE
%?- test_error(9)
%OUTPUT
%test_error 9
%test_error 8
%test_error 7
%test_error 6
%
%OUTPUT
%ERROR Cannot get Numeric for term: Y of type: VARIABLE

:- table(test_remove_duplicates/2).

test_remove_duplicates(X,Y) :- (true;true), X=1, Y=4, writeln(a).
test_remove_duplicates(X,Y) :- X=8, Y=9, writeln(b).
test_remove_duplicates(X,Y) :- X=8, Y=9, writeln(c).
test_remove_duplicates(X,Y) :- (X=1;X=3), Y=2, writeln(d).

%?- test_remove_duplicates(X, Y)
%OUTPUT
%a
%a
%b
%c
%d
%d
%
%OUTPUT
% X=1
% Y=4
% X=8
% Y=9
% X=1
% Y=2
% X=3
% Y=2
%?- test_remove_duplicates(X, Y)
% X=1
% Y=4
% X=8
% Y=9
% X=1
% Y=2
% X=3
% Y=2
%?- test_remove_duplicates(1, Y)
%OUTPUT
%a
%a
%d
%
%OUTPUT
% Y=4
% Y=2

:- table(test_variables/4).

test_variables(p(X,Y,Z), a, b, c) :- write(1), nl.
test_variables(p(a,b,c), X, Y, Z) :- write(2), nl.

%?- test_variables(p(A,B,C), A, B, C)
%OUTPUT
%1
%2
%
%OUTPUT
% A=a
% B=b
% C=c
%?- test_variables(p(B,C,A), B, C, A)
% A=c
% B=a
% C=b
%?- test_variables(p(A,B,C), C, A, B)
%OUTPUT
%1
%2
%
%OUTPUT
% A=b
% B=c
% C=a
% A=a
% B=b
% C=c
%?- test_variables(A, B, C, D), A=p(a,_,_)
%OUTPUT
%1
%2
%
%OUTPUT
% A=p(a, _, _)
% B=a
% C=b
% D=c
% A=p(a, b, c)
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% D=UNINSTANTIATED VARIABLE
%?- test_variables(A, B, C, D)
% A=p(X, Y, Z)
% B=a
% C=b
% D=c
% A=p(a, b, c)
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% D=UNINSTANTIATED VARIABLE
 */
/**
 * <code>table/1</code> - defines a user defined predicate as using tabled execution.
 */
public final class Tabling extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      PredicateKey key = PredicateKey.createFromNameAndArity(arg);
      getPredicates().addUserDefinedPredicate(new TabledUserDefinedPredicateFactory(getKnowledgeBase(), key));
      return true;
   }
}