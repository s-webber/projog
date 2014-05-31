writeAndRepeat(N) :- write(N), nl.
writeAndRepeat(N) :- N > 1, N1 is N-1, writeAndRepeat(N1).

%QUERY writeAndRepeat(0)
%OUTPUT
% 0
%
%OUTPUT
%ANSWER/
%NO

%QUERY writeAndRepeat(1)
%OUTPUT
% 1
%
%OUTPUT
%ANSWER/
%NO

%QUERY writeAndRepeat(3)
%OUTPUT
% 3
%
%OUTPUT
%ANSWER/
%OUTPUT
% 2
%
%OUTPUT
%ANSWER/
%OUTPUT
% 1
%
%OUTPUT
%ANSWER/
%NO