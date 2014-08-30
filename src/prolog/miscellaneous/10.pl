% try to replace W,X,Y,Z and T variables with '_' - no longer works

qwerty1(L1, L2) :- L1=L2.
%QUERY qwerty1([a,B,c], [A,b(A,x),c])
%ANSWER
% A=a
% B=b(a, x)
%ANSWER

qwerty2(L1,L2) :- L1=[W,b(X,Y),Z], L1=L2.
%QUERY qwerty2([a,B,c], [A,b(A,x),c])
%ANSWER
% A=a
% B=b(a, x)
%ANSWER

qwerty3(L1,L2) :- L1=L2, L1=[W,b(X,Y),Z].
%QUERY qwerty3([a,B,c], [A,b(A,x),c])
%ANSWER
% A=a
% B=b(a, x)
%ANSWER

%QUERY L1=[a,B,c], L2=[A,b(A,x),c], L1=[_,b(T,x),_], L1=L2
%ANSWER
% A=a
% B=b(a, x)
% L1=[a,b(a, x),c]
% L2=[a,b(a, x),c]
% T=a
%ANSWER

qwerty4(P1,P2) :- P1=p(W,b(X,Y),Z), P1=P2.
%QUERY qwerty4(p(a,B,c), p(A,b(A,x),c))
%ANSWER
% A=a
% B=b(a, x)
%ANSWER

%QUERY P1=p(a,B,c), P2=p(A,b(A,x),c), P1=p(_,b(T,x),_), P1=P2
%ANSWER
% A=a
% B=b(a, x)
% P1=p(a, b(a, x), c)
% P2=p(a, b(a, x), c)
% T=a
%ANSWER

qwerty5(L1,L2) :- L1=[Q,b(X,Y),Z], L1=L2.
%QUERY qwerty5([a,B,c], [A,b(A,x),c])
%ANSWER
% A=a
% B=b(a, x)
%ANSWER