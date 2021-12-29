p(p(p(1))).
p(p(p(2))).
p(p(p(3))).

test1(X,Y) :- X=A, B=Y, Y=3, C=p(A), p(C).
test1(X,Y) :- X=A, B=Y, C=p(A), Y=2, p(C).

%?- test1(p(X), X)
% X=3
% X=2

p2(I) :- I=I2, p(p(p(I2))), 1 is I mod 2.

test2(V, W, X, Y, Z) :- A=p(X), p(Y)=B, p(A)=C, D=p(B), p(C), p(D), p(Z)=C, p2(T), W is X, C=D, V is T*T.

test3(X,Y) :- A=Y, Y=B, test2(X, A, Y, B, p(B)).
%?- test3(X, Y)
% X=1
% Y=1
% X=9
% Y=1
% X=1
% Y=2
% X=9
% Y=2
% X=1
% Y=3
% X=9
% Y=3
