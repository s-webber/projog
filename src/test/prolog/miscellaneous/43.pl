x(L,L).
x([X|Xs],Ys) :- x(Xs,Ys).

%?- x([a,b,c], X)
% X=[a,b,c]
% X=[b,c]
% X=[c]
% X=[]
%NO

%FAIL x([a,b,c], [a,c])

%?- x(a, X)
% X=a
%NO
