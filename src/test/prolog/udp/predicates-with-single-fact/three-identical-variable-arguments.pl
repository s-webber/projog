p(X,X,X).

%FALSE p(b,a,a)
%FALSE p(a,b,a)
%FALSE p(a,a,b)

%TRUE p(a,a,a)
%TRUE p(b,b,b)

%FALSE p(a,a,b)

%QUERY p(p(X),Y,p(7))
%ANSWER
% X=7
% Y=p(7)
%ANSWER

%QUERY p(Y,p(X),p(7))
%ANSWER
% X=7
% Y=p(7)
%ANSWER

%QUERY p(Y,p(7),p(X))
%ANSWER
% X=7
% Y=p(7)
%ANSWER

%QUERY p(p(7),Y,p(X))
%ANSWER
% X=7
% Y=p(7)
%ANSWER

%QUERY p(p(7),p(X),Y)
%ANSWER
% X=7
% Y=p(7)
%ANSWER

%QUERY p(p(X),p(7),Y)
%ANSWER
% X=7
% Y=p(7)
%ANSWER

%QUERY D=x(A,B,C), p(A,B,C)
%ANSWER
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% D=x(X, X, X)
%ANSWER

