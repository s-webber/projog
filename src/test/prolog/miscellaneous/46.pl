x(V) :- V = [a|_].
x(V) :- V = [_|b].
x(V) :- V = [a|b].
x(V) :- V = [X/Y|_].
x(V) :- V = [_|p(X,Y)].

%?- x(W)
% W=[a|_]
% W=[_|b]
% W=[a|b]
% W=[X / Y|_]
% W=[_|p(X, Y)]

y :- [a|b] = [a|a].
y :- [b|a] = [a|a].
y :- [a|a] = [b|a].
y :- [a|a] = [a|b].
y :- [b|a] = [a|b].
%FAIL y

z :- [a|b] = [a|b].
%TRUE z
