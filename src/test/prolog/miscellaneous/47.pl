x(X,L) :- L = [X|_].
x(X,L) :- L = [_|X].
x(X,L) :- L = [X|X].

%?- x(a,L)
% L=[a|_]
% L=[_|a]
% L=[a|a]

%?- x(a,[X,b])
% X=a
%NO

%?- x(X,[a|a])
% X=a
% X=a
% X=a

%FAIL x(a,[b|c])
