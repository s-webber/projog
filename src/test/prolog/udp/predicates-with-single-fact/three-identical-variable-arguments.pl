p(X,X,X).

%FAIL p(b,a,a)
%FAIL p(a,b,a)
%FAIL p(a,a,b)

%TRUE p(a,a,a)
%TRUE p(b,b,b)

%FAIL p(a,a,b)

%?- p(p(X),Y,p(7))
% X=7
% Y=p(7)

%?- p(Y,p(X),p(7))
% X=7
% Y=p(7)

%?- p(Y,p(7),p(X))
% X=7
% Y=p(7)

%?- p(p(7),Y,p(X))
% X=7
% Y=p(7)

%?- p(p(7),p(X),Y)
% X=7
% Y=p(7)

%?- p(p(X),p(7),Y)
% X=7
% Y=p(7)

%?- D=x(A,B,C), p(A,B,C)
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% D=x(X, X, X)

