concatenate([],L,L).
concatenate([X|L1],L2,[X|L3]) :- concatenate(L1,L2,L3).

data([a,b,c]).
data([x,y,z]).
data([1,2,3]).

test1(Q) :- data(X), concatenate([q,w,e,r,t,y],X,Q).

test2(Q) :- data(X), data(D), concatenate(D,X,Q).

%QUERY test1(Q)
%ANSWER Q=[q,w,e,r,t,y,a,b,c]
%ANSWER Q=[q,w,e,r,t,y,x,y,z]
%ANSWER Q=[q,w,e,r,t,y,1,2,3]

%QUERY test2(Q)
%ANSWER Q = [a,b,c,a,b,c]
%ANSWER Q = [x,y,z,a,b,c]
%ANSWER Q = [1,2,3,a,b,c]
%ANSWER Q = [a,b,c,x,y,z]
%ANSWER Q = [x,y,z,x,y,z]
%ANSWER Q = [1,2,3,x,y,z]
%ANSWER Q = [a,b,c,1,2,3]
%ANSWER Q = [x,y,z,1,2,3]
%ANSWER Q = [1,2,3,1,2,3]
