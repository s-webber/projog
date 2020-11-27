% argument indexing avoids trying to find alternative solutions
% first argument of p/3 is indexable as all values in that position are immutable

p(a, X, Y) :- Y is X+1.
p(b, X, Y) :- Y is X-1.
p(c, X, Y) :- Y is X*2.

%QUERY p(a,5,Y)
%ANSWER Y=6

%QUERY p(b,5,Y)
%ANSWER Y=4

%QUERY p(c,5,Y)
%ANSWER Y=10

%FALSE p(z,5,Y)

%QUERY p(X,5,Y)
%ANSWER
% X=a
% Y=6
%ANSWER
%ANSWER
% X=b
% Y=4
%ANSWER
%ANSWER
% X=c
% Y=10
%ANSWER

