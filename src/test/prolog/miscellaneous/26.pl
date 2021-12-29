p(p(1)).
p(p(2)).
p(p(3)).
x(L) :- L = p(X), p(L).

%?- x(A)
% A=p(1)
% A=p(2)
% A=p(3)

%TRUE x(p(1))
%TRUE x(p(2))
%TRUE x(p(3))

%FAIL x(p(4))
