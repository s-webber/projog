y(X,X).

x([a,b,c]).
x([q,w,e]).
x([x,y,x]).

p(X) :- x(Y), y(X,Y).

%QUERY p([A,B,e])
%ANSWER
% A=q
% B=w
%ANSWER
%NO

%QUERY  p([A,B,C])
%ANSWER
% A=a
% B=b
% C=c
%ANSWER
%ANSWER
% A=q
% B=w
% C=e
%ANSWER
%ANSWER
% A=x
% B=y
% C=x
%ANSWER


