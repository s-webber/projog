% example 1
x(X) :- e(Y), X is Y+4.

e(0).
e(X) :- o(Y), X is Y+1, X>0.

o(1).
o(X) :- x(Y), X is Y+1, X>1.

% example 2
e2(X) :- repeat(3), fail.
e2(X) :- e1(X).
e1(0).
e1(X) :- o3(Y), X is Y+1, X>0.

o3(X) :- o2(X).
o2(X) :- o1(X).
o1(1).
o1(X) :- e2(Y), X is Y+1, X>1.

%QUERY e(X), write(X), nl, X>20, !
%OUTPUT
%0
%2
%6
%8
%12
%14
%18
%20
%24
%
%OUTPUT
%ANSWER X=24
%NO

%QUERY o(X), write(X), nl, X>20, !
%OUTPUT
%1
%5
%7
%11
%13
%17
%19
%23
%
%OUTPUT
%ANSWER X=23
%NO

%QUERY e1(X), write(X), nl, X>10, !
%OUTPUT
%0
%2
%4
%6
%8
%10
%12
%
%OUTPUT
%ANSWER X=12
%NO

%QUERY o1(X), write(X), nl, X>10, !
%OUTPUT
%1
%3
%5
%7
%9
%11
%
%OUTPUT
%ANSWER X=11
%NO
