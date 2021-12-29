x([q(X,Y)|Xs]).
x([X|Xs]) :- x(Xs).

y([q(X,Y)|Xs],[q(X1,Y1)|Xs]) :- Y > 0, Y1 is Y - 1.
y([X|Xs],[X|Xs1]) :- y(Xs,Xs1).

%?- x([a,b,q(a,b),c,d,e])
%YES
%NO
%FAIL x([a,b,t(a,b),c,d,e])

%?- y([a,b,q(a,5),c,d,e], [a,b,q(a,4),c,d,e])
%YES
%NO
%FAIL y([a,b,q(a,5),c,d,e], [a,b,q(a,6),c,d,e])
%FAIL y([a,b,q(a,0),c,d,e], [a,b,q(a,-1),c,d,e])
%?- y([a,b,q(a,5),c,d,e], [a,b,q(a,X),c,d,e])
% X=4
%NO
