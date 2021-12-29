x([]).
x([[X|Xs]|Ys]) :- writef('%t %t', [X, Xs]), nl, x(Ys).

%?- x([[a,b,c],[q,w,e,r,t,y],[x,y,z]])
%OUTPUT
%a [b,c]
%q [w,e,r,t,y]
%x [y,z]
%
%OUTPUT
%YES
