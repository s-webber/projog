concatenate([],L,L).
concatenate([X|L1],L2,[X|L3]) :- concatenate(L1,L2,L3).

data([a,b,c]).
data([x,y,z]).
data([1,2,3]).

test1(Q) :- data(X), concatenate([q,w,e,r,t,y],X,Q).

test2(Q) :- data(X), data(D), concatenate(D,X,Q).

% TODO remove cut from both of these queries and confirm still determines if re-evaluation should succeed

%?- test1(Q), !
% Q=[q,w,e,r,t,y,a,b,c]
%NO

%?- test2(Q), !
% Q=[a,b,c,a,b,c]
%NO
