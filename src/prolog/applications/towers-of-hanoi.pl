hanoi(N) :- move(N,left,centre,right).

move(0,_,_,_) :- !.
move(N,A,B,C) :-
   M is N-1,
   move(M,A,C,B), inform(A,B), move(M,C,B,A).

inform(X,Y) :-
   write([move,a,disc,from,the,X,pole,to,the,Y,pole]),
   nl.

%QUERY hanoi(3)
%OUTPUT
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
%
%OUTPUT
%ANSWER/
%NO

%QUERY hanoi(5)
%OUTPUT
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,right,pole]
% [move,a,disc,from,the,centre,pole,to,the,right,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,right,pole,to,the,left,pole]
% [move,a,disc,from,the,right,pole,to,the,centre,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
%
%OUTPUT
%ANSWER/
%NO

%QUERY hanoi(X)
%ANSWER X = 0
%NO

%QUERY hanoi(x)
%ERROR Cannot find calculatable: x