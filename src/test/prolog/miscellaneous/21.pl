test1(X, Y) :- X = Y.

%?- test1(X,X)
% X=UNINSTANTIATED VARIABLE

%?- test1(X,Y)
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE

%?- test1(X,1)
% X=1

%?- test1(a,X)
% X=a

p(1).
p(2).
p(3).

test2(X, Y) :- p(X), p(Y).

%?- test2(X,Y)
% X=1
% Y=1
% X=1
% Y=2
% X=1
% Y=3
% X=2
% Y=1
% X=2
% Y=2
% X=2
% Y=3
% X=3
% Y=1
% X=3
% Y=2
% X=3
% Y=3

%?- test2(X,X)
% X=1
% X=2
% X=3

%?- test2(1,Y)
% Y=1
% Y=2
% Y=3

%TRUE test2(1,3)

test3(X, X) :- p(X), p(X).

%?- test3(X,Y)
% X=1
% Y=1
% X=2
% Y=2
% X=3
% Y=3

%?- test3(1,Y)
% Y=1

%TRUE test3(3,3)
%FAIL test3(1,3)

test4(X, Y) :- test1(X, Y), Y=1.
%TRUE test4(1,1)
%FAIL test4(2,2)
%?- test4(1,Y)
% Y=1
%?- test4(Y,1)
% Y=1
%?- test4(X,Y)
% X=1
% Y=1

test5(X, Y) :- test1(X, Y), X=1.
%TRUE test5(1,1)
%FAIL test5(2,2)
%?- test5(1,Y)
% Y=1
%?- test5(Y,1)
% Y=1
%?- test5(X,Y)
% X=1
% Y=1

