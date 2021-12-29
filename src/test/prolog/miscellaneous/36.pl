x(X,Y,Z) :- Z is X mod Y.
x(X,Y,Z) :- Z is X rem Y.

%?- x(25, 7, X)
% X=4
% X=4

%?- x(25, -7, X)
% X=-3
% X=4

%?- x(-25, 7, X)
% X=3
% X=-4

%?- x(-25, -7, X)
% X=-4
% X=-4
