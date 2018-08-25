x(V) :- V = [a|_].
x(V) :- V = [_|b].
x(V) :- V = [a|b].
x(V) :- V = [X/Y|_].
x(V) :- V = [_|p(X,Y)].

%QUERY x(W)
%ANSWER W=[a|_]
%ANSWER W=[_|b]
%ANSWER W=[a|b]
%ANSWER W=[X / Y|_]
%ANSWER W=[_|p(X, Y)]

y :- [a|b] = [a|a].
y :- [b|a] = [a|a].
y :- [a|a] = [b|a].
y :- [a|a] = [a|b].
y :- [b|a] = [a|b].
%FALSE y

z :- [a|b] = [a|b].
%TRUE z