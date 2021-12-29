x1(W,Z) :- W is 1, Z is 2.

%FAIL x1(X,X)
%?- x1(X,Y)
% X=1
% Y=2

x2(W,Z) :- W is 1, Z == 1.

%?- x2(X,X)
% X=1
%FAIL x2(X,Y)

x3(X, Y) :- X = Y, x1(X,Y).
%FAIL x3(X,X)
%FAIL x3(X,Y)

x4(X, Y) :- X = Y, x2(X,Y).
%?- x4(X,Y)
% X=1
% Y=1
%?- x4(X,X)
% X=1

%?- X=Y, x2(X,Y)
% X=1
% Y=1

%?- Y=X, x2(X,Y)
% X=1
% Y=1

q(1).
q(2).
q(3).

x5(X, Y) :- X = Y, q(X).

%?- x5(X,Y)
% X=1
% Y=1
% X=2
% Y=2
% X=3
% Y=3

%?- x5(X,X)
% X=1
% X=2
% X=3

x6(X, Y) :- X = Y, q(Y).

%?- x6(X,Y)
% X=1
% Y=1
% X=2
% Y=2
% X=3
% Y=3

%?- x6(X,X)
% X=1
% X=2
% X=3
