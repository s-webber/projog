x(q(1)).
x(q(2)).
x(q(3)).

qwerty(X,Y) :- X=Y, x(q(X)), x(q(Y)).
%?- qwerty(X,Y)
% X=1
% Y=1
% X=2
% Y=2
% X=3
% Y=3
