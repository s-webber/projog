% test recursive functions that contain cut (!) #88

x(X,Y,Z) :- Z is X+Y.
x(X,5,Z) :- x(X, 7, Z), Z<9.
x(X,Y,Z) :- Y<8, Q=Y+1, x(X,Q,Z).

y(X,Y,Z) :- Z is X+Y.
y(X,5,Z) :- !, y(X, 7, Z), Z<9.
y(X,Y,Z) :- Y<8, Q=Y+1, y(X,Q,Z).

%?- x(1,5,Z)
% Z=6
% Z=8
% Z=7
% Z=8
% Z=9
%NO

%?- y(1,5,Z)
% Z=6
% Z=8
%NO
