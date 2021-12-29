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

%FAIL t1(Z,p(Z))
%?- t1(W,p(Z))
% W=2
% Z=1
% W=2
% Z=3

%FAIL t2(Z,Z)
%?- t2(W,Z)
% W=2
% Z=1
% W=2
% Z=3

%FAIL t3(Z,Z)
%?- t3(W,Z)
% W=2
% Z=1
% W=2
% Z=3

%FAIL t4(p(Z),Z)
%?- t4(p(Z),W)
% W=2
% Z=1
% W=2
% Z=3

%FAIL t5(Z,Z)
%?- t5(Z,W)
% W=2
% Z=1
% W=2
% Z=3

%FAIL t6(Z,Z)
%?- t6(Z,W)
% W=2
% Z=1
% W=2
% Z=3

%FAIL t7(p(Z),Z)
%?- t7(p(Z),W)
% W=2
% Z=1
