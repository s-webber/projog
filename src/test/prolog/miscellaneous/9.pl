test_repeat(N).
test_repeat(N) :- N > 1, N1 is N-1, test_repeat(N1).

%TRUE_NO test_repeat(1)

%?- test_repeat(2)
%YES
%YES
%NO

%?- test_repeat(3)
%YES
%YES
%YES
%NO

%?- test_repeat(7)
%YES
%YES
%YES
%YES
%YES
%YES
%YES
%NO

% check conjunction backtracks arguments in second clause before retrying first
%?- test_repeat(2), var(X), X is 1
% X=1
% X=1
%NO

%?- test_repeat(2), var(X), X = 1
% X=1
% X=1
%NO
