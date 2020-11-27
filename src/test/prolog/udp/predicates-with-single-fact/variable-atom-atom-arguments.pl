p(X,b,c).

%FALSE p(X,b,b)
%FALSE p(X,c,c)
%FALSE p(X,y,z)
%FALSE p(x,y,z)
%FALSE p(b,c,b)
%FALSE p(c,b,b)

%TRUE p(a,b,c)
%TRUE p(z,b,c)

%QUERY p(A,B,C)
%ANSWER 
% A=UNINSTANTIATED VARIABLE
% B=b
% C=c
%ANSWER 

%QUERY p(Q,b,c)
%ANSWER Q=UNINSTANTIATED VARIABLE

%QUERY p(Q,b,c),W=[Q]
%ANSWER
% Q=UNINSTANTIATED VARIABLE
% W=[X]
%ANSWER

