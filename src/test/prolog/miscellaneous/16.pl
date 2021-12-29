p(1).
p(I) :- I is 1*1+1/1.
p(3).

test1(X, Y) :- X = A, B = Y, p(X), p(Y), X=Y.
test1(X, Y) :- X = A, B = Y, p(A), p(B), X=Y.
test1(X, Y) :- X = A, B = Y, p(X), p(B), X=B.
test1(X, Y) :- X = A, B = Y, p(X), p(B), B=A.

test2(X, Y) :- X = A, p(X), B = Y, p(Y), X=Y.
test2(X, Y) :- X = A, p(A), B = Y, p(B), X=Y.
test2(X, Y) :- X = A, p(X), B = Y, p(B), X=B.
test2(X, Y) :- X = A, p(X), B = Y, p(B), B=A.

test3(X, Y) :- x(a(b(X,2,[a,b|B]))) = x(a(b(A,2,[a,b|Y]))), p(X), p(Y), X=Y, p(X), p(Y), p(A), p(B).
test3(X, Y) :- x(a(b(X,2,[a,b|B]))) = x(a(b(A,2,[a,b|Y]))), p(A), p(B), X=Y, p(A), p(B), p(X), p(Y).
test3(X, Y) :- x(a(b(X,2,[a,b|B]))) = x(a(b(A,2,[a,b|Y]))), p(X), p(B), X=B, p(A), p(X), p(B), p(Y).
test3(X, Y) :- x(a(b(X,2,[a,b|B]))) = x(a(b(A,2,[a,b|Y]))), p(X), p(B), B=A, p(Y), p(B), p(X), p(A).

test4(X, Y) :- X = A, B = Y, X=Y, p(X), p(Y).
test4(X, Y) :- X = A, B = Y, X=Y, p(A), p(B).
test4(X, Y) :- X = A, B = Y, X=B, p(X), p(B).
test4(X, Y) :- X = A, B = Y, B=A, p(X), p(B).

test5(X, Y) :- X=Y, X = A, B = Y, p(X), p(Y).
test5(X, Y) :- X=Y, X = A, B = Y, p(A), p(B).
test5(X, Y) :- X=B, X = A, B = Y, p(X), p(B).
test5(X, Y) :- B=A, X = A, B = Y, p(X), p(B).

test6(X, Y) :- p(X), p(Y), X=Y, X = A, B = Y.
test6(X, Y) :- p(A), p(B), X=Y, X = A, B = Y.
test6(X, Y) :- p(X), p(B), X=B, X = A, B = Y.
test6(X, Y) :- p(X), p(B), B=A, X = A, B = Y.

%FAIL test1(1,2)
%FAIL test1(W,5)

%FAIL test2(1,2)
%FAIL test2(W,5)

%FAIL test3(1,2)
%FAIL test3(W,5)

%FAIL test4(1,2)
%FAIL test4(W,5)

%FAIL test5(1,2)
%FAIL test5(W,5)

%FAIL test6(1,2)
%FAIL test6(W,5)

%?- test1(W,1)
% W=1
% W=1
% W=1
% W=1
%NO

%?- test1(2,W)
% W=2
% W=2
% W=2
% W=2
%NO

%?- test2(W,1)
% W=1
% W=1
% W=1
% W=1
%NO

%?- test2(2,W)
% W=2
% W=2
% W=2
% W=2
%NO

%?- test3(W,1)
% W=1
% W=1
% W=1
% W=1
%NO

%?- test3(2,W)
% W=2
% W=2
% W=2
% W=2
%NO

%?- test4(W,1)
% W=1
% W=1
% W=1
% W=1
%NO

%?- test4(2,W)
% W=2
% W=2
% W=2
% W=2
%NO

%?- test5(W,1)
% W=1
% W=1
% W=1
% W=1
%NO

%?- test5(2,W)
% W=2
% W=2
% W=2
% W=2
%NO

%?- test6(W,1)
% W=1
% W=1
% W=1
% W=1
%NO

%?- test6(2,W)
% W=2
% W=2
% W=2
% W=2
%NO

%?- test1(W,Z)
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3

%?- test2(W,Z)
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3

%?- test3(W,Z)
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3

%?- test4(W,Z)
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3

%?- test5(W,Z)
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3

%?- test6(W,Z)
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
% W=1
% Z=1
% W=2
% Z=2
% W=3
% Z=3
