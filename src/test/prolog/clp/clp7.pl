%?- [X,Y] ins 1..2, repeat(2), X#=Y, label([X])
% X=1
% Y=1
% X=2
% Y=2
% X=1
% Y=1
% X=2
% Y=2
%NO

%?- repeat(2), X in 1..3, \+ integer(X), label([X])
% X=1
% X=2
% X=3
% X=1
% X=2
% X=3
%NO

%?- X in 1..3, repeat(2), \+ integer(X), label([X])
% X=1
% X=2
% X=3
% X=1
% X=2
% X=3
%NO

p(7).
p(8).
p(9).

%?- X in 1..3, p(Y), label([X]), Z is X*Y
% X=1
% Y=7
% Z=7
% X=2
% Y=7
% Z=14
% X=3
% Y=7
% Z=21
% X=1
% Y=8
% Z=8
% X=2
% Y=8
% Z=16
% X=3
% Y=8
% Z=24
% X=1
% Y=9
% Z=9
% X=2
% Y=9
% Z=18
% X=3
% Y=9
% Z=27
%NO

%?- p(Y), X in 1..3, label([X]), Z is X*Y
% X=1
% Y=7
% Z=7
% X=2
% Y=7
% Z=14
% X=3
% Y=7
% Z=21
% X=1
% Y=8
% Z=8
% X=2
% Y=8
% Z=16
% X=3
% Y=8
% Z=24
% X=1
% Y=9
% Z=9
% X=2
% Y=9
% Z=18
% X=3
% Y=9
% Z=27
%NO
