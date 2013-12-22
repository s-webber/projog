% Towers of Hanoi
%
% The Towers of Hanoi is a game played with three poles and a number of discs of different sizes which can slide onto any pole.
% The game starts with all the discs stacked in ascending order of size on one pole, the smallest at the top.
% The aim of the game is to move the entire stack to another pole, obeying the following rules:
% - Only one disc may be moved at a time.
% - Each move consists of taking the upper disc from one of the poles and sliding it onto another pole.
% - No disc may be placed on top of a smaller one.

hanoi(N) :- move(N,left,centre,right).

move(0,_,_,_) :- !.
move(N,A,B,C) :-
   M is N-1,
   move(M,A,C,B), inform(A,B), move(M,C,B,A).

inform(X,Y) :-
   write([move,a,disc,from,the,X,pole,to,the,Y,pole]),
   nl.
