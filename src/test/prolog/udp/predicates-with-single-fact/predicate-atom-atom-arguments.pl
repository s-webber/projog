p(p(a),b,a).

%FAIL p(p(X),X,a)
%FAIL p(p(X),X,X)

%?- p(p(X),b,X)
% X=a

%?- p(p(X),Y,X)
% X=a
% Y=b

