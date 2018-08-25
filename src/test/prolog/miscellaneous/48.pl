x([],[]).
x([X],[X]).
x([X,Y|Ys],[X|Zs]) :- X \= Y, x([Y|Ys],Zs).

y([X],[X]).
y([X,Y|Ys],[X|Zs]) :- X \= Y, y([Y|Ys],Zs).

%QUERY x([a,b], X)
%ANSWER X=[a,b]
%NO

%QUERY y([a,b], X)
%ANSWER X=[a,b]
%NO
