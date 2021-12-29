p(a,b,X).

%FAIL p(a,a,X)
%FAIL p(b,b,X)
%FAIL p(x,y,X)
%FAIL p(x,y,z)
%FAIL p(a,a,c)
%FAIL p(b,b,c)

%TRUE p(a,b,c)
%TRUE p(a,b,b)
%TRUE p(a,b,a)
%TRUE p(a,b,z)

%?- p(A,B,C)
% A=a
% B=b
% C=UNINSTANTIATED VARIABLE

%?- p(a,b,Q)
% Q=UNINSTANTIATED VARIABLE

%?- p(a,b,Q),W=[Q]
% Q=UNINSTANTIATED VARIABLE
% W=[X]

