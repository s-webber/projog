memberoflist(X,[X|Xs]).
memberoflist(X,[a|Ys]) :- memberoflist(X,Ys).

%TRUE_NO memberoflist(b, [b,c,d,e])
%TRUE_NO memberoflist(b, [a,b,c,d,e])
%TRUE_NO memberoflist(b, [a,a,a,a,a,a,a,a,a,b,c,d,e])
%FALSE memberoflist(b, [a,a,a,a,a,a,a,a,z,b,c,d,e])
