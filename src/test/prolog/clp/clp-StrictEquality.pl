%FAIL [X,Y] ins 1..3, X==Y

%FAIL X in 1..3, X#=Y, X==Y

%FAIL X#=Y, X in 1..3, X==Y

%FAIL X in 1..3, 2==X

%FAIL X in 1..3, X==2

%FAIL X #=1, 2==X

%FAIL X #=1, X==2

%FAIL X==1, X#=1

%?- [X,Y] ins 1..3, X=Y, X==Y
% X=1..3
% Y=1..3

%?- X in 1..3, X=Y, X==Y
% X=1..3
% Y=1..3

%?- X=Y, X in 1..3, X==Y
% X=1..3
% Y=1..3

%?- X#=1, 1==X
% X=1

%?- X#=1, X==1
% X=1
