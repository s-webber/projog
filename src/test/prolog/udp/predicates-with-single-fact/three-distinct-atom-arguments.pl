p(a,b,c).

%FALSE p(a,c,b)
%FALSE p(b,a,c)
%FALSE p(b,c,a)
%FALSE p(c,a,b)
%FALSE p(c,b,a)
%FALSE p(a,a,a)
%FALSE p(b,b,b)
%FALSE p(c,c,c)
%FALSE p(x,b,c)
%FALSE p(a,x,c)
%FALSE p(a,b,x)
%FALSE p(x,x,x)
%FALSE p(x,y,z)
%FALSE p(X,X,c)
%FALSE p(X,b,X)
%FALSE p(a,X,X)
%FALSE p(X,X,X)
%FALSE p(Y,X,X)
%FALSE p(X,Y,X)
%FALSE p(X,X,Y)
%FALSE p(a,b)
%FALSE p(a,b,c,d)

%TRUE p(a,b,c)

%QUERY p(X,b,c)
%ANSWER X=a

%QUERY p(a,X,c)
%ANSWER X=b

%QUERY p(a,b,X)
%ANSWER X=c

%QUERY p(X,Y,c)
%ANSWER
% X=a
% Y=b
%ANSWER

%QUERY p(X,b,Y)
%ANSWER
% X=a
% Y=c
%ANSWER

%QUERY p(a,X,Y)
%ANSWER
% X=b
% Y=c
%ANSWER

%QUERY p(X,Y,Z)
%ANSWER
% X=a
% Y=b
% Z=c
%ANSWER

