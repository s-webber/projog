p(a,b,X).

%FALSE p(a,a,X)
%FALSE p(b,b,X)
%FALSE p(x,y,X)
%FALSE p(x,y,z)
%FALSE p(a,a,c)
%FALSE p(b,b,c)

%TRUE p(a,b,c)
%TRUE p(a,b,b)
%TRUE p(a,b,a)
%TRUE p(a,b,z)

%QUERY p(A,B,C)
%ANSWER 
% A=a
% B=b
% C=UNINSTANTIATED VARIABLE
%ANSWER 

%QUERY p(a,b,Q)
%ANSWER Q=UNINSTANTIATED VARIABLE

%QUERY p(a,b,Q),W=[Q]
%ANSWER
% Q=UNINSTANTIATED VARIABLE
% W=[X]
%ANSWER

