p(1).
p(2).
p(3).
p(4).
p(5).

test(V) :- p(T), V is T*T.

%?- test(X)
% X=1
% X=4
% X=9
% X=16
% X=25
