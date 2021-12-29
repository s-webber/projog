% same variable being used more than once in a rule
test1(X,X).
test1([a,b,c],[q,w,e]).

%?- test1([a,b,c],[X,Y,Z])
% X=a
% Y=b
% Z=c
% X=q
% Y=w
% Z=e
