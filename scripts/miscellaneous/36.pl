x(X,Y,Z) :- Z is X mod Y.
x(X,Y,Z) :- Z is X rem Y.

%QUERY x(25, 7, X)
%ANSWER X = 4
%ANSWER X = 4

%QUERY x(25, -7, X)
%ANSWER X = -3
%ANSWER X = 4

%QUERY x(-25, 7, X)
%ANSWER X = 3
%ANSWER X = -4

%QUERY x(-25, -7, X)
%ANSWER X = -4
%ANSWER X = -4