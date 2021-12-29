p(a,b,c).

%FAIL p(a,c,b)
%FAIL p(b,a,c)
%FAIL p(b,c,a)
%FAIL p(c,a,b)
%FAIL p(c,b,a)
%FAIL p(a,a,a)
%FAIL p(b,b,b)
%FAIL p(c,c,c)
%FAIL p(x,b,c)
%FAIL p(a,x,c)
%FAIL p(a,b,x)
%FAIL p(x,x,x)
%FAIL p(x,y,z)
%FAIL p(X,X,c)
%FAIL p(X,b,X)
%FAIL p(a,X,X)
%FAIL p(X,X,X)
%FAIL p(Y,X,X)
%FAIL p(X,Y,X)
%FAIL p(X,X,Y)
%FAIL p(a,b)
%FAIL p(a,b,c,d)

%TRUE p(a,b,c)

%?- p(X,b,c)
% X=a

%?- p(a,X,c)
% X=b

%?- p(a,b,X)
% X=c

%?- p(X,Y,c)
% X=a
% Y=b

%?- p(X,b,Y)
% X=a
% Y=c

%?- p(a,X,Y)
% X=b
% Y=c

%?- p(X,Y,Z)
% X=a
% Y=b
% Z=c

