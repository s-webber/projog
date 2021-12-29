writeAndRepeat(N) :- write(N), nl.
writeAndRepeat(N) :- N > 1, N1 is N-1, writeAndRepeat(N1).

%?- writeAndRepeat(0)
%OUTPUT
%0
%
%OUTPUT
%YES
%NO

%?- writeAndRepeat(1)
%OUTPUT
%1
%
%OUTPUT
%YES
%NO

%?- writeAndRepeat(3)
%OUTPUT
%3
%
%OUTPUT
%YES
%OUTPUT
%2
%
%OUTPUT
%YES
%OUTPUT
%1
%
%OUTPUT
%YES
%NO
