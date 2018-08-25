% same variable being used more than once in a rule
test1(X,X).
test1([a,b,c],[q,w,e]).

%QUERY test1([a,b,c],[X,Y,Z])
%ANSWER
% X=a
% Y=b
% Z=c
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
