a(1).
a(2).
a(3).

b(X, Y, Z) :- Z is X+Y.
b(X, Y, Z) :- Z is X-Y.
b(X, Y, Z) :- Z is X*Y.

c(A,B,C) :- a(A), a(B), b(A,B,C).

test(A,B,C) :- A=5; c(A,B,C).

%TRUE test(2,3,6)

%QUERY test(5,B,C)
%ANSWER
% B = UNINSTANTIATED VARIABLE
% C = UNINSTANTIATED VARIABLE
%ANSWER
%NO

%QUERY test(2,3,X)
%ANSWER X = 5
%ANSWER X = -1
%ANSWER X = 6

%QUERY test(A,B,6)
%ANSWER
% A = 5
% B = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% A = 2
% B = 3
%ANSWER
%ANSWER
% A = 3
% B = 2
%ANSWER
%ANSWER
% A = 3
% B = 3
%ANSWER
%NO

%QUERY test(A,B,C)
%ANSWER
% A = 5
% B = UNINSTANTIATED VARIABLE
% C = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% A = 1
% B = 1
% C = 2
%ANSWER
%ANSWER
% A = 1
% B = 1
% C = 0
%ANSWER
%ANSWER
% A = 1
% B = 1
% C = 1
%ANSWER
%ANSWER
% A = 1
% B = 2
% C = 3
%ANSWER
%ANSWER
% A = 1
% B = 2
% C = -1
%ANSWER
%ANSWER
% A = 1
% B = 2
% C = 2
%ANSWER
%ANSWER
% A = 1
% B = 3
% C = 4
%ANSWER
%ANSWER
% A = 1
% B = 3
% C = -2
%ANSWER
%ANSWER
% A = 1
% B = 3
% C = 3
%ANSWER
%ANSWER
% A = 2
% B = 1
% C = 3
%ANSWER
%ANSWER
% A = 2
% B = 1
% C = 1
%ANSWER
%ANSWER
% A = 2
% B = 1
% C = 2
%ANSWER
%ANSWER
% A = 2
% B = 2
% C = 4
%ANSWER
%ANSWER
% A = 2
% B = 2
% C = 0
%ANSWER
%ANSWER
% A = 2
% B = 2
% C = 4
%ANSWER
%ANSWER
% A = 2
% B = 3
% C = 5
%ANSWER
%ANSWER
% A = 2
% B = 3
% C = -1
%ANSWER
%ANSWER
% A = 2
% B = 3
% C = 6
%ANSWER
%ANSWER
% A = 3
% B = 1
% C = 4
%ANSWER
%ANSWER
% A = 3
% B = 1
% C = 2
%ANSWER
%ANSWER
% A = 3
% B = 1
% C = 3
%ANSWER
%ANSWER
% A = 3
% B = 2
% C = 5
%ANSWER
%ANSWER
% A = 3
% B = 2
% C = 1
%ANSWER
%ANSWER
% A = 3
% B = 2
% C = 6
%ANSWER
%ANSWER
% A = 3
% B = 3
% C = 6
%ANSWER
%ANSWER
% A = 3
% B = 3
% C = 0
%ANSWER
%ANSWER
% A = 3
% B = 3
% C = 9
%ANSWER
