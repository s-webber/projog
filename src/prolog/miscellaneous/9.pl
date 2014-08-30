test_repeat(N).
test_repeat(N) :- N > 1, N1 is N-1, test_repeat(N1).

%TRUE_NO test_repeat(1)

%QUERY test_repeat(2)
%ANSWER/
%ANSWER/
%NO

%QUERY test_repeat(3)
%ANSWER/
%ANSWER/
%ANSWER/
%NO

%QUERY test_repeat(7)
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%NO

% check conjunction backtracks arguments in second clause before retrying first
%QUERY test_repeat(2), var(X), X is 1
%ANSWER X=1
%ANSWER X=1
%NO

%QUERY test_repeat(2), var(X), X = 1
%ANSWER X=1
%ANSWER X=1
%NO