p(a,a,a).

%FALSE p(b,a,a)
%FALSE p(a,b,a)
%FALSE p(a,a,b)
%FALSE p(X,a,b)
%FALSE p(X,b,a)
%FALSE p(a,X,b)
%FALSE p(b,X,a)
%FALSE p(a,b,X)
%FALSE p(b,a,X)

%QUERY p(X,Y,Z)
%ANSWER
% X=a
% Y=a
% Z=a
%ANSWER

%QUERY p(X,X,X)
%ANSWER X=a

