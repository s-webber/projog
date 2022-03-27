%?- [X,Y,Z] ins 7..10, Y#<X, Z#>X+1
% X=8
% Y=7
% Z=10

%FAIL [X,Y,Z] ins 7..10, Y#<X, Z#>X+2

%?- X in 7..9, Y#=X+2, X#=7
% X = 7
% Y = 9

%?- X in 7..9, X#=7, Y#=X+2
% X = 7
% Y = 9

%?- Y#=X+2, X in 7..9, X#=7
% X = 7
% Y = 9

%?- Y#=X+2, X#=7, X in 7..9
% X = 7
% Y = 9

%?- X#=7, Y#=X+2, X in 7..9
% X = 7
% Y = 9

%?- X#=7, X in 7..9, Y#=X+2
% X = 7
% Y = 9

%?- [X,Y] in 7..9, Y#=X+2
% X = 7
% Y = 9

%?- Y#=X+2, [X,Y] in 7..9
% X = 7
% Y = 9

%?- [X,Y] in 7..9, Y-2#=X
% X = 7
% Y = 9

%?- Y-2#=X, [X,Y] in 7..9
% X = 7
% Y = 9

%FAIL X in 7..9, Y#=X+2, X#=6
%FAIL X in 7..9, X#=6, Y#=X+2
%FAIL Y#=X+2, X in 7..9, X#=6
%FAIL Y#=X+2, X#=6, X in 7..9
%FAIL X#=6, Y#=X+2, X in 7..9
%FAIL X#=6, X in 7..9, Y#=X+2
%FAIL [X,Y] ins 7..9, Y#=X+2, X#=8
%FAIL [X,Y] ins 7..9, X#=8, Y#=X+2
%FAIL Y#=X+2, [X,Y] ins 7..9, X#=8
%FAIL Y#=X+2, X#=8, [X,Y] ins 7..9
%FAIL X#=8, Y#=X+2, [X,Y] ins 7..9
%FAIL X#=8, [X,Y] ins 7..9, Y#=X+2
