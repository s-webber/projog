%FAIL X in 7..9, X\=7

%FAIL X in 7..9, 8\=X

%FAIL X in 7..9, X\=9

%?- X in 7..9, X\=6
% X=7..9

%?- X in 7..9, 10\=X
% X=7..9

%FAIL X#=2, Y in 1..3, X\=Y

%FAIL X in 7..9, Y in 9..12, X\=Y

%?- X in 7..9, Y in 10..12, X\=Y
% X=7..9
% Y=10..12
