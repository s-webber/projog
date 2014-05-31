test3_1(X,X).

test3_2([a,b,c]).
test3_2([q,w,e]).
test3_2([x,y,z]).

test3_3(X) :- test3_2(Y), test3_1(X,Y).

%QUERY test3_3(X)
%ANSWER X=[a,b,c]
%ANSWER X=[q,w,e]
%ANSWER X=[x,y,z]

%TRUE_NO test3_3([a,b,c])
%TRUE_NO test3_3([q,w,e])
%TRUE test3_3([x,y,z])
%QUERY test3_3([X,y,z])
%ANSWER X=x
%FALSE test3_3([X,y,X])
