test3_1(X,X).

test3_2([a,b,c]).
test3_2([q,w,e]).
test3_2([x,y,z]).

test3_3(X) :- test3_2(Y), test3_1(X,Y).

%?- test3_3(X)
% X=[a,b,c]
% X=[q,w,e]
% X=[x,y,z]

%TRUE_NO test3_3([a,b,c])
%TRUE_NO test3_3([q,w,e])
%TRUE test3_3([x,y,z])
%?- test3_3([X,y,z])
% X=x
%FAIL test3_3([X,y,X])
