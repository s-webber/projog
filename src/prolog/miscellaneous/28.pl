n(1).
n(2).
n(3).

testE(X) :- n(C), X is C+1, X>2.

%QUERY testE(X)
%ANSWER X=3
%ANSWER X=4

n(1,2,3).
n(a,b,c).
n(x,y,x).

testM1(A,B,C,E) :- n(X,Y,Z), n(A,B,C), n(X,b,H), n(V,T,V), E=V.
testM2(A,B,C,E) :- n(X,Y,Z), n(A,B,C), n(X,b,C), n(V,T,V), E=V.

%QUERY testM1(A,B,C,E)
%ANSWER
% A=1
% B=2
% C=3
% E=x
%ANSWER
%ANSWER
% A=a
% B=b
% C=c
% E=x
%ANSWER
%ANSWER
% A=x
% B=y
% C=x
% E=x
%ANSWER
%NO

%QUERY testM2(A,B,C,E)
%ANSWER
% A=a
% B=b
% C=c
% E=x
%ANSWER
%NO