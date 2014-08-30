x([X], X).
x([X|Xs], Y) :- x(Xs, Y).

%QUERY x([a,b,c], Y)
%ANSWER Y=c
%NO 
%QUERY x([a,b,c], c)
%ANSWER/
%NO
%FALSE x([a,b,c], a)
%FALSE x([a,b,c], b)
%FALSE x([a,b,c], d)
%FALSE x([], Y)
%FALSE x(c, Y)