p(X,b,c).

%FAIL p(X,b,b)
%FAIL p(X,c,c)
%FAIL p(X,y,z)
%FAIL p(x,y,z)
%FAIL p(b,c,b)
%FAIL p(c,b,b)

%TRUE p(a,b,c)
%TRUE p(z,b,c)

%?- p(A,B,C)
% A=UNINSTANTIATED VARIABLE
% B=b
% C=c

%?- p(Q,b,c)
% Q=UNINSTANTIATED VARIABLE

%?- p(Q,b,c),W=[Q]
% Q=UNINSTANTIATED VARIABLE
% W=[X]

