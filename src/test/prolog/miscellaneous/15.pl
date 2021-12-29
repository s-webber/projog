append2([],Ys,Ys) :-  true.
append1([X|Xs],Ys,[X|Zs]) :- append2(Xs,Ys,Zs).

%?- append1([X],[p(X)],[a,Y])
% X=a
% Y=p(a)
