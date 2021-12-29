y0(X) :- X > 5, !.
y0(X) :- (true,!;fail), write(X), X1 is X+1, y0(X1).

%?- y0(1)
%OUTPUT 12345
%YES
%NO

y1(X, Coll) :- X > 5, !.
y1(X, Coll) :- 
  (memberchk(x(X),Coll), !, write(X); write(' ')),
  X1 is X+1, y1(X1,Coll).  

%?- y1(1, [x(0), x(1), x(2), x(4), x(5), x(6)])
%OUTPUT 12 45
%YES
%NO

y2(X, Coll) :- X > 5, !.
y2(X, Coll) :- 
  true, (memberchk(x(X),Coll), !, write(X); write(' ')),
  X1 is X+1, y2(X1,Coll).

%?- y2(1, [x(0), x(1), x(2), x(4), x(5), x(6)])
%OUTPUT 12 45
%YES
%NO

y3(X, Coll) :- X > 5 , !.
y3(X, Coll) :- memberchk(x(X), Coll) , ! , write(X) ; write(' ') , X1 is X + 1 , y3(X1, Coll).

%?- y3(1, [x(0), x(1), x(2), x(4), x(5), x(6)])
%OUTPUT 1
%YES
%NO
