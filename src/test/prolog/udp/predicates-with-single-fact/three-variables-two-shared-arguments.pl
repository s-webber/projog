p(X,Y,X).

%FAIL p(a,b,c)
%FAIL p(a,a,c)
%FAIL p(a,c,c)

%TRUE p(a,a,a)
%TRUE p(a,b,a)
%TRUE p(x,y,x)

%FAIL p(a,c,c)

%?- p(a,b,Q)
% Q=a

%?- p(Q,b,a)
% Q=a

%?- p(A,B,C)
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE

%?- p(A,B,C), A=7
% A=7
% B=UNINSTANTIATED VARIABLE
% C=7

%?- D=x(A,B,C), p(A,B,C)
% A=UNINSTANTIATED VARIABLE
% B=UNINSTANTIATED VARIABLE
% C=UNINSTANTIATED VARIABLE
% D=x(X, Y, X)

