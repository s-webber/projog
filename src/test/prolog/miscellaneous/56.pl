geven(0).
geven(X) :- godd(Y), X is Y+1, X>0.
godd(1).
godd(X) :- geven(Y), X is Y+1, X>1.

%QUERY geven(X), !
%ANSWER X=0
%NO

%QUERY godd(X), !
%ANSWER X=1
%NO

%QUERY geven(X), X>100, !
%ANSWER X=102
%NO

%QUERY godd(X), X>100, !
%ANSWER X=101
%NO

%QUERY geven(X), write(X), nl, X>10, !
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
%ANSWER X=12
%NO

%QUERY godd(X), write(X), nl, X>10, !
%OUTPUT
%1
%3
%5
%7
%9
%11
%
%OUTPUT
%ANSWER X=11
%NO
