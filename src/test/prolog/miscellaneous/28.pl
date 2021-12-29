n(1).
n(2).
n(3).

testE(X) :- n(C), X is C+1, X>2.

%?- testE(X)
% X=3
% X=4

n(1,2,3).
n(a,b,c).
n(x,y,x).

testM1(A,B,C,E) :- n(X,Y,Z), n(A,B,C), n(X,b,H), n(V,T,V), E=V.
testM2(A,B,C,E) :- n(X,Y,Z), n(A,B,C), n(X,b,C), n(V,T,V), E=V.

%?- testM1(A,B,C,E)
% A=1
% B=2
% C=3
% E=x
% A=a
% B=b
% C=c
% E=x
% A=x
% B=y
% C=x
% E=x
%NO

%?- testM2(A,B,C,E)
% A=a
% B=b
% C=c
% E=x
%NO
