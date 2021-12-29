test2_1(a).
test2_1(b).
test2_1(c).

test2_2(X, X) :- test2_1(_).
%FAIL test2_2(x,y)
%?- test2_2(X,X)
% X=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE
% X=UNINSTANTIATED VARIABLE

%?- test2_2(x,x)
%YES
%YES
%YES

%?- test2_2(x,X)
% X=x
% X=x
% X=x

%?- test2_2(X,y)
% X=y
% X=y
% X=y
