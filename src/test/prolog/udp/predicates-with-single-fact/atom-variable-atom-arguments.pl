p(a,X,c).

%FALSE p(a,X,a)
%FALSE p(c,X,c)
%FALSE p(x,X,z)
%FALSE p(x,y,z)
%FALSE p(a,c,a)
%FALSE p(c,a,c)

%TRUE p(a,a,c)
%TRUE p(a,c,c)
%TRUE p(a,b,c)
%TRUE p(a,z,c)

%QUERY p(A,B,C)
%ANSWER 
% A=a
% B=UNINSTANTIATED VARIABLE
% C=c
%ANSWER 

%QUERY p(a,Q,c)
%ANSWER Q=UNINSTANTIATED VARIABLE

%QUERY p(a,Q,c),W=[Q]
%ANSWER
% Q=UNINSTANTIATED VARIABLE
% W=[X]
%ANSWER

