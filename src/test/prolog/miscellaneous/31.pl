a :- true, !, true, repeat(3).
a :- true.

%?- a
%YES
%YES
%YES
%NO

b :- true, repeat(3), !, repeat(3).
b :- true.

%?- b
%YES
%YES
%YES
%NO

c1 :- true, true, !.
c1 :- true.

c2 :- true, !, true.
c2 :- true.

c3 :- !, true, true.
c3 :- true.

c4 :- !, true, !, true.
c4 :- true.

c5 :- true, !, true, !.
c5 :- true.

c6 :- !, true, true, !.
c6 :- true.

c7 :- !, true, !, true, !.
c7 :- true.

c8 :- true, !.
c8 :- true.

c9 :- !, true.
c9 :- true.

c10 :- !.
c10 :- true.

%TRUE c1
%TRUE c2
%TRUE c3
%TRUE c4
%TRUE c5
%TRUE c6
%TRUE c7
%TRUE c8
%TRUE c9
%TRUE c10

d :- true, 1>2, !.
d :- true.

%TRUE d

e :- true, true, !, 1>2.
e :- true.

%FAIL e

f :- true, fail, !, 1>2.
f :- true.

%TRUE f
