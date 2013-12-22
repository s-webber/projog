:- member(X,[X|Xs]).
member(X,[a|Ys]) :- member(X,Ys).

% %TRUE_NO% member(b, [b,c,d,e])
% %TRUE_NO% member(b, [a,b,c,d,e])
% %TRUE_NO% member(b, [a,a,a,a,a,a,a,a,a,b,c,d,e])
% %FALSE% member(b, [a,a,a,a,a,a,a,a,z,b,c,d,e])
