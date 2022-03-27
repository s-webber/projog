%FAIL X in 1..3, integer(X)

%?- X in 1..3, label([X]), integer(X)
% X=1
% X=2
% X=3
%NO

%?- X in 1..3, \+ integer(X), label([X]), integer(X)
% X=1
% X=2
% X=3
%NO

%?- X in 1..3, X=1, integer(X)
% X=1

%?- X in 1..3, 1=X, integer(X)
% X=1

%?- X in 1..3, X#=1, integer(X)
% X=1

%?- X in 1..3, 1#=X, integer(X)
% X=1

%?- X in 0..3, Y#=X+2, 1=X, integer(X), integer(Y)
% X=1
% Y=3

%?- X in 0..3, Y#=X+2, X=1, integer(X), integer(Y)
% X=1
% Y=3

%?- X in 0..3, Y#=X+2, X#=1, integer(X), integer(Y)
% X=1
% Y=3

%?- X in 0..3, Y#=X+2, 1=X, integer(X), integer(Y)
% X=1
% Y=3

%?- X in 0..3, Y#=X+2, 1#=X, integer(X), integer(Y)
% X=1
% Y=3
