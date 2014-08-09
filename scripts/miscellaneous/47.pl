x(X,L) :- L = [X|_].
x(X,L) :- L = [_|X].
x(X,L) :- L = [X|X].

%QUERY x(a,L)
%ANSWER L=[a|_]
%ANSWER L=[_|a]
%ANSWER L=[a|a]

%QUERY x(a,[X,b])
%ANSWER X=a
%NO

%QUERY x(X,[a|a])
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a

%FALSE x(a,[b|c])