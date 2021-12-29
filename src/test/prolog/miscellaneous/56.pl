geven(0).
geven(X) :- godd(Y), X is Y+1, X>0.
godd(1).
godd(X) :- geven(Y), X is Y+1, X>1.

%?- geven(X), !
% X=0
%NO

%?- godd(X), !
% X=1
%NO

%?- geven(X), X>100, !
% X=102
%NO

%?- godd(X), X>100, !
% X=101
%NO

%?- geven(X), write(X), nl, X>10, !
%OUTPUT
%0
%2
%4
%6
%8
%10
%12
%
%OUTPUT
% X=12
%NO

%?- godd(X), write(X), nl, X>10, !
%OUTPUT
%1
%3
%5
%7
%9
%11
%
%OUTPUT
% X=11
%NO
