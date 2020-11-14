p(p(1)).
p(p(2)).
p(p(3)).
x(L) :- L = p(X), p(L).

%QUERY x(A)
%ANSWER A=p(1)
%ANSWER A=p(2)
%ANSWER A=p(3)

%TRUE x(p(1))
%TRUE x(p(2))
%TRUE x(p(3))

%FALSE x(p(4))
