x([[X|Xs]|Ys]) :- 1<2, writef('1 %t %t %t', [X, Xs, Ys]).
x([[X|Xs]|Ys]) :- 1=2, writef('2 %t %t %t', [X, Xs, Ys]).
x([[X|Xs]|Ys]) :- 1=<2, writef('3 %t %t %t', [X, Xs, Ys]).

%?- x([[x,y,z],b,c,d])
%OUTPUT 1 x [y,z] [b,c,d]
%YES
%OUTPUT 3 x [y,z] [b,c,d]
%YES
