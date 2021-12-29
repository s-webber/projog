x([],[]).
x([X],[X]).
x([X,Y|Ys],[X|Zs]) :- X \= Y, x([Y|Ys],Zs).

y([X],[X]).
y([X,Y|Ys],[X|Zs]) :- X \= Y, y([Y|Ys],Zs).

%?- x([a,b], X)
% X=[a,b]
%NO

%?- y([a,b], X)
% X=[a,b]
%NO
