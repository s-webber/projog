y(X,X).

x([a,b,c]).
x([q,w,e]).
x([x,y,x]).

p(X) :- x(Y), y(X,Y).

%?- p([A,B,e])
% A=q
% B=w
%NO

%?-  p([A,B,C])
% A=a
% B=b
% C=c
% A=q
% B=w
% C=e
% A=x
% B=y
% C=x


