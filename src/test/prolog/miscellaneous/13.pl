q(1).
q(2).
q(3).

x(X, Y) :- q(X), q(Y).

%?- x(Z,Z)
% Z=1
% Z=2
% Z=3

%?- x(A,B)
% A=1
% B=1
% A=1
% B=2
% A=1
% B=3
% A=2
% B=1
% A=2
% B=2
% A=2
% B=3
% A=3
% B=1
% A=3
% B=2
% A=3
% B=3

% test having anonymous variables ('_') in query arguments

%?- x(_,_)
%YES
%YES
%YES
%YES
%YES
%YES
%YES
%YES
%YES

qwerty :- X=Y, q(X), q(Y).
%?- qwerty
%YES
%YES
%YES
