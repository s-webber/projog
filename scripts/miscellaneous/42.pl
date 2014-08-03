x([]).
x([[X|Xs]|Ys]) :- writef('%t %t', [X, Xs]), nl, x(Ys).

%QUERY x([[a,b,c],[q,w,e,r,t,y],[x,y,z]])
%OUTPUT
% a [b,c]
% q [w,e,r,t,y]
% x [y,z]
% 
%OUTPUT
%ANSWER/
