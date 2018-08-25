x(q(1)).
x(q(2)).
x(q(3)).

qwerty(X,Y) :- X=Y, x(q(X)), x(q(Y)).
%QUERY qwerty(X,Y)
%ANSWER
% X=1
% Y=1
%ANSWER
%ANSWER
% X=2
% Y=2
%ANSWER
%ANSWER
% X=3
% Y=3
%ANSWER
