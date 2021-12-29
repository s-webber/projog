p(a,a,a).

%FAIL p(b,a,a)
%FAIL p(a,b,a)
%FAIL p(a,a,b)
%FAIL p(X,a,b)
%FAIL p(X,b,a)
%FAIL p(a,X,b)
%FAIL p(b,X,a)
%FAIL p(a,b,X)
%FAIL p(b,a,X)

%?- p(X,Y,Z)
% X=a
% Y=a
% Z=a

%?- p(X,X,X)
% X=a

