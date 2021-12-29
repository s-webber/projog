hanoi(N) :- move(N,left,centre,right).

move(0,_,_,_) :- !.
move(N,A,B,C) :-
  M is N-1,
  move(M,A,C,B), inform(A,B), move(M,C,B,A).

inform(X,Y) :-
  write([move,a,disc,from,the,X,pole,to,the,Y,pole]),
  nl.

%?- hanoi(3)
%OUTPUT
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%
%OUTPUT
%YES

%?- hanoi(5)
%OUTPUT
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,right,pole]
%[move,a,disc,from,the,centre,pole,to,the,right,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,right,pole,to,the,left,pole]
%[move,a,disc,from,the,right,pole,to,the,centre,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%
%OUTPUT
%YES

%?- hanoi(X)
% X=0

%?- hanoi(x)
%ERROR Cannot find arithmetic operator: x/0
