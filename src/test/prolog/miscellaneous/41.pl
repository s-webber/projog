x([X], X).
x([X|Xs], Y) :- x(Xs, Y).

%?- x([a,b,c], Y)
% Y=c
%NO 
%?- x([a,b,c], c)
%YES
%NO
%FAIL x([a,b,c], a)
%FAIL x([a,b,c], b)
%FAIL x([a,b,c], d)
%FAIL x([], Y)
%FAIL x(c, Y)
