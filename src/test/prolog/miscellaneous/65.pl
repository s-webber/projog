a(1).
a(2).
a(3).

b(X, Y, Z) :- Z is X+Y.
b(X, Y, Z) :- Z is X-Y.
b(X, Y, Z) :- Z is X*Y.

c(A,B,C) :- a(A), a(B), b(A,B,C).

test(A,B,C) :- A=5; c(A,B,C).

%TRUE test(2,3,6)

%?- test(5,B,C)
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
%NO

%?- test(2,3,X)
% X=5
% X=-1
% X=6

%?- test(A,B,6)
% A=5
% B=UNINSTANTIATED VARIABLE
% A=2
% B=3
% A=3
% B=2
% A=3
% B=3
%NO

%?- test(A,B,C)
% A=5
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% A=1
% B=1
% C=2
% A=1
% B=1
% C=0
% A=1
% B=1
% C=1
% A=1
% B=2
% C=3
% A=1
% B=2
% C=-1
% A=1
% B=2
% C=2
% A=1
% B=3
% C=4
% A=1
% B=3
% C=-2
% A=1
% B=3
% C=3
% A=2
% B=1
% C=3
% A=2
% B=1
% C=1
% A=2
% B=1
% C=2
% A=2
% B=2
% C=4
% A=2
% B=2
% C=0
% A=2
% B=2
% C=4
% A=2
% B=3
% C=5
% A=2
% B=3
% C=-1
% A=2
% B=3
% C=6
% A=3
% B=1
% C=4
% A=3
% B=1
% C=2
% A=3
% B=1
% C=3
% A=3
% B=2
% C=5
% A=3
% B=2
% C=1
% A=3
% B=2
% C=6
% A=3
% B=3
% C=6
% A=3
% B=3
% C=0
% A=3
% B=3
% C=9
