%?- [Y,Z] ins 1..3, X in 1..2, Y#=X+2, all_different([X,Y,Z])
% X = 1
% Y = 3
% Z = 2

%?- [Y,Z] ins 1..3, X in 1..2, all_different([X,Y,Z]), Y#=X+2
% X = 1
% Y = 3
% Z = 2

%?- [Y,Z] ins 1..3, Y#=X+2, X in 1..2, all_different([X,Y,Z])
% X = 1
% Y = 3
% Z = 2

%?- [Y,Z] ins 1..3, Y#=X+2, all_different([X,Y,Z]), X in 1..2
% X = 1
% Y = 3
% Z = 2

%?- [Y,Z] ins 1..3, all_different([X,Y,Z]), Y#=X+2, X in 1..2
% X = 1
% Y = 3
% Z = 2

%?- [Y,Z] ins 1..3, all_different([X,Y,Z]), X in 1..2, Y#=X+2
% X = 1
% Y = 3
% Z = 2

%?- X in 1..2, [Y,Z] ins 1..3, Y#=X+2, all_different([X,Y,Z])
% X = 1
% Y = 3
% Z = 2

%?- X in 1..2, [Y,Z] ins 1..3, all_different([X,Y,Z]), Y#=X+2
% X = 1
% Y = 3
% Z = 2

%?- X in 1..2, Y#=X+2, [Y,Z] ins 1..3, all_different([X,Y,Z])
% X = 1
% Y = 3
% Z = 2

%?- X in 1..2, Y#=X+2, all_different([X,Y,Z]), [Y,Z] ins 1..3
% X = 1
% Y = 3
% Z = 2

%?- X in 1..2, all_different([X,Y,Z]), Y#=X+2, [Y,Z] ins 1..3
% X = 1
% Y = 3
% Z = 2

%?- X in 1..2, all_different([X,Y,Z]), [Y,Z] ins 1..3, Y#=X+2
% X = 1
% Y = 3
% Z = 2

%?- Y#=X+2, X in 1..2, [Y,Z] ins 1..3, all_different([X,Y,Z])
% X = 1
% Y = 3
% Z = 2

%?- Y#=X+2, X in 1..2, all_different([X,Y,Z]), [Y,Z] ins 1..3
% X = 1
% Y = 3
% Z = 2

%?- Y#=X+2, [Y,Z] ins 1..3, X in 1..2, all_different([X,Y,Z])
% X = 1
% Y = 3
% Z = 2

%?- Y#=X+2, [Y,Z] ins 1..3, all_different([X,Y,Z]), X in 1..2
% X = 1
% Y = 3
% Z = 2

%?- Y#=X+2, all_different([X,Y,Z]), [Y,Z] ins 1..3, X in 1..2
% X = 1
% Y = 3
% Z = 2

%?- Y#=X+2, all_different([X,Y,Z]), X in 1..2, [Y,Z] ins 1..3
% X = 1
% Y = 3
% Z = 2

%?- all_different([X,Y,Z]), X in 1..2, Y#=X+2, [Y,Z] ins 1..3
% X = 1
% Y = 3
% Z = 2

%?- all_different([X,Y,Z]), X in 1..2, [Y,Z] ins 1..3, Y#=X+2
% X = 1
% Y = 3
% Z = 2

%?- all_different([X,Y,Z]), Y#=X+2, X in 1..2, [Y,Z] ins 1..3
% X = 1
% Y = 3
% Z = 2

%?- all_different([X,Y,Z]), Y#=X+2, [Y,Z] ins 1..3, X in 1..2
% X = 1
% Y = 3
% Z = 2

%?- all_different([X,Y,Z]), [Y,Z] ins 1..3, Y#=X+2, X in 1..2
% X = 1
% Y = 3
% Z = 2

%?- all_different([X,Y,Z]), [Y,Z] ins 1..3, X in 1..2, Y#=X+2
% X = 1
% Y = 3
% Z = 2
