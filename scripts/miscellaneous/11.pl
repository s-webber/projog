x(p(1)).
x(p(3)).

z(1).
z(3).

t1(X,Y) :- X is 2, x(Y).

t2(X,Y) :- X is 2, x(p(Y)).

t3(X,Y) :- X is 2, z(Y).

t4(Y,X) :- X is 2, x(Y).

t5(Y,X) :- X is 2, x(p(Y)).

t6(Y,X) :- X is 2, z(Y).

t7(Y,X) :- X is 2, Y=p(1).

%FALSE t1(Z,p(Z))
%QUERY t1(W,p(Z))
%ANSWER
% W=2
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=3
%ANSWER

%FALSE t2(Z,Z)
%QUERY t2(W,Z)
%ANSWER
% W=2
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=3
%ANSWER

%FALSE t3(Z,Z)
%QUERY t3(W,Z)
%ANSWER
% W=2
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=3
%ANSWER

%FALSE t4(p(Z),Z)
%QUERY t4(p(Z),W)
%ANSWER
% W=2
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=3
%ANSWER

%FALSE t5(Z,Z)
%QUERY t5(Z,W)
%ANSWER
% W=2
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=3
%ANSWER

%FALSE t6(Z,Z)
%QUERY t6(Z,W)
%ANSWER
% W=2
% Z=1
%ANSWER
%ANSWER
% W=2
% Z=3
%ANSWER

%FALSE t7(p(Z),Z)
%QUERY t7(p(Z),W)
%ANSWER
% W=2
% Z=1
%ANSWER
