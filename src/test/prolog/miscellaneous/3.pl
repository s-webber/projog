% same predicate name included twice in conjunction of implication
%?- a(X,Y)
% X=q
% Y=e
% X=r
% Y=e

w(q,i).
w(r,i).
w(e,o).
a(X,T) :- w(X,i), w(T,o).
