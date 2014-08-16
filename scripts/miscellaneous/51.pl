a(Z) :- write(Z), nl, X is Z + 1, b(X).

b(Z) :- Z < 7, a(Z).

%QUERY b(2)
%OUTPUT
% 2
% 3
% 4
% 5
% 6
%
%OUTPUT
%NO

w(Z) :- write(Z), nl, X is Z + 1, x(X).
x(Z) :- X is Z + 0.5, z(X).
z(Z) :- Z < 7, w(Z).

%QUERY z(0.25)
%OUTPUT
% 0.25
% 1.75
% 3.25
% 4.75
% 6.25
%
%OUTPUT
%NO

q(Z) :- write('q'), nl, r(Z).
r(Z) :- write('r'), nl, z(Z).

%QUERY q(0.25)
%OUTPUT
% q
% r
% 0.25
% 1.75
% 3.25
% 4.75
% 6.25
%
%OUTPUT
%NO