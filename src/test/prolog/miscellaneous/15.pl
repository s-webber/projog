append2([],Ys,Ys) :-  true.
append1([X|Xs],Ys,[X|Zs]) :- append2(Xs,Ys,Zs).

%QUERY append1([X],[p(X)],[a,Y])
%ANSWER
% X=a
% Y=p(a)
%ANSWER