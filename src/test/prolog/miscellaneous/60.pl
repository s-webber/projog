% first

test_repeat1(N).
test_repeat1(N) :- N > 1, N1 is N-1, test_repeat1(N1).

?-assert(a1(x,y,z)).
?-assert(a1(q,w,e)).
?-assert(a1(g,h,j)).

x1(X,Y,Z) :- test_repeat1(3), a1(X,Y,Z).

y1(X,Y,Z) :- assert(a1(1,3,5)), test_repeat1(3), asserta(a1(2,4,6)), assertz(a1(3,6,9)), a1(X,Y,Z).

z1(X,Y,Z) :- assert(b1(1,2,3)), assert(b1(4,5,6)), assert(b1(7,8,9)), test_repeat1(3), b1(X,Y,Z).

% second

test_repeat2(X) :- X<100.
test_repeat2(X) :- X<1000.
test_repeat2(X) :- X<10000.

?-assert(a2(x,y,z)).
?-assert(a2(q,w,e)).
?-assert(a2(g,h,j)).

x2(X,Y,Z) :- test_repeat2(3), a2(X,Y,Z).

y2(X,Y,Z) :- assert(a2(1,3,5)), test_repeat2(3), asserta(a2(2,4,6)), assertz(a2(3,6,9)), a2(X,Y,Z).

z2(X,Y,Z) :- assert(b2(1,2,3)), assert(b2(4,5,6)), assert(b2(7,8,9)), test_repeat2(3), b2(X,Y,Z).

% third

?-assert(a3(x,y,z)).
?-assert(a3(q,w,e)).
?-assert(a3(g,h,j)).

x3(X,Y,Z) :- repeat(3), a3(X,Y,Z).

y3(X,Y,Z) :- assert(a3(1,3,5)), repeat(3), asserta(a3(2,4,6)), assertz(a3(3,6,9)), a3(X,Y,Z).

z3(X,Y,Z) :- assert(b3(1,2,3)), assert(b3(4,5,6)), assert(b3(7,8,9)), repeat(3), b3(X,Y,Z).

%QUERY assert(q(10,30,50)), asserta(q(20,40,60)), assertz(q(30,60,90)), q(X,Y,Z)
%ANSWER
% X=20
% Y=40
% Z=60
%ANSWER
%ANSWER
% X=10
% Y=30
% Z=50
%ANSWER
%ANSWER
% X=30
% Y=60
% Z=90
%ANSWER

% first

%QUERY 0=0, x1(X,w,Z)
%ANSWER
% X=q
% Z=e
%ANSWER
%ANSWER
% X=q
% Z=e
%ANSWER
%ANSWER
% X=q
% Z=e
%ANSWER
%NO

%QUERY 1=1, x1(X,Y,Z)
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%NO

%QUERY 2=2, y1(X,Y,Z)
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%NO

%QUERY 3=3, z1(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%NO

%QUERY 4=4, z1(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%NO

%TRUE retractall(b1(_,_,_))

%QUERY 5=5, z1(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%NO

%TRUE retractall(a1(1,_,_))
%TRUE retractall(a1(2,_,_))
%TRUE retractall(a1(3,_,_))
%TRUE retractall(a1(x,y,z))

%QUERY 6=6, x1(X,Y,Z)
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%NO

%TRUE retractall(a1(_,h,j))

%QUERY 7=7, x1(X,Y,Z)
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%NO

%TRUE retractall(a1(q,w,e))

%FALSE 8=8, x1(X,Y,Z)

% second

%QUERY 0=0, x2(X,w,Z)
%ANSWER
% X=q
% Z=e
%ANSWER
%ANSWER
% X=q
% Z=e
%ANSWER
%ANSWER
% X=q
% Z=e
%ANSWER
%NO

%QUERY 1=1, x2(X,Y,Z)
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER

%QUERY 2=2, y2(X,Y,Z)
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER

%QUERY 3=3, z2(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER

%QUERY 4=4, z2(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER

%TRUE retractall(b2(_,_,_))

%QUERY 5=5, z2(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER

%TRUE retractall(a2(1,_,_))
%TRUE retractall(a2(2,_,_))
%TRUE retractall(a2(3,_,_))
%TRUE retractall(a2(x,y,z))

%QUERY 6=6, x2(X,Y,Z)
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER

%TRUE retractall(a2(_,h,j))

%QUERY 7=7, x2(X,Y,Z)
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER

%TRUE retractall(a2(q,w,e))

%FALSE 8=8, x2(X,Y,Z)

% third

%QUERY 0=0, x3(X,w,Z)
%ANSWER
% X=q
% Z=e
%ANSWER
%ANSWER
% X=q
% Z=e
%ANSWER
%ANSWER
% X=q
% Z=e
%ANSWER
%NO

%QUERY 1=1, x3(X,Y,Z)
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER

%QUERY 2=2, y3(X,Y,Z)
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=2
% Y=4
% Z=6
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=1
% Y=3
% Z=5
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER
%ANSWER
% X=3
% Y=6
% Z=9
%ANSWER

%QUERY 3=3, z3(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER

%QUERY 4=4, z3(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER

%TRUE retractall(b3(_,_,_))

%QUERY 5=5, z3(X,Y,Z)
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=4
% Y=5
% Z=6
%ANSWER
%ANSWER
% X=7
% Y=8
% Z=9
%ANSWER

%TRUE retractall(a3(1,_,_))
%TRUE retractall(a3(2,_,_))
%TRUE retractall(a3(3,_,_))
%TRUE retractall(a3(x,y,z))

%QUERY 6=6, x3(X,Y,Z)
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=g
% Y=h
% Z=j
%ANSWER

%TRUE retractall(a3(_,h,j))

%QUERY 7=7, x3(X,Y,Z)
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER
%ANSWER
% X=q
% Y=w
% Z=e
%ANSWER

%TRUE retractall(a3(q,w,e))

%FALSE 8=8, x3(X,Y,Z)
