% same predicate name included twice in conjunction of implication
%QUERY a(X,Y)
%ANSWER
% X=q
% Y=e
%ANSWER
%ANSWER
% X=r
% Y=e
%ANSWER
%NO
w(q,i).
w(r,i).
w(e,o).
a(X,T) :- w(X,i), w(T,o).
