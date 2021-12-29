p(a,X,c).

%FAIL p(a,X,a)
%FAIL p(c,X,c)
%FAIL p(x,X,z)
%FAIL p(x,y,z)
%FAIL p(a,c,a)
%FAIL p(c,a,c)

%TRUE p(a,a,c)
%TRUE p(a,c,c)
%TRUE p(a,b,c)
%TRUE p(a,z,c)

%?- p(A,B,C)
% A=a
% B=UNINSTANTIATED VARIABLE
% C=c

%?- p(a,Q,c)
% Q=UNINSTANTIATED VARIABLE

%?- p(a,Q,c),W=[Q]
% Q=UNINSTANTIATED VARIABLE
% W=[X]

