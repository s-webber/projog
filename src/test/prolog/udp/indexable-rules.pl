% argument indexing avoids trying to find alternative solutions
% first argument of p/3 is indexable as all values in that position are immutable

p(a, X, Y) :- Y is X+1.
p(b, X, Y) :- Y is X-1.
p(c, X, Y) :- Y is X*2.

%?- p(a,5,Y)
% Y=6

%?- p(b,5,Y)
% Y=4

%?- p(c,5,Y)
% Y=10

%FAIL p(z,5,Y)

%?- p(X,5,Y)
% X=a
% Y=6
% X=b
% Y=4
% X=c
% Y=10

