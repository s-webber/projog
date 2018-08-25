p(1).
p(2).
p(3).
p(4).
p(5).

test(V) :- p(T), V is T*T.

%QUERY test(X)
%ANSWER X=1
%ANSWER X=4
%ANSWER X=9
%ANSWER X=16
%ANSWER X=25