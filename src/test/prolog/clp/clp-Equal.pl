%?- [X,Y] ins 1..3, X=Y
% X=1..3
% Y=1..3

%?- [X,Y] ins 1..3, X=Y, Y=2
% X=2
% Y=2

%FAIL [X,Y] ins 1..3, X=Y, Y=4

%?- X in 1..4, Y in 2..5, X=Y
% X=2..4
% Y=2..4

%FAIL X in 1..4, Y in 2..5, X=Y, X#=1

%?- X#=2, Y in 1..3, X=Y
% X=2
% Y=2

%?- X in 12..18, Y in 13..22, X#\=14, Y#\=16, X=Y
% X={13, 15, 17, 18}
% Y={13, 15, 17, 18}

%?- X in 1..4, Y=2, X=Y
% X=2
% Y=2

%FAIL X in 1..4, Y=5, X=Y

%FAIL X in 1..3, Y in 4..6, X=Y

%?- X in 1..4, f(X)=f(X)
% X=1..4

%?- X in 1..4, Y in 2..5, f(X)=f(Y)
% X=2..4
% Y=2..4
%FAIL X in 1..4, Y in 5..8, f(X)=f(Y)

%?- X in 1..5, Y in 4..9, f(X)=f(4), X=Y
% X=4
% Y=4
%?- X in 1..4, Y in 4..7, f(X)=f(2), f(5)=f(Y)
% X=2
% Y=5
%FAIL X in 1..4, f(X)=f(0)
%FAIL X in 1..4, f(5)=f(X)
