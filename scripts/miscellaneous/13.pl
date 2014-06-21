q(1).
q(2).
q(3).

x(X, Y) :- q(X), q(Y).

%QUERY x(Z,Z)
%ANSWER Z=1
%ANSWER Z=2
%ANSWER Z=3

%QUERY x(A,B)
%ANSWER
% A=1
% B=1
%ANSWER
%ANSWER
% A=1
% B=2
%ANSWER
%ANSWER
% A=1
% B=3
%ANSWER
%ANSWER
% A=2
% B=1
%ANSWER
%ANSWER
% A=2
% B=2
%ANSWER
%ANSWER
% A=2
% B=3
%ANSWER
%ANSWER
% A=3
% B=1
%ANSWER
%ANSWER
% A=3
% B=2
%ANSWER
%ANSWER
% A=3
% B=3
%ANSWER

% test having anonymous variables ('_') in query arguments

%QUERY x(_,_)
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/

qwerty :- X=Y, q(X), q(Y).
%QUERY qwerty
%ANSWER/
%ANSWER/
%ANSWER/