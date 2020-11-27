p(p(a),b,a).

%FALSE p(p(X),X,a)
%FALSE p(p(X),X,X)

%QUERY p(p(X),b,X)
%ANSWER X=a

%QUERY p(p(X),Y,X)
%ANSWER
% X=a
% Y=b
%ANSWER

