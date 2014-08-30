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

%FALSE test1(1,2)
%FALSE test1(W,5)

%FALSE test2(1,2)
%FALSE test2(W,5)

%FALSE test3(1,2)
%FALSE test3(W,5)

%FALSE test4(1,2)
%FALSE test4(W,5)

%FALSE test5(1,2)
%FALSE test5(W,5)

%FALSE test6(1,2)
%FALSE test6(W,5)

%QUERY test1(W,1)
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%NO

%QUERY test1(2,W)
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%NO

%QUERY test2(W,1)
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%NO

%QUERY test2(2,W)
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%NO

%QUERY test3(W,1)
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%NO

%QUERY test3(2,W)
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%NO

%QUERY test4(W,1)
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%NO

%QUERY test4(2,W)
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%NO

%QUERY test5(W,1)
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%NO

%QUERY test5(2,W)
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%NO

%QUERY test6(W,1)
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%ANSWER W=1
%NO

%QUERY test6(2,W)
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%ANSWER W=2
%NO

%QUERY test1(W,Z)
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER

%QUERY test2(W,Z)
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER

%QUERY test3(W,Z)
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER

%QUERY test4(W,Z)
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER

%QUERY test5(W,Z)
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER

%QUERY test6(W,Z)
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER
%ANSWER
% W=1
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=2
%ANSWER
%ANSWER
% W=3
% Z=3
%ANSWER